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

package top.continew.admin.education.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.continew.admin.education.service.SmsService;

/**
 * 短信服务实现
 *
 * @author don
 * @since 2026/02/05
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Value("${sms.enabled:false}")
    private boolean smsEnabled;

    @Value("${sms.provider:mock}")
    private String smsProvider;

    @Value("${sms.verify-code-template:【Young教育】您的验证码为：%s，5分钟内有效，请勿泄露给他人。}")
    private String verifyCodeTemplate;

    @Override
    public boolean sendVerifyCode(String phone, String code) {
        if (StrUtil.isBlank(phone) || StrUtil.isBlank(code)) {
            log.warn("手机号或验证码为空，无法发送短信: phone={}, code={}", phone, code);
            return false;
        }

        String message = String.format(verifyCodeTemplate, code);
        return sendSms(phone, message, "验证码");
    }

    @Override
    public boolean sendNotification(String phone, String message) {
        if (StrUtil.isBlank(phone) || StrUtil.isBlank(message)) {
            log.warn("手机号或消息内容为空，无法发送短信: phone={}, message={}", phone, message);
            return false;
        }

        return sendSms(phone, message, "通知");
    }

    @Override
    public int sendBatch(String[] phones, String message) {
        if (phones == null || phones.length == 0 || StrUtil.isBlank(message)) {
            log.warn("手机号列表或消息内容为空，无法批量发送短信");
            return 0;
        }

        int successCount = 0;
        for (String phone : phones) {
            if (sendNotification(phone, message)) {
                successCount++;
            }
        }

        log.info("批量发送短信完成: 总数={}, 成功={}, 失败={}", phones.length, successCount, phones.length - successCount);
        return successCount;
    }

    /**
     * 统一的短信发送方法
     */
    private boolean sendSms(String phone, String message, String type) {
        log.info("准备发送{}短信: phone={}, message={}", type, phone, message);

        if (!smsEnabled) {
            log.info("[短信服务已禁用] {}短信发送模拟成功: phone={}", type, phone);
            return true;
        }

        try {
            switch (smsProvider.toLowerCase()) {
                case "aliyun":
                    return sendByAliyun(phone, message);
                case "tencent":
                    return sendByTencent(phone, message);
                case "mock":
                default:
                    return sendByMock(phone, message, type);
            }
        } catch (Exception e) {
            log.error("发送{}短信失败: phone={}, error={}", type, phone, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 阿里云短信发送（待实现）
     */
    private boolean sendByAliyun(String phone, String message) {
        log.info("[阿里云短信] 发送短信: phone={}, message={}", phone, message);
        // TODO: 实现阿里云短信发送逻辑
        // 1. 初始化阿里云短信客户端
        // 2. 构建发送请求
        // 3. 调用发送接口
        // 4. 处理响应结果
        log.warn("阿里云短信服务尚未实现，使用模拟发送");
        return sendByMock(phone, message, "阿里云");
    }

    /**
     * 腾讯云短信发送（待实现）
     */
    private boolean sendByTencent(String phone, String message) {
        log.info("[腾讯云短信] 发送短信: phone={}, message={}", phone, message);
        // TODO: 实现腾讯云短信发送逻辑
        // 1. 初始化腾讯云短信客户端
        // 2. 构建发送请求
        // 3. 调用发送接口
        // 4. 处理响应结果
        log.warn("腾讯云短信服务尚未实现，使用模拟发送");
        return sendByMock(phone, message, "腾讯云");
    }

    /**
     * 模拟短信发送（用于开发测试）
     */
    private boolean sendByMock(String phone, String message, String type) {
        log.info("[{}模拟短信] 发送成功: phone={}, message={}", type, phone, message);

        // 模拟发送延迟
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 模拟99%的成功率，1%的失败率用于测试
        return Math.random() > 0.01;
    }
}
