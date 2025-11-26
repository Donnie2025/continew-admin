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
 * 会员卡绑定请求参数
 *
 * @author don
 * @since 2025/05/10 22:11
 */
@Data
@Schema(description = "会员卡绑定请求参数")
public class StuCardBindReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 学生ID
     */
    @Schema(description = "学生ID")
    @NotNull(message = "学生ID不能为空")
    private Long stuId;

    /**
     * 学生姓名
     */
    @Schema(description = "学生姓名")
    @NotBlank(message = "学生姓名不能为空")
    private String stuName;

    /**
     * 会员卡ID
     */
    @Schema(description = "会员卡ID")
    @NotNull(message = "会员卡ID不能为空")
    private Long cardId;

    /**
     * 会员卡名称
     */
    @Schema(description = "会员卡名称")
    @NotBlank(message = "会员卡名称不能为空")
    private String cardName;

    /**
     * 会员卡类型（TL:次卡有限期 TU:次卡无限期 BL:储蓄卡有限期 BU:储蓄卡无限期）
     */
    @Schema(description = "会员卡类型（TL:次卡有限期 TU:次卡无限期 BL:储蓄卡有限期 BU:储蓄卡无限期）")
    @NotBlank(message = "会员卡类型不能为空")
    private String cardType;

    /**
     * 充值次数
     */
    @Schema(description = "充值次数")
    @NotNull(message = "充值次数不能为空")
    private BigDecimal balance;

    /**
     * 到期日期
     */
    @Schema(description = "到期日期")
    private LocalDate expireDate;

    /**
     * 实收金额
     */
    @Schema(description = "实收金额")
    @NotNull(message = "实收金额不能为空")
    private BigDecimal actualAmount;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @Length(max = 255, message = "备注长度不能超过255")
    private String remark;
}