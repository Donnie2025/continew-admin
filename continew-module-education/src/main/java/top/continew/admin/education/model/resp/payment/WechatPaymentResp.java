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

package top.continew.admin.education.model.resp.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 微信JSAPI支付参数响应
 *
 * @author don
 * @since 2026/06/05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "微信JSAPI支付参数")
public class WechatPaymentResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 时间戳
     */
    @Schema(description = "时间戳", example = "1617183540")
    private String timeStamp;

    /**
     * 随机字符串
     */
    @Schema(description = "随机字符串", example = "5K8264ILTKCH16CQ2502SI8ZNMTM67VS")
    private String nonceStr;

    /**
     * 订单详情扩展字符串
     */
    @Schema(description = "订单详情扩展字符串", example = "prepay_id=wx201410272009395522657a690389285100")
    private String packageValue;

    /**
     * 签名方式
     */
    @Schema(description = "签名方式", example = "RSA")
    private String signType;

    /**
     * 签名
     */
    @Schema(description = "签名")
    private String paySign;

    /**
     * 应用ID（公众号AppId）
     */
    @Schema(description = "应用ID", example = "wx8888888888888888")
    private String appId;
}
