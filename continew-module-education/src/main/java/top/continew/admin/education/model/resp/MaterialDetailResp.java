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

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;
import java.io.Serial;
import java.time.*;

/**
 * 教材详情信息
 *
 * @author don
 * @since 2025/12/29 21:22
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "教材详情信息")
public class MaterialDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 父节点ID（0=根节点）
     */
    @Schema(description = "父节点ID（0=根节点）")
    @ExcelProperty(value = "父节点ID")
    private Long pid;

    /**
     * 节点类型（CATEGORY/BOOK/LEVEL/UNIT/LESSON）
     */
    @Schema(description = "节点类型（CATEGORY/BOOK/LEVEL/UNIT/LESSON）")
    @ExcelProperty(value = "节点类型")
    private String type;

    /**
     * 节点名称
     */
    @Schema(description = "节点名称")
    @ExcelProperty(value = "节点名称")
    private String name;

    /**
     * 编码（BOOK/LEVEL层使用）
     */
    @Schema(description = "编码（BOOK/LEVEL层使用）")
    @ExcelProperty(value = "编码")
    private String code;

    /**
     * 封面图片（BOOK层使用）
     */
    @Schema(description = "封面图片（BOOK层使用）")
    @ExcelProperty(value = "封面图片")
    private String coverImg;

    /**
     * 描述
     */
    @Schema(description = "描述")
    @ExcelProperty(value = "描述")
    private String description;

    /**
     * 课节资源链接（LESSON层使用）
     */
    @Schema(description = "课节资源链接（LESSON层使用）")
    @ExcelProperty(value = "课节链接")
    private String lessonUrl;

    /**
     * ClassIn云盘ID
     */
    @Schema(description = "ClassIn云盘ID")
    @ExcelProperty(value = "ClassIn云盘ID")
    private String cloudId;

    /**
     * ClassIn云盘名称
     */
    @Schema(description = "ClassIn云盘名称")
    @ExcelProperty(value = "ClassIn云盘名称")
    private String cloudName;

    /**
     * 是否前端展示（1:展示 0:不展示）
     */
    @Schema(description = "是否前端展示（1:展示 0:不展示）")
    @ExcelProperty(value = "是否前端展示（1:展示 0:不展示）")
    private Boolean isShow;

    /**
     * 排序
     */
    @Schema(description = "排序")
    @ExcelProperty(value = "排序")
    private Integer sort;

    /**
     * 状态（1:启用 0:禁用）
     */
    @Schema(description = "状态（1:启用 0:禁用）")
    @ExcelProperty(value = "状态（1:启用 0:禁用）")
    private Integer status;
}