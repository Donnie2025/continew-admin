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

package top.continew.admin.education.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 支付渠道实体
 *
 * @author don
 * @since 2026/06/09
 */
@Data
@TableName("edu_payment_channel")
public class PaymentChannelDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 渠道编码（alipay-支付宝 wechat-微信）
     */
    private String channelCode;

    /**
     * 渠道名称（支付宝、微信）
     */
    private String channelName;

    /**
     * 支付类型（online-在线支付 qrcode-扫码支付 offline-线下支付）
     */
    private String paymentType;

    /**
     * 收款二维码图片地址（仅支付类型为qrcode时有值）
     */
    private String qrcodeImage;

    /**
     * 支付说明
     */
    private String description;

    /**
     * 排序字段，值越小排序越靠前
     */
    private Integer sort;

    /**
     * 状态（1：启用；2：禁用）
     */
    private Integer status;
}
