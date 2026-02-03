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

package top.continew.admin.education.model.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import java.io.Serial;
import java.io.Serializable;

/**
 * 课节飞书导入请求参数
 *
 * @author continew-org
 * @since 2026-01-01
 */
@Data
@Schema(description = "课节飞书导入请求参数")
public class MaterialLessonImportReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教材ID
     */
    @Schema(description = "教材ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "教材ID不能为空")
    private Long materialId;

    /**
     * 飞书文件夹链接
     */
    @Schema(description = "飞书文件夹链接", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://ai.feishu.cn/drive/folder/TEV8fJml3lsaj2dLBOUcxovanvc")
    @NotBlank(message = "飞书文件夹链接不能为空")
    @URL(message = "飞书文件夹链接格式不正确")
    @Length(max = 500, message = "飞书文件夹链接长度不能超过 {max} 个字符")
    private String feishuUrl;

    /**
     * 是否覆盖已存在的课节
     */
    @Schema(description = "是否覆盖已存在的课节", defaultValue = "false")
    private Boolean overwrite = false;

    /**
     * 导入备注
     */
    @Schema(description = "导入备注")
    @Length(max = 500, message = "导入备注长度不能超过 {max} 个字符")
    private String remark;
}
