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

                            // 只处理PDF文件
                            if ("pdf".equalsIgnoreCase(fileType) || fileName.toLowerCase().endsWith(".pdf")) {
                                FeishuFile feishuFile = new FeishuFile();
                                feishuFile.setToken(fileNode.get("token").asText());
                                feishuFile.setName(fileName);
                                feishuFile.setType(fileType);
                                feishuFile.setSize(fileNode.has("size") ? fileNode.get("size").asLong() : 0L);

                                // 获取文件下载链接
                                String downloadUrl = getFileDownloadUrl(feishuFile.getToken());
                                feishuFile.setUrl(downloadUrl);

                                files.add(feishuFile);
                            }
                        }
                    }
                    log.info("成功获取飞书文件夹 {} 下的文件，共 {} 个PDF文件", folderToken, files.size());
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
