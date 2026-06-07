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

package top.continew.admin.education.service.payment.impl;

import cn.hutool.core.util.StrUtil;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.education.config.WechatMpConfig;
import top.continew.admin.education.config.WechatPayConfig;
import top.continew.admin.education.mapper.OrderMapper;
import top.continew.admin.education.model.entity.OrderDO;
import top.continew.admin.education.model.resp.payment.WechatPaymentResp;
import top.continew.admin.education.service.payment.WechatPayService;
import top.continew.starter.core.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 微信支付服务实现
 *
 * @author don
 * @since 2026/06/05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WechatPayServiceImpl implements WechatPayService {

    private final JsapiServiceExtension jsapiService;
    private final WechatPayConfig wechatPayConfig;
    private final WechatMpConfig wechatMpConfig;
    private final NotificationConfig rsaAutoCertificateConfig;
    private final OrderMapper orderMapper;

    @Override
    public WechatPaymentResp createJsapiOrder(String orderNo, String openid, Integer amount, String description) {
        log.info("创建JSAPI支付订单, orderNo: {}, openid: {}, amount: {}, description: {}", orderNo, openid, amount, description);

        try {
            // 构建支付请求
            PrepayRequest request = new PrepayRequest();

            // 设置金额
            Amount amountObj = new Amount();
            amountObj.setTotal(amount);
            amountObj.setCurrency("CNY");

            // 设置支付者
            Payer payer = new Payer();
            payer.setOpenid(openid);

            // 设置订单信息
            request.setAppid(wechatMpConfig.getAppId());
            request.setMchid(wechatPayConfig.getMchId());
            request.setDescription(description);
            request.setOutTradeNo(orderNo);
            request.setNotifyUrl(wechatPayConfig.getNotifyUrl());
            request.setAmount(amountObj);
            request.setPayer(payer);

            // 调用微信支付API
            PrepayWithRequestPaymentResponse response = jsapiService.prepayWithRequestPayment(request);

            log.info("JSAPI支付订单创建成功, orderNo: {}", orderNo);

            // 构建返回给前端的支付参数
            return WechatPaymentResp.builder()
                .timeStamp(response.getTimeStamp())
                .nonceStr(response.getNonceStr())
                .packageValue(response.getPackageVal())
                .signType(response.getSignType())
                .paySign(response.getPaySign())
                .appId(wechatMpConfig.getAppId())
                .build();

        } catch (ServiceException e) {
            log.error("创建JSAPI支付订单失败, orderNo: {}, errorCode: {}, errorMessage: {}", orderNo, e.getErrorCode(), e
                .getErrorMessage(), e);
            throw new BusinessException("创建支付订单失败: " + e.getErrorMessage());
        } catch (Exception e) {
            log.error("创建JSAPI支付订单异常, orderNo: {}", orderNo, e);
            throw new BusinessException("创建支付订单失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentNotify(String requestBody, Map<String, String> headers) {
        log.info("收到微信支付回调通知");

        try {
            // 构建通知解析器
            NotificationParser parser = new NotificationParser(rsaAutoCertificateConfig);

            RequestParam requestParam = new RequestParam.Builder().serialNumber(headers.get("Wechatpay-Serial"))
                .nonce(headers.get("Wechatpay-Nonce"))
                .signature(headers.get("Wechatpay-Signature"))
                .timestamp(headers.get("Wechatpay-Timestamp"))
                .body(requestBody)
                .build();

            // 解析通知内容
            Transaction transaction = parser.parse(requestParam, Transaction.class);

            String outTradeNo = transaction.getOutTradeNo();
            String transactionId = transaction.getTransactionId();
            String tradeState = transaction.getTradeState().name();

            log.info("支付回调解析成功, outTradeNo: {}, transactionId: {}, tradeState: {}", outTradeNo, transactionId, tradeState);

            // 查询订单
            OrderDO order = orderMapper.selectById(outTradeNo);
            if (order == null) {
                log.error("订单不存在, orderNo: {}", outTradeNo);
                throw new BusinessException("订单不存在");
            }

            // 检查订单状态，防止重复处理
            if ("COMPLETED".equals(order.getOrderStatus())) {
                log.warn("订单已处理，跳过重复回调, orderNo: {}", outTradeNo);
                return;
            }

            // 处理支付成功
            if ("SUCCESS".equals(tradeState)) {
                order.setOrderStatus("PAID"); // 已支付，待确认
                order.setPaymentTime(LocalDateTime.now());
                order.setRemark(StrUtil.isBlank(order.getRemark())
                    ? "微信支付成功，交易号：" + transactionId
                    : order.getRemark() + "；微信支付成功，交易号：" + transactionId);

                orderMapper.updateById(order);

                log.info("订单支付成功，状态已更新, orderNo: {}, transactionId: {}", outTradeNo, transactionId);

                // TODO: 发送支付成功通知（短信/邮件/公众号消息）

            } else {
                log.warn("支付未成功, orderNo: {}, tradeState: {}", outTradeNo, tradeState);
            }

        } catch (Exception e) {
            log.error("处理支付回调失败", e);
            throw new BusinessException("处理支付回调失败: " + e.getMessage());
        }
    }

    @Override
    public String queryOrderStatus(String orderNo) {
        log.info("查询订单支付状态, orderNo: {}", orderNo);

        try {
            QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
            request.setMchid(wechatPayConfig.getMchId());
            request.setOutTradeNo(orderNo);

            Transaction transaction = jsapiService.queryOrderByOutTradeNo(request);

            String tradeState = transaction.getTradeState().name();
            log.info("订单支付状态查询成功, orderNo: {}, tradeState: {}", orderNo, tradeState);

            return tradeState;

        } catch (ServiceException e) {
            log.error("查询订单支付状态失败, orderNo: {}, errorCode: {}, errorMessage: {}", orderNo, e.getErrorCode(), e
                .getErrorMessage(), e);
            throw new BusinessException("查询订单状态失败: " + e.getErrorMessage());
        } catch (Exception e) {
            log.error("查询订单支付状态异常, orderNo: {}", orderNo, e);
            throw new BusinessException("查询订单状态失败: " + e.getMessage());
        }
    }

    @Override
    public void closeOrder(String orderNo) {
        log.info("关闭订单, orderNo: {}", orderNo);

        try {
            CloseOrderRequest request = new CloseOrderRequest();
            request.setMchid(wechatPayConfig.getMchId());
            request.setOutTradeNo(orderNo);

            jsapiService.closeOrder(request);

            log.info("订单关闭成功, orderNo: {}", orderNo);

        } catch (ServiceException e) {
            log.error("关闭订单失败, orderNo: {}, errorCode: {}, errorMessage: {}", orderNo, e.getErrorCode(), e
                .getErrorMessage(), e);
            throw new BusinessException("关闭订单失败: " + e.getErrorMessage());
        } catch (Exception e) {
            log.error("关闭订单异常, orderNo: {}", orderNo, e);
            throw new BusinessException("关闭订单失败: " + e.getMessage());
        }
    }
}
