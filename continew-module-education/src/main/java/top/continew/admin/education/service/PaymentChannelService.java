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

import top.continew.admin.education.model.resp.PaymentChannelResp;

import java.util.List;

/**
 * 支付渠道服务
 *
 * @author don
 * @since 2026/06/09
 */
public interface PaymentChannelService {

    /**
     * 获取启用的支付渠道列表（按排序）
     *
     * @return 支付渠道列表
     */
    List<PaymentChannelResp> listActiveChannels();

    /**
     * 根据ID获取支付渠道详情
     *
     * @param id 支付渠道ID
     * @return 支付渠道信息
     */
    PaymentChannelResp getChannelById(Long id);
}
