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
@ConfigurationProperties(prefix = "classin")
public class ClassinProperties {

    /**
     * API配置
     * 注意：AppId 和 AppSecret 配置已迁移至数据库 edu_institution 表
     * 实际运行时从数据库读取，通过 InstitutionService.getActiveInstitution() 获取
     */
    private ApiConfig api;

    /**
     * API配置类
     */
    @Data
    public static class ApiConfig {
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
         * 编辑学生接口路径
         */
        private String editSchoolStudent;

        /**
         * 添加教师接口路径
         */
        private String addTeacher;

        /**
         * 新增课程接口
         */
        private String addCourse;

        /**
         * 编辑课程接口
         */
        private String editCourse;

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
         * 删除活动接口
         */
        private String deleteActivity;

        /**
         * 添加课程教师接口（API v2）
         */
        private String addCourseTeacher;

        /**
         * 移除课程教师接口
         */
        private String removeCourseTeacher;

        /**
         * 添加课程学生接口（单个）
         */
        private String addCourseStudent;

        /**
         * 添加课程学生接口（批量）
         */
        private String addCourseStudentMultiple;

        /**
         * 删除课程学生接口
         */
        private String removeCourseStudent;
    }
}