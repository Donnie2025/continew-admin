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
import java.math.BigDecimal;

/**
 * 会员卡管理创建或修改参数
 *
 * @author don
 * @since 2025/05/10 00:06
 */
@Data
@Schema(description = "会员卡管理创建或修改参数")
public class CardReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会员卡标题
     */
    @Schema(description = "会员卡标题")
    @NotBlank(message = "会员卡标题不能为空")
    @Length(max = 100, message = "会员卡标题长度不能超过 {max} 个字符")
    private String title;

    /**
     * 副标题
     */
    @Schema(description = "副标题")
    @Length(max = 200, message = "副标题长度不能超过 {max} 个字符")
    private String subTitle;

    /**
     * 会员卡描述
     */
    @Schema(description = "会员卡描述")
    @Length(max = 500, message = "会员卡描述长度不能超过 {max} 个字符")
    private String description;

    /**
     * 会员卡类型（TL:次卡有限期 TU:次卡无限期 BL:储蓄卡有限期 BU:储蓄卡无限期）
     */
    @Schema(description = "会员卡类型（TL:次卡有限期 TU:次卡无限期 BL:储蓄卡有限期 BU:储蓄卡无限期）")
    @NotBlank(message = "会员卡类型不能为空")
    @Length(max = 10, message = "会员卡类型长度不能超过 {max} 个字符")
    private String type;

    /**
     * 初始次数
     */
    @Schema(description = "初始次数")
    private Integer initTimes;

    /**
     * 初始有效天数
     */
    @Schema(description = "初始有效天数")
    private Integer initDays;

    /**
     * 初始余额
     */
    @Schema(description = "初始余额")
    private BigDecimal initBalance;

    /**
     * 代理售卖价格
     */
    @Schema(description = "代理售卖价格")
    private BigDecimal price;

    /**
     * 排序字段，值越小排序越靠前
     */
    @Schema(description = "排序字段，值越小排序越靠前")
    private Integer sort;

    /**
     * 所属机构ID
     */
    @Schema(description = "所属机构ID")
    private Long institutionId;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @Length(max = 500, message = "备注长度不能超过 {max} 个字符")
    private String remark;
}