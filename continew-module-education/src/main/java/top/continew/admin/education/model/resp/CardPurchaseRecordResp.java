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
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员卡购买记录响应
 *
 * @author don
 * @since 2025/05/09
 */
@Data
@Schema(description = "会员卡购买记录")
public class CardPurchaseRecordResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "会员卡标题")
    private String cardTitle;

    @Schema(description = "本次购买课时数")
    private BigDecimal amount;

    @Schema(description = "本次支付金额")
    private BigDecimal purchasePrice;

    @Schema(description = "交易类型（bind:首次购买 recharge:续费）")
    private String transType;

    @Schema(description = "购买时间")
    private LocalDateTime createTime;

    @Schema(description = "备注")
    private String remark;
}
