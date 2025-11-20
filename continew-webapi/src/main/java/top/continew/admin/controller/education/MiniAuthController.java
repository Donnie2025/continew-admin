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

package top.continew.admin.controller.education;

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.education.model.req.MiniBindPhoneReq;
import top.continew.admin.education.model.req.MiniSendCodeReq;
import top.continew.admin.education.model.req.MiniWechatLoginReq;
import top.continew.admin.education.model.resp.MiniLoginResp;
import top.continew.admin.education.service.MiniAuthService;

/**
 * 小程序认证 API
 *
 * @author don
 * @since 2025/11/19
 */
@Tag(name = "小程序认证 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mini/auth")
public class MiniAuthController {

    private final MiniAuthService miniAuthService;

    @SaIgnore
    @Operation(summary = "微信登录", description = "小程序微信登录")
    @PostMapping("/login/wechat")
    public MiniLoginResp loginByWechat(@Validated @RequestBody MiniWechatLoginReq req, HttpServletRequest request) {
        return miniAuthService.loginByWechat(req, request);
    }

    @Operation(summary = "发送验证码", description = "发送手机验证码")
    @PostMapping("/send-code")
    public void sendVerifyCode(@Validated @RequestBody MiniSendCodeReq req) {
        miniAuthService.sendVerifyCode(req);
    }

    @Operation(summary = "绑定手机号", description = "绑定手机号到学生账户")
    @PostMapping("/bind-phone")
    public void bindPhone(@Validated @RequestBody MiniBindPhoneReq req) {
        miniAuthService.bindPhone(req);
    }
}
