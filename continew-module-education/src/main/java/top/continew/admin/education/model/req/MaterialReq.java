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
 * 教材创建或修改参数
 *
 * @author don
 * @since 2025/12/29 21:22
 */
@Data
@Schema(description = "教材创建或修改参数")
public class MaterialReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 父节点ID（0=根节点）
     */
    @Schema(description = "父节点ID（0=根节点）")
    private Long pid;

    /**
     * 节点类型（CATEGORY/BOOK/LEVEL/UNIT/LESSON）
     */
    @Schema(description = "节点类型（CATEGORY/BOOK/LEVEL/UNIT/LESSON）")
    @NotBlank(message = "节点类型不能为空")
    @Length(max = 20, message = "节点类型长度不能超过 {max} 个字符")
    private String type;

    /**
     * 节点名称
     */
    @Schema(description = "节点名称")
    @NotBlank(message = "名称不能为空")
    @Length(max = 200, message = "名称长度不能超过 {max} 个字符")
    private String name;

    /**
     * 编码（BOOK/LEVEL层使用）
     */
    @Schema(description = "编码（BOOK/LEVEL层使用）")
    @Length(max = 50, message = "编码长度不能超过 {max} 个字符")
    private String code;

    /**
     * 封面图片（BOOK层使用）
     */
    @Schema(description = "封面图片（BOOK层使用）")
    @Length(max = 500, message = "封面图片URL长度不能超过 {max} 个字符")
    private String coverImg;

    /**
     * 描述
     */
    @Schema(description = "描述")
    @Length(max = 1000, message = "描述长度不能超过 {max} 个字符")
    private String description;

    /**
     * 课节资源链接（LESSON层使用）
     */
    @Schema(description = "课节资源链接（LESSON层使用）")
    @Length(max = 500, message = "课节链接长度不能超过 {max} 个字符")
    private String lessonUrl;

    /**
     * ClassIn云盘ID
     */
    @Schema(description = "ClassIn云盘ID")
    @Length(max = 100, message = "ClassIn云盘ID长度不能超过 {max} 个字符")
    private String cloudId;

    /**
     * ClassIn云盘名称
     */
    @Schema(description = "ClassIn云盘名称")
    @Length(max = 200, message = "ClassIn云盘名称长度不能超过 {max} 个字符")
    private String cloudName;

    /**
     * 是否前端展示（1:展示 0:不展示）
     */
    @Schema(description = "是否前端展示（1:展示 0:不展示）")
    @NotNull(message = "是否前端展示不能为空")
    private Boolean isShow;

    /**
     * 排序
     */
    @Schema(description = "排序")
    @NotNull(message = "排序不能为空")
    @Min(value = 0, message = "排序值不能小于0")
    @Max(value = 9999, message = "排序值不能大于9999")
    private Integer sort;

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