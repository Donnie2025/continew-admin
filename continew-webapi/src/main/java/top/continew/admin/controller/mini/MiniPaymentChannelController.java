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

package top.continew.admin.controller.mini;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.education.model.resp.PaymentChannelResp;
import top.continew.admin.education.service.PaymentChannelService;
import top.continew.starter.web.model.R;

import java.util.List;

/**
 * 小程序支付渠道 API
 *
 * @author don
 * @since 2026/06/09
 */
@Tag(name = "小程序支付渠道 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mini/payment-channel")
public class MiniPaymentChannelController {

    private final PaymentChannelService paymentChannelService;

    @Operation(summary = "获取可用支付渠道", description = "获取启用状态的支付渠道列表，按排序字段排序")
    @GetMapping("/list")
    public R<List<PaymentChannelResp>> listActiveChannels() {
        List<PaymentChannelResp> channels = paymentChannelService.listActiveChannels();
        return R.ok(channels);
    }
}
