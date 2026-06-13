/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.continew.admin.education.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import top.continew.admin.education.config.FeishuConfig;
import top.continew.admin.education.service.FeishuService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 飞书服务实现
 *
 * @author continew-org
 * @since 2026-01-01
 */
@Slf4j
@Service
public class FeishuServiceImpl implements FeishuService {

    private final FeishuConfig feishuConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public FeishuServiceImpl(FeishuConfig feishuConfig) {
        this.feishuConfig = feishuConfig;
        this.restTemplate = new RestTemplate();
        // 配置支持 PATCH 方法 - 使用 Apache HttpClient5
        this.restTemplate
            .setRequestFactory(new org.springframework.http.client.HttpComponentsClientHttpRequestFactory());
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 获取访问令牌
     */
    @Override
    public String getAccessToken() {
        try {
            String url = feishuConfig.getBaseUrl() + "/open-apis/auth/v3/tenant_access_token/internal";
            log.info("请求飞书访问令牌，URL: {}", url);
            log.info("App ID: {}", feishuConfig.getAppId());

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("app_id", feishuConfig.getAppId());
            requestBody.put("app_secret", feishuConfig.getAppSecret());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                if (jsonNode.get("code").asInt() == 0) {
                    return jsonNode.get("tenant_access_token").asText();
                } else {
                    log.error("获取飞书访问令牌失败: {}", jsonNode.get("msg").asText());
                }
            }
        } catch (Exception e) {
            log.error("获取飞书访问令牌异常", e);
        }
        return null;
    }

    @Override
    public List<FeishuFile> getFolderFiles(String folderToken) {
        List<FeishuFile> files = new ArrayList<>();

        try {
            String accessToken = getAccessToken();
            if (accessToken == null) {
                log.error("无法获取飞书访问令牌");
                return files;
            }
            log.info("成功获取飞书访问令牌: {}", accessToken.substring(0, Math.min(20, accessToken.length())) + "...");

            String url = feishuConfig
                .getBaseUrl() + "/open-apis/drive/v1/files" + "?folder_token=" + folderToken + "&page_size=200" + "&order_by=EditedTime" + "&direction=DESC";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                if (jsonNode.get("code").asInt() == 0) {
                    JsonNode filesNode = jsonNode.get("data").get("files");
                    if (filesNode != null && filesNode.isArray()) {
                        for (JsonNode fileNode : filesNode) {
                            String fileName = fileNode.get("name").asText();
                            String fileType = fileNode.get("type").asText();

                            // 返回所有文件和文件夹（包括PDF、文件夹等）
                            FeishuFile feishuFile = new FeishuFile();
                            feishuFile.setToken(fileNode.get("token").asText());
                            feishuFile.setName(fileName);
                            feishuFile.setType(fileType);
                            feishuFile.setSize(fileNode.has("size") ? fileNode.get("size").asLong() : 0L);

                            // 为文件获取下载链接（文件夹除外）
                            if (!"folder".equalsIgnoreCase(fileType)) {
                                String downloadUrl = getFileDownloadUrl(feishuFile.getToken());
                                feishuFile.setUrl(downloadUrl);
                            }

                            files.add(feishuFile);
                        }
                    }
                    log.info("成功获取飞书文件夹 {} 下的文件，共 {} 个文件/文件夹（包括文件夹、PDF文件等）", folderToken, files.size());
                } else {
                    log.error("获取飞书文件夹文件失败: {}", jsonNode.get("msg").asText());
                }
            }
        } catch (Exception e) {
            log.error("调用飞书API获取文件夹文件失败", e);
        }

