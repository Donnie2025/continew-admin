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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.education.config.FeishuConfig;
import top.continew.admin.education.service.FeishuService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 飞书测试控制器（仅用于测试，生产环境请删除）
 *
 * @author continew-org
 * @since 2026-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/test/feishu")
@RequiredArgsConstructor
public class FeishuTestController {

    private final FeishuConfig feishuConfig;
    private final FeishuService feishuService;

    /**
     * 测试飞书配置
     */
    @GetMapping("/config")
    public Map<String, Object> testConfig() {
        Map<String, Object> result = new HashMap<>();
        result.put("appId", feishuConfig.getAppId());
        result.put("baseUrl", feishuConfig.getBaseUrl());
        // 不返回完整的secret，只返回前8位用于验证
        String secret = feishuConfig.getAppSecret();
        result.put("appSecretPrefix", secret != null ? secret.substring(0, 8) + "***" : "null");
        result.put("configLoaded", true);

        log.info("飞书配置测试 - App ID: {}, Base URL: {}", feishuConfig.getAppId(), feishuConfig.getBaseUrl());

        return result;
    }

    /**
     * 测试文件夹链接解析
     */
    @GetMapping("/parse-url")
    public Map<String, Object> testParseUrl(@RequestParam String url) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 使用FeishuServiceImpl的方法解析链接
            top.continew.admin.education.service.impl.FeishuServiceImpl impl = (top.continew.admin.education.service.impl.FeishuServiceImpl)feishuService;
            String folderToken = impl.extractFolderTokenFromUrl(url);

            result.put("originalUrl", url);
            result.put("folderToken", folderToken);
            result.put("success", folderToken != null);

            log.info("链接解析测试 - URL: {}, Token: {}", url, folderToken);
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("success", false);
            log.error("链接解析失败", e);
        }

        return result;
    }

    /**
     * 测试获取文件夹文件（注意：这会调用真实的飞书API）
     */
    @GetMapping("/files")
    public Map<String, Object> testGetFiles(@RequestParam String folderToken) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<FeishuService.FeishuFile> files = feishuService.getFolderFiles(folderToken);

            result.put("folderToken", folderToken);
            result.put("fileCount", files.size());
            result.put("files", files);
            result.put("success", true);

            log.info("文件获取测试 - Token: {}, 文件数: {}", folderToken, files.size());
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("success", false);
            log.error("文件获取失败", e);
        }

        return result;
    }
}
