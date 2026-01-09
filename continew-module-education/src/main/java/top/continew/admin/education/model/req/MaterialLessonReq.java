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

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;
import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 课节创建或修改参数
 *
 * @author don
 * @since 2025/12/29 21:22
 */
@Data
@Schema(description = "课节创建或修改参数")
public class MaterialLessonReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教材ID
     */
    @Schema(description = "教材ID")
    @NotNull(message = "教材ID不能为空")
    private Long materialId;

    /**
     * 教材名称（冗余字段，格式：name + level）
     */
    @Schema(description = "教材名称（冗余字段，格式：name + level）")
    @Length(max = 150, message = "教材名称（冗余字段，格式：name + level）长度不能超过 {max} 个字符")
    private String materialName;

    /**
     * 课节名字
     */
    @Schema(description = "课节名字")
    @NotBlank(message = "课节名字不能为空")
    @Length(max = 100, message = "课节名字长度不能超过 {max} 个字符")
    private String lessonName;

    /**
     * 状态（1:启用 0:禁用）
     */
    @Schema(description = "状态（1:启用 0:禁用）")
    private Integer status;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private Long createUser;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}