        return files;
    }

    @Override
    public String getFileDownloadUrl(String fileToken) {
        // 返回飞书的直接文件链接格式
        // 格式: https://域名.feishu.cn/file/文件token
        String directUrl = feishuConfig.getFileDomain() + "/file/" + fileToken;
        log.info("生成飞书文件直接链接: {}", directUrl);
        return directUrl;
    }

    private static final int FEISHU_CHUNK_SIZE = 4 * 1024 * 1024; // 4MB per chunk
    private static final int FEISHU_MAX_SIMPLE_UPLOAD = 20 * 1024 * 1024; // 20MB
    private static final long FEISHU_MAX_UPLOAD = 100L * 1024 * 1024; // 100MB，超出则跳过飞书上传

    @Override
    public String uploadFile(String folderToken, byte[] fileBytes, String fileName) {
        if (fileBytes.length > FEISHU_MAX_UPLOAD) {
            log.warn("文件 {} 大小 {}MB 超过飞书上传限制，跳过飞书同步", fileName, fileBytes.length / 1024 / 1024);
            return null;
        }
        try {
            String accessToken = getAccessToken();
            if (accessToken == null) {
                log.error("无法获取飞书访问令牌");
                return null;
            }
            if (fileBytes.length > FEISHU_MAX_SIMPLE_UPLOAD) {
                return uploadFileInChunks(accessToken, folderToken, fileBytes, fileName);
            }
            return uploadFileSimple(accessToken, folderToken, fileBytes, fileName);
        } catch (Exception e) {
            log.error("上传文件到飞书异常: {}", fileName, e);
        }
        return null;
    }

    private String uploadFileSimple(String accessToken,
                                    String folderToken,
                                    byte[] fileBytes,
                                    String fileName) throws Exception {
        String url = feishuConfig.getBaseUrl() + "/open-apis/drive/v1/files/upload_all";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        org.springframework.util.LinkedMultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
        body.add("file_name", fileName);
        body.add("parent_type", "explorer");
        body.add("parent_node", folderToken);
        body.add("size", String.valueOf(fileBytes.length));
        body.add("file", new org.springframework.core.io.ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return fileName;
            }
        });
        ResponseEntity<String> response = restTemplate
            .postForEntity(url, new HttpEntity<>(body, headers), String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode json = objectMapper.readTree(response.getBody());
            if (json.get("code").asInt() == 0) {
                String fileToken = json.get("data").get("file_token").asText();
                log.info("成功上传文件到飞书: {} -> {}", fileName, fileToken);
                return fileToken;
            }
            log.error("上传文件到飞书失败: {}", json.get("msg").asText());
        }
        return null;
    }

    private String uploadFileInChunks(String accessToken,
                                      String folderToken,
                                      byte[] fileBytes,
                                      String fileName) throws Exception {
        String baseUrl = feishuConfig.getBaseUrl();
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setBearerAuth(accessToken);
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);

        // 1. prepare
        Map<String, Object> prepareBody = new HashMap<>();
        prepareBody.put("file_name", fileName);
        prepareBody.put("parent_type", "explorer");
        prepareBody.put("parent_node", folderToken);
        prepareBody.put("size", fileBytes.length);
        prepareBody.put("block_size", FEISHU_CHUNK_SIZE);
        log.info("飞书分片上传 prepare 请求: fileName={}, size={}, blockSize={}, folderToken={}", fileName, fileBytes.length, FEISHU_CHUNK_SIZE, folderToken);
        ResponseEntity<String> prepareResp = restTemplate
            .postForEntity(baseUrl + "/open-apis/drive/v1/files/upload_prepare", new HttpEntity<>(prepareBody, jsonHeaders), String.class);
        log.info("飞书分片上传 prepare 响应: {}", prepareResp.getBody());
        JsonNode prepareJson = objectMapper.readTree(prepareResp.getBody());
        if (prepareJson.get("code").asInt() != 0) {
            log.error("飞书分片上传 prepare 失败: {}", prepareJson.get("msg").asText());
            return null;
        }
        String uploadId = prepareJson.get("data").get("upload_id").asText();
        int blockSize = prepareJson.get("data").get("block_size").asInt(FEISHU_CHUNK_SIZE);
        int blockNum = prepareJson.get("data").get("block_num").asInt();

        // 2. upload parts
        for (int i = 0; i < blockNum; i++) {
            int start = i * blockSize;
            int length = Math.min(blockSize, fileBytes.length - start);
            byte[] chunk = java.util.Arrays.copyOfRange(fileBytes, start, start + length);
            HttpHeaders partHeaders = new HttpHeaders();
            partHeaders.setBearerAuth(accessToken);
            partHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
            org.springframework.util.LinkedMultiValueMap<String, Object> partBody = new org.springframework.util.LinkedMultiValueMap<>();
            partBody.add("upload_id", uploadId);
            partBody.add("seq", String.valueOf(i));
            partBody.add("size", String.valueOf(length));
            partBody.add("file", new org.springframework.core.io.ByteArrayResource(chunk) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            });
            restTemplate
                .postForEntity(baseUrl + "/open-apis/drive/v1/files/upload_part", new HttpEntity<>(partBody, partHeaders), String.class);
        }

        // 3. finish
        Map<String, Object> finishBody = new HashMap<>();
        finishBody.put("upload_id", uploadId);
        finishBody.put("block_num", blockNum);
        ResponseEntity<String> finishResp = restTemplate
            .postForEntity(baseUrl + "/open-apis/drive/v1/files/upload_finish", new HttpEntity<>(finishBody, jsonHeaders), String.class);
        JsonNode finishJson = objectMapper.readTree(finishResp.getBody());
        if (finishJson.get("code").asInt() == 0) {
            String fileToken = finishJson.get("data").get("file_token").asText();
            log.info("分片上传成功: {} -> {}", fileName, fileToken);
            return fileToken;
        }
        log.error("飞书分片上传 finish 失败: {}", finishJson.get("msg").asText());
        return null;
    }

    @Override
    public String createFolder(String parentFolderToken, String folderName) {
        try {
            String accessToken = getAccessToken();
            if (accessToken == null) {
                log.error("无法获取飞书访问令牌");
                return null;
            }

            // 如果没有传父文件夹token，使用配置的根文件夹token
            String actualParentToken = (parentFolderToken != null && !parentFolderToken.isEmpty())
                ? parentFolderToken
                : feishuConfig.getRootFolderToken();

            if (actualParentToken == null || actualParentToken.isEmpty()) {
                log.error("飞书父文件夹token为空且未配置根文件夹token");
                return null;
            }

            // 幂等性检查：先查询父文件夹下是否已存在同名文件夹
            // 注意：此处仍然查询飞书API，但仅用于幂等性保证，避免重复创建
            log.info("检查飞书文件夹是否已存在: name={}, parent_token={}", folderName, actualParentToken);
            String existingToken = checkFolderExists(actualParentToken, folderName);
            if (existingToken != null) {
                log.info("飞书文件夹已存在，直接返回: {} -> {}", folderName, existingToken);
                return existingToken;
            }

            // 文件夹不存在，创建新文件夹
            String url = feishuConfig.getBaseUrl() + "/open-apis/drive/v1/files/create_folder";

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", folderName);
            requestBody.put("folder_token", actualParentToken);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            log.info("创建飞书文件夹请求: name={}, folder_token={}", folderName, actualParentToken);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            log.info("创建飞书文件夹响应: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                if (jsonNode.get("code").asInt() == 0) {
                    String folderToken = jsonNode.get("data").get("token").asText();
                    log.info("成功在飞书创建文件夹: {} -> {}", folderName, folderToken);
                    return folderToken;
                } else {
                    log.error("在飞书创建文件夹失败: {}", jsonNode.get("msg").asText());
                }
            }
        } catch (Exception e) {
            log.error("在飞书创建文件夹异常: {}", folderName, e);
        }
        return null;
    }

    /**
     * 检查父文件夹下是否已存在指定名称的子文件夹（用于幂等性保证）
     *
     * @param parentFolderToken 父文件夹token
     * @param folderName        要检查的文件夹名称
     * @return 如果存在返回文件夹token，否则返回null
     */
    private String checkFolderExists(String parentFolderToken, String folderName) {
        try {
            String accessToken = getAccessToken();
            if (accessToken == null) {
                return null;
            }

            String url = feishuConfig
                .getBaseUrl() + "/open-apis/drive/v1/files?folder_token=" + parentFolderToken + "&page_size=200";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                if (jsonNode.get("code").asInt() == 0) {
                    JsonNode filesNode = jsonNode.get("data").get("files");
                    if (filesNode != null && filesNode.isArray()) {
                        for (JsonNode fileNode : filesNode) {
                            String type = fileNode.get("type").asText();
                            String name = fileNode.get("name").asText();
                            // 找到同名文件夹
                            if ("folder".equals(type) && folderName.equals(name)) {
                                String token = fileNode.get("token").asText();
                                log.info("在飞书找到已存在的文件夹: {} -> {}", folderName, token);
                                return token;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("检查飞书文件夹是否存在时出错: {}", folderName, e);
        }
        return null;
    }

    @Override
    public boolean deleteFolder(String folderToken, String type) {
        try {
            String accessToken = getAccessToken();
            if (accessToken == null) {
                return false;
            }
            String url = feishuConfig.getBaseUrl() + "/open-apis/drive/v1/files/" + folderToken + "?type=" + type;
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return jsonNode.get("code").asInt() == 0;
            }
        } catch (Exception e) {
            log.error("删除飞书文件夹异常: folderToken={}", folderToken, e);
        }
        return false;
    }

    @Override
    public boolean renameFile(String fileToken, String newName) {
        try {
            String accessToken = getAccessToken();
            if (accessToken == null) {
                log.error("无法获取飞书访问令牌");
                return false;
            }

            String url = feishuConfig.getBaseUrl() + "/open-apis/drive/v1/files/" + fileToken + "?type=folder";

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", newName);

            String bodyJson = objectMapper.writeValueAsString(requestBody);
            log.info("重命名飞书文件请求体: {}", bodyJson);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            log.info("重命名飞书文件请求: fileToken={}, newName={}", fileToken, newName);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class);

            log.info("重命名飞书文件响应: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                if (jsonNode.get("code").asInt() == 0) {
                    log.info("成功重命名飞书文件: {} -> {}", fileToken, newName);
                    return true;
                } else {
                    log.error("重命名飞书文件失败: {}", jsonNode.get("msg").asText());
                }
            }
        } catch (Exception e) {
            log.error("重命名飞书文件异常: fileToken={}, newName={}", fileToken, newName, e);
        }
        return false;
    }

    /**
     * 已废弃：不再查找子文件夹逻辑
     * 策略调整：完全信任数据库中的feishuFolderToken
     * 如果数据库中已有token，飞书端不再重复创建文件夹，避免重复和不一致问题
     *
     * @deprecated 此方法已废弃，请直接使用数据库中的feishuFolderToken
     */
    @Deprecated
    @Override
    public String findSubFolder(String parentFolderToken, String folderName) {
        log.warn("调用了已废弃的findSubFolder方法，父文件夹token: {}, 文件夹名称: {}", parentFolderToken, folderName);
        log.warn("请改用数据库中存储的feishuFolderToken，不要在飞书端重复查找或创建文件夹");
        // 返回null，表示不再执行查找逻辑
        return null;
    }

    /**
     * 从飞书分享链接中提取文件夹token
     */
    public String extractFolderTokenFromUrl(String shareUrl) {
        if (shareUrl == null || shareUrl.isEmpty()) {
            return null;
        }

        // 从URL中提取文件夹token
        String[] parts = shareUrl.split("/");
        if (parts.length > 0) {
            return parts[parts.length - 1];
        }

        return null;
    }
}
