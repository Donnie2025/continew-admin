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

import top.continew.admin.common.model.resp.BaseResp;
import java.io.Serial;
import java.time.*;
import java.math.BigDecimal;

/**
 * 订单信息
 *
 * @author don
 * @since 2025/05/10 22:11
 */
@Data
@Schema(description = "订单信息")
public class TransactionResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 学生会员卡绑定表ID
     */
    @Schema(description = "学生会员卡绑定表ID")
    private Long stuCardId;

    /**
     * 学生ID
     */
    @Schema(description = "学生ID")
    private Long stuId;

    /**
     * 学生姓名
     */
    @Schema(description = "学生姓名")
    private String stuName;

    /**
     * 会员卡ID
     */
    @Schema(description = "会员卡ID")
    private Long cardId;

    /**
     * 会员卡标题
     */
    @Schema(description = "会员卡标题")
    private String cardTitle;

    /**
     * 变动类型（credit:充值, debit:扣费, freeze:冻结, activate:激活, cancel:取消约课, bind:首次绑卡, book_debit:约课扣费）
     */
    @Schema(description = "变动类型（credit:充值, debit:扣费, freeze:冻结, activate:激活, cancel:取消约课, bind:首次绑卡, book_debit:约课扣费）")
    private String type;

    /**
     * 借贷方向：C=Credit 入账/增加余额，D=Debit 出账/减少余额
     */
    @Schema(description = "借贷方向：C=Credit 入账/增加余额，D=Debit 出账/减少余额")
    private String direction;

    /**
     * 课时变动数量（始终为正数，direction 标明方向）
     */
    @Schema(description = "课时变动数量")
    private BigDecimal amount;

    /**
     * 支出金额（扣款）
     */
    @Schema(description = "支出金额（扣款）")
    private BigDecimal debitAmount;

    /**
     * 收入金额（充值/收入）
     */
    @Schema(description = "收入金额（充值/收入）")
    private BigDecimal creditAmount;

    /**
     * 减少有效期天数
     */
    @Schema(description = "减少有效期天数")
    private Integer debitDays;

    /**
     * 增加有效期天数
     */
    @Schema(description = "增加有效期天数")
    private Integer creditDays;

    /**
     * 变动前余额/次数
     */
    @Schema(description = "变动前余额/次数")
    private BigDecimal beforeAmount;

    /**
     * 变动后余额/次数
     */
    @Schema(description = "变动后余额/次数")
    private BigDecimal afterAmount;

    /**
     * 实收金额
     */
    @Schema(description = "实收金额")
    private BigDecimal actualAmount;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 操作人姓名
     */
    @Schema(description = "操作人姓名")
    private String operatorName;
}