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

package top.continew.admin.education.service;

/**
 * 短信服务接口
 *
 * @author don
 * @since 2026/02/05
 */
public interface SmsService {

    /**
     * 发送验证码短信
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 发送结果
     */
    boolean sendVerifyCode(String phone, String code);

    /**
     * 发送通知短信
     *
     * @param phone   手机号
     * @param message 短信内容
     * @return 发送结果
     */
    boolean sendNotification(String phone, String message);

    /**
     * 批量发送短信
     *
     * @param phones  手机号列表
     * @param message 短信内容
     * @return 发送成功的数量
     */
    int sendBatch(String[] phones, String message);
}
