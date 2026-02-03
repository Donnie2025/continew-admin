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

package top.continew.admin.education.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import top.continew.admin.education.config.FeishuConfig;
import top.continew.admin.education.service.FeishuService;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 飞书文件代理控制器
 * 用于代理访问飞书文件，处理认证问题
 */
@Slf4j
@RestController
@RequestMapping("/api/feishu/file")
@RequiredArgsConstructor
public class FeishuFileController {

    private final FeishuService feishuService;
    private final FeishuConfig feishuConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 代理访问飞书文件
     * 
     * @param fileToken 文件令牌
     * @param response  HTTP响应
     */
    @GetMapping("/{fileToken}")
    public void proxyFeishuFile(@PathVariable String fileToken, HttpServletResponse response) {
        try {
            // 获取访问令牌
            String accessToken = feishuService.getAccessToken();
            if (accessToken == null) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("{\"error\": \"无法获取飞书访问令牌\"}");
                return;
            }

            // 构造飞书下载URL
            String downloadUrl = feishuConfig.getBaseUrl() + "/open-apis/drive/v1/files/" + fileToken + "/download";

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 调用飞书API
            ResponseEntity<byte[]> feishuResponse = restTemplate
                .exchange(downloadUrl, HttpMethod.GET, entity, byte[].class);

            if (feishuResponse.getStatusCode() == HttpStatus.OK) {
                // 设置响应头
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "inline; filename=" + fileToken + ".pdf");

                // 写入响应体
                byte[] fileContent = feishuResponse.getBody();
                if (fileContent != null) {
                    response.setContentLength(fileContent.length);
                    try (OutputStream out = response.getOutputStream()) {
                        out.write(fileContent);
                        out.flush();
                    }
                }

                log.info("成功代理飞书文件访问: {}", fileToken);
            } else {
                response.setStatus(feishuResponse.getStatusCode().value());
                response.getWriter().write("{\"error\": \"飞书文件访问失败\"}");
            }

        } catch (Exception e) {
            log.error("代理飞书文件访问失败: {}", fileToken, e);
            try {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.getWriter().write("{\"error\": \"文件访问异常: " + e.getMessage() + "\"}");
            } catch (IOException ioException) {
                log.error("写入错误响应失败", ioException);
            }
        }
    }

    /**
     * 获取文件信息（不下载文件内容）
     * 
     * @param fileToken 文件令牌
     * @return 文件信息
     */
    @GetMapping("/{fileToken}/info")
    public ResponseEntity<String> getFileInfo(@PathVariable String fileToken) {
        try {
            // 这里可以返回文件的基本信息，如文件名、大小等
            return ResponseEntity.ok("{\"fileToken\": \"" + fileToken + "\", \"status\": \"available\"}");
        } catch (Exception e) {
            log.error("获取文件信息失败: {}", fileToken, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"获取文件信息失败\"}");
        }
    }

    /**
     * 测试端点
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("{\"message\": \"飞书文件代理控制器工作正常\"}");
    }
}
