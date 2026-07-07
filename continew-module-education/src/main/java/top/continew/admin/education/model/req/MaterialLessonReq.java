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
     * 教材ID（映射到MaterialDO.pid）
     */
    @Schema(description = "教材ID")
    @NotNull(message = "教材ID不能为空")
    private Long materialId;

    /**
     * 课节名字（映射到MaterialDO.name）
     */
    @Schema(description = "课节名字")
    @NotBlank(message = "课节名字不能为空")
    @Length(max = 100, message = "课节名字长度不能超过 {max} 个字符")
    private String lessonName;

    /**
     * 课节链接（映射到MaterialDO.lessonUrl）
     */
    @Schema(description = "课节链接")
    @Length(max = 500, message = "课节链接长度不能超过 {max} 个字符")
    private String lessonUrl;

    /**
     * 节点类型（自动设置为LESSON）
     */
    @Schema(description = "节点类型", hidden = true)
    private String type;

    /**
     * 教材名称（虚拟字段，不映射到数据库）
     */
    @Schema(description = "教材名称", hidden = true)
    @Length(max = 150, message = "教材名称长度不能超过 {max} 个字符")
    private String materialName;

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