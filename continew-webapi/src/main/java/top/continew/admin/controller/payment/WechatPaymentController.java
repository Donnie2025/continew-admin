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

package top.continew.admin.controller.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.education.model.req.payment.CreatePaymentReq;
import top.continew.admin.education.model.resp.payment.WechatPaymentResp;
import top.continew.admin.education.service.payment.WechatPayService;
import top.continew.starter.web.model.R;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付控制器
 *
 * @author don
 * @since 2026/06/05
 */
@Slf4j
@Tag(name = "微信支付API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment/wechat")
public class WechatPaymentController {

    private final WechatPayService wechatPayService;

    /**
     * 创建JSAPI支付订单
     */
    @Operation(summary = "创建JSAPI支付订单", description = "创建微信公众号JSAPI支付订单，返回支付参数")
    @PostMapping("/jsapi")
    public R<WechatPaymentResp> createJsapiOrder(@Validated @RequestBody CreatePaymentReq req) {
        log.info("收到创建JSAPI支付订单请求: {}", req);

        WechatPaymentResp paymentResp = wechatPayService.createJsapiOrder(req.getOrderNo(), req.getOpenid(), req
            .getAmount(), req.getDescription());

        return R.ok(paymentResp);
    }

    /**
     * 支付回调通知
     */
    @Operation(summary = "支付回调通知", description = "接收微信支付回调通知")
    @PostMapping("/notify")
    public String paymentNotify(@RequestBody String requestBody,
                                @RequestHeader("Wechatpay-Serial") String serial,
                                @RequestHeader("Wechatpay-Nonce") String nonce,
                                @RequestHeader("Wechatpay-Signature") String signature,
                                @RequestHeader("Wechatpay-Timestamp") String timestamp) {
        log.info("收到微信支付回调通知");

        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Wechatpay-Serial", serial);
            headers.put("Wechatpay-Nonce", nonce);
            headers.put("Wechatpay-Signature", signature);
            headers.put("Wechatpay-Timestamp", timestamp);

            wechatPayService.handlePaymentNotify(requestBody, headers);

            // 返回成功响应
            return "{\"code\":\"SUCCESS\",\"message\":\"成功\"}";

        } catch (Exception e) {
            log.error("处理支付回调失败", e);
            return "{\"code\":\"FAIL\",\"message\":\"" + e.getMessage() + "\"}";
        }
    }

    /**
     * 查询订单支付状态
     */
    @Operation(summary = "查询订单支付状态", description = "查询订单在微信支付系统中的状态")
    @GetMapping("/status/{orderNo}")
    public R<String> queryOrderStatus(@Parameter(description = "订单号") @PathVariable String orderNo) {
        log.info("查询订单支付状态, orderNo: {}", orderNo);

        String status = wechatPayService.queryOrderStatus(orderNo);
        return R.ok(status);
    }

    /**
     * 关闭订单
     */
    @Operation(summary = "关闭订单", description = "关闭未支付的订单")
    @PostMapping("/close/{orderNo}")
    public R<Void> closeOrder(@Parameter(description = "订单号") @PathVariable String orderNo) {
        log.info("关闭订单, orderNo: {}", orderNo);

        wechatPayService.closeOrder(orderNo);
        return R.ok();
    }
}
