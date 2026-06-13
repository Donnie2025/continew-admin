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

package top.continew.admin.education.model.req.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建支付请求
 *
 * @author don
 * @since 2026/06/05
 */
@Data
@Schema(description = "创建支付请求")
public class CreatePaymentReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    @Schema(description = "订单号", example = "ORD20260605120000001")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    /**
     * 用户OpenId（微信支付使用，可选，后端会从数据库获取）
     */
    @Schema(description = "用户OpenId", example = "oUpF8uMuAJO_M2pxb1Q9zNjWeS6o")
    private String openid;

    /**
     * 支付金额（单位：分）
     */
    @Schema(description = "支付金额（单位：分）", example = "10000")
    @NotNull(message = "支付金额不能为空")
    @Positive(message = "支付金额必须大于0")
    private Integer amount;

    /**
     * 商品描述
     */
    @Schema(description = "商品描述", example = "购买会员卡-VIP年卡")
    @NotBlank(message = "商品描述不能为空")
    private String description;
}
