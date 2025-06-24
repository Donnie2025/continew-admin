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
 * ClassIn配置属性
 *
 * @author donnie
 * @since 2025/04/12 20:49
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "classin.api")
public class ClassinProperties {

    /**
     * API基础URL
     */
    private String url;

    /**
     * 注册接口路径
     */
    private String register;

    /**
     * 添加学生接口路径
     */
    private String addSchoolStudent;

    /**
     * 添加教师接口路径
     */
    private String addTeacher;

    /**
     * 新增课程接口
     */
    private String addCourse;

    /**
     * 创建课堂活动接口
     */
    private String createClass;

    /**
     * 编辑课堂活动接口
     */
    private String updateClass;

    /**
     * 创建单元接口
     */
    private String createUnit;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 应用密钥
     */
    private String appSecret;
}