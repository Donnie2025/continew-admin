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
import java.math.BigDecimal;

/**
 * 订单创建或修改参数
 *
 * @author don
 * @since 2025/11/26 22:05
 */
@Data
@Schema(description = "订单创建或修改参数")
public class OrderReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单金额
     */
    @Schema(description = "订单金额")
    private BigDecimal orderPrice;

    /**
     * 会员卡ID
     */
    @Schema(description = "会员卡ID", example = "1")
    @NotNull(message = "会员卡ID不能为空")
    private Long cardId;

    /**
     * 支付方式（wechat:微信支付, alipay:支付宝）
     */
    @Schema(description = "支付方式（wechat:微信支付, alipay:支付宝）")
    @NotBlank(message = "支付方式（wechat:微信支付, alipay:支付宝）不能为空")
    @Length(max = 20, message = "支付方式（wechat:微信支付, alipay:支付宝）长度不能超过 {max} 个字符")
    private String paymentType;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    @NotNull(message = "创建人不能为空")
    private Long createUser;
}