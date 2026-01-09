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
     * 教材编码
     */
    @Schema(description = "教材编码")
    @NotBlank(message = "教材编码不能为空")
    @Length(max = 50, message = "教材编码长度不能超过 {max} 个字符")
    private String code;

    /**
     * 教材名字
     */
    @Schema(description = "教材名字")
    @NotBlank(message = "教材名字不能为空")
    @Length(max = 100, message = "教材名字长度不能超过 {max} 个字符")
    private String name;

    /**
     * 级别（K1:幼儿园小班 K2:幼儿园中班 K3:幼儿园大班 G1-G12:1-12年级 ADULT:成人）
     */
    @Schema(description = "级别（K1:幼儿园小班 K2:幼儿园中班 K3:幼儿园大班 G1-G12:1-12年级 ADULT:成人）")
    @NotBlank(message = "级别不能为空")
    @Length(max = 50, message = "级别长度不能超过 {max} 个字符")
    private String level;

    /**
     * 分类（CHILDREN:少儿启蒙 TEENAGER:青少年 ADULT:成人教材 COMPREHENSIVE:综合教材 READING:阅读绘本 PHONICS:自然拼读 EXAM:考试教材 GRAMMAR:语法）
     */
    @Schema(description = "分类（CHILDREN:少儿启蒙 TEENAGER:青少年 ADULT:成人教材 COMPREHENSIVE:综合教材 READING:阅读绘本 PHONICS:自然拼读 EXAM:考试教材 GRAMMAR:语法）")
    @NotBlank(message = "分类不能为空")
    @Length(max = 50, message = "分类长度不能超过 {max} 个字符")
    private String category;

    /**
     * 封面图片
     */
    @Schema(description = "封面图片")
    @Length(max = 500, message = "封面图片URL长度不能超过 {max} 个字符")
    private String coverImg;

    /**
     * 教材描述
     */
    @Schema(description = "教材描述")
    @Length(max = 500, message = "教材描述长度不能超过 {max} 个字符")
    private String description;

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