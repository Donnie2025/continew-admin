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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import top.continew.admin.education.service.OrderService;
import top.continew.admin.education.service.payment.WechatPayService;
import top.continew.admin.education.enums.OrderStatus;
import top.continew.admin.common.context.UserContext;
import top.continew.admin.common.context.UserContextHolder;
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
    private final OrderService orderService;

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

            // 打印调用微信支付API的请求参数
            log.info("========== 调用微信支付API请求参数开始 ==========");
            log.info("appid: {}", request.getAppid());
            log.info("mchid: {}", request.getMchid());
            log.info("description: {}", request.getDescription());
            log.info("outTradeNo: {}", request.getOutTradeNo());
            log.info("notifyUrl: {}", request.getNotifyUrl());
            log.info("amount.total: {}", amountObj.getTotal());
            log.info("amount.currency: {}", amountObj.getCurrency());
            log.info("payer.openid: {}", payer.getOpenid());
            log.info("========== 调用微信支付API请求参数结束 ==========");

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
        log.info("========== 回调通知详情 ==========");
        log.info("请求头 - Serial: {}", headers.get("Wechatpay-Serial"));
        log.info("请求头 - Nonce: {}", headers.get("Wechatpay-Nonce"));
        log.info("请求头 - Timestamp: {}", headers.get("Wechatpay-Timestamp"));
        log.info("请求头 - Signature: {}", headers.get("Wechatpay-Signature"));
        log.info("请求体（原始）: {}", requestBody);

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
            log.info("========== 开始解析回调通知 ==========");
            Transaction transaction = parser.parse(requestParam, Transaction.class);
            log.info("========== 回调通知解析成功 ==========");

            String outTradeNo = transaction.getOutTradeNo();
            String transactionId = transaction.getTransactionId();
            String tradeState = transaction.getTradeState().name();

            log.info("========== 解析结果 ==========");
            log.info("订单号(outTradeNo): {}", outTradeNo);
            log.info("交易号(transactionId): {}", transactionId);
            log.info("支付状态(tradeState): {}", tradeState);
            log.info("支付类型(tradeType): {}", transaction.getTradeType());
            if (transaction.getAmount() != null) {
                log.info("支付金额(total): {}", transaction.getAmount().getTotal());
            }
            if (transaction.getPayer() != null) {
                log.info("支付者openid: {}", transaction.getPayer().getOpenid());
            }
            log.info("========== 解析结果结束 ==========");

            // 查询订单（通过订单号查询）
            QueryWrapper<OrderDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_no", outTradeNo);
            OrderDO order = orderMapper.selectOne(queryWrapper);

            if (order == null) {
                log.error("订单不存在, orderNo: {}", outTradeNo);
                throw new BusinessException("订单不存在");
            }

            log.info("========== 订单信息 ==========");
            log.info("订单ID: {}", order.getId());
            log.info("订单号: {}", order.getOrderNo());
            log.info("当前状态: {}", order.getOrderStatus());
            log.info("订单金额: {}", order.getOrderPrice());
            log.info("========== 订单信息结束 ==========");

            // 检查订单状态，防止重复处理
            if (OrderStatus.COMPLETED.equals(order.getOrderStatus())) {
                log.warn("订单已处理，跳过重复回调, orderNo: {}", outTradeNo);
                return;
            }

            // 处理支付成功
            if ("SUCCESS".equals(tradeState)) {
                log.info("========== 开始处理支付成功 ==========");

                // 调用订单服务的确认订单方法，完成充值
                // 注意：回调场景下没有用户上下文，需要临时设置学生ID作为操作用户
                try {
                    // 临时设置用户上下文为订单的学生ID
                    UserContext userContext = new UserContext();
                    userContext.setId(order.getStudentId());
                    userContext.setUsername(order.getStudentName());
                    UserContextHolder.setContext(userContext, false);

                    orderService.confirmOrder(order.getId());
                    log.info("订单确认成功，已完成充值, orderId: {}, orderNo: {}", order.getId(), outTradeNo);
                } catch (Exception e) {
                    log.error("订单确认失败, orderId: {}, orderNo: {}", order.getId(), outTradeNo, e);
                    // 如果确认失败，将订单状态保持为PENDING（待确认），需人工处理
                    order.setOrderStatus(OrderStatus.PENDING);
                    order.setPaymentTime(LocalDateTime.now());
                    order.setRemark(StrUtil.isBlank(order.getRemark())
                        ? "微信支付成功，交易号：" + transactionId + "；自动充值失败，需人工确认"
                        : order.getRemark() + "；微信支付成功，交易号：" + transactionId + "；自动充值失败，需人工确认");
                    orderMapper.updateById(order);
                    throw e;
                } finally {
                    // 清理用户上下文
                    UserContextHolder.clearContext();
                }

                log.info("========== 支付成功处理完成 ==========");

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
