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

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.common.context.UserContext;
import top.continew.admin.common.satoken.StpMiniUtil;
import top.continew.admin.education.mapper.StudentMapper;
import top.continew.admin.education.model.entity.StudentDO;
import top.continew.admin.education.model.req.payment.CreatePaymentReq;
import top.continew.admin.education.model.resp.payment.WechatPaymentResp;
import top.continew.admin.education.service.payment.WechatPayService;
import top.continew.starter.core.exception.BusinessException;
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
    private final StudentMapper studentMapper;
    private final WxMpService wxMpService;

    /**
     * 创建JSAPI支付订单
     */
    @Operation(summary = "创建JSAPI支付订单", description = "创建微信公众号JSAPI支付订单，返回支付参数")
    @PostMapping("/jsapi")
    public R<WechatPaymentResp> createJsapiOrder(@Validated @RequestBody CreatePaymentReq req) {
        log.info("收到创建JSAPI支付订单请求: {}", req);

        // 验证小程序用户登录
        StpMiniUtil.checkLogin();

        Long userId = StpMiniUtil.getLoginIdAsLong();
        String openid = null;

        // 优先从 UserContext 中获取 openid
        try {
            UserContext userContext = (UserContext)StpMiniUtil.getSession()
                .get(cn.dev33.satoken.session.SaSession.USER);
            if (userContext != null && userContext.getOpenid() != null && !userContext.getOpenid().trim().isEmpty()) {
                openid = userContext.getOpenid();
                log.info("从 UserContext 获取 openid: userId={}, openid={}", userId, openid);
            }
        } catch (Exception e) {
            log.warn("从 UserContext 获取 openid 失败，将从数据库查询: {}", e.getMessage());
        }

        // 如果 UserContext 中没有，从数据库查询并更新 UserContext
        if (openid == null || openid.trim().isEmpty()) {
            log.info("UserContext 中没有 openid，从数据库查询: userId={}", userId);
            StudentDO student = studentMapper.selectById(userId);
            if (student == null) {
                throw new BusinessException("学生信息不存在");
            }

            openid = student.getOpenid();

            // 更新到 UserContext 中，避免下次再查数据库
            if (openid != null && !openid.trim().isEmpty()) {
                try {
                    UserContext userContext = (UserContext)StpMiniUtil.getSession()
                        .get(cn.dev33.satoken.session.SaSession.USER);
                    if (userContext != null) {
                        userContext.setOpenid(openid);
                        StpMiniUtil.getSession().set(cn.dev33.satoken.session.SaSession.USER, userContext);
                        log.info("已将 openid 更新到 UserContext: userId={}, openid={}", userId, openid);
                    }
                } catch (Exception e) {
                    log.warn("更新 openid 到 UserContext 失败: {}", e.getMessage());
                }
            }

            log.info("学生信息 - ID: {}, 姓名: {}, 手机: {}, openid: {}", student.getId(), student.getName(), student
                .getPhone(), openid);
        }

        if (openid == null || openid.trim().isEmpty()) {
            throw new BusinessException("使用微信支付需要先使用微信登录");
        }

        WechatPaymentResp paymentResp = wechatPayService.createJsapiOrder(req.getOrderNo(), openid, req.getAmount(), req
            .getDescription());

        return R.ok(paymentResp);
    }

    /**
     * 获取微信JS接口权限验证签名
     */
    @Operation(summary = "获取微信JS接口签名", description = "获取微信JSAPI权限验证所需的签名")
    @GetMapping("/jsapi/signature")
    @SaIgnore
    public R<Map<String, String>> getJsapiSignature(@Parameter(description = "当前页面URL") @RequestParam String url) {
        log.info("获取JSAPI签名, url: {}", url);

        try {
            me.chanjar.weixin.common.bean.WxJsapiSignature signature = wxMpService.createJsapiSignature(url);

            Map<String, String> result = new HashMap<>();
            result.put("appId", wxMpService.getWxMpConfigStorage().getAppId());
            result.put("noncestr", signature.getNonceStr());
            result.put("timestamp", String.valueOf(signature.getTimestamp()));
            result.put("signature", signature.getSignature());
            result.put("url", url);

            log.info("JSAPI签名生成成功");
            return R.ok(result);

        } catch (Exception e) {
            log.error("获取JSAPI签名失败: {}", e.getMessage(), e);
            throw new BusinessException("获取JSAPI签名失败: " + e.getMessage());
        }
    }

    /**
     * 支付回调通知
     */
    @Operation(summary = "支付回调通知", description = "接收微信支付回调通知")
    @PostMapping("/notify")
    @SaIgnore
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

        // 验证小程序用户登录
        StpMiniUtil.checkLogin();

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

        // 验证小程序用户登录
        StpMiniUtil.checkLogin();

        wechatPayService.closeOrder(orderNo);
        return R.ok();
    }
}
