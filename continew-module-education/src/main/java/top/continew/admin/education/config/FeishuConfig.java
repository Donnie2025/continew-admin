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

package top.continew.admin.education.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 飞书配置
 *
 * @author continew-org
 * @since 2026-01-01
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "feishu")
public class FeishuConfig {

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 应用密钥
     */
    private String appSecret;

    /**
     * 飞书API基础URL
     */
    private String baseUrl = "https://open.feishu.cn";

    /**
     * 飞书文件访问域名
     */
    private String fileDomain = "https://ai.feishu.cn";

    /**
     * 飞书根文件夹token（所有教材文件夹都创建在此根文件夹下）
     */
    private String rootFolderToken;
}
