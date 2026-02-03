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

package top.continew.admin.education.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 课节飞书导入响应
 *
 * @author continew-org
 * @since 2026-01-01
 */
@Data
@Schema(description = "课节飞书导入响应")
public class MaterialLessonImportResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 导入总数
     */
    @Schema(description = "导入总数")
    private Integer totalCount;

    /**
     * 成功导入数量
     */
    @Schema(description = "成功导入数量")
    private Integer successCount;

    /**
     * 失败导入数量
     */
    @Schema(description = "失败导入数量")
    private Integer failureCount;

    /**
     * 跳过数量（已存在且不覆盖）
     */
    @Schema(description = "跳过数量")
    private Integer skipCount;

    /**
     * 成功导入的课节名称列表
     */
    @Schema(description = "成功导入的课节名称列表")
    private List<String> successLessons;

    /**
     * 失败导入的课节信息列表
     */
    @Schema(description = "失败导入的课节信息列表")
    private List<FailureInfo> failureLessons;

    /**
     * 跳过的课节名称列表
     */
    @Schema(description = "跳过的课节名称列表")
    private List<String> skipLessons;

    /**
     * 失败信息
     */
    @Data
    @Schema(description = "失败信息")
    public static class FailureInfo implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 课节名称
         */
        @Schema(description = "课节名称")
        private String lessonName;

        /**
         * 失败原因
         */
        @Schema(description = "失败原因")
        private String reason;
    }
}
