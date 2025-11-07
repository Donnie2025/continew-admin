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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 学生批量导入响应
 *
 * @author don
 * @since 2025/10/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "学生批量导入响应")
public class StudentBatchImportResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 成功数量
     */
    @Schema(description = "成功数量")
    private Integer successCount;

    /**
     * 失败数量
     */
    @Schema(description = "失败数量")
    private Integer failureCount;

    /**
     * 失败详情列表
     */
    @Schema(description = "失败详情列表")
    private List<ImportFailureDetail> failures;

    /**
     * 导入失败详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "导入失败详情")
    public static class ImportFailureDetail implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 学生姓名
         */
        @Schema(description = "学生姓名")
        private String studentName;

        /**
         * 手机号码
         */
        @Schema(description = "手机号码")
        private String phone;

        /**
         * 失败原因
         */
        @Schema(description = "失败原因")
        private String reason;
    }
}
