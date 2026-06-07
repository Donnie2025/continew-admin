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

package top.continew.admin.education.service.payment;

import top.continew.admin.education.model.resp.payment.WechatPaymentResp;

import java.util.Map;

/**
 * 微信支付服务接口
 *
 * @author don
 * @since 2026/06/05
 */
public interface WechatPayService {

    /**
     * 创建JSAPI支付订单
     *
     * @param orderNo     订单号
     * @param openid      用户openid
     * @param amount      金额（单位：分）
     * @param description 商品描述
     * @return 支付参数
     */
    WechatPaymentResp createJsapiOrder(String orderNo, String openid, Integer amount, String description);

    /**
     * 处理支付回调通知
     *
     * @param requestBody 请求体
     * @param headers     请求头
     */
    void handlePaymentNotify(String requestBody, Map<String, String> headers);

    /**
     * 查询订单支付状态
     *
     * @param orderNo 订单号
     * @return 支付状态
     */
    String queryOrderStatus(String orderNo);

    /**
     * 关闭订单
     *
     * @param orderNo 订单号
     */
    void closeOrder(String orderNo);
}
