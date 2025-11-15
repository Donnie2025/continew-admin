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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.education.model.req.CredentialSetPasswordReq;
import top.continew.admin.education.model.req.CredentialVerifyPasswordReq;
import top.continew.admin.education.model.resp.CredentialStatusResp;
import top.continew.admin.education.model.resp.CredentialVerifyPasswordResp;
import top.continew.admin.education.service.CredentialService;
import top.continew.starter.web.model.R;

/**
 * 用户凭证管理 API
 * 
 * 统一管理学生和教师的密码设置、验证等凭证相关功能
 *
 * @author don
 * @since 2025/11/14
 */
@Tag(name = "用户凭证管理 API")
@RestController
@RequestMapping("/education/credential")
@RequiredArgsConstructor
public class CredentialController {

    private final CredentialService credentialService;

    /**
     * 设置用户密码（通用接口）
     *
     * @param req 设置密码请求参数
     * @return 操作结果
     */
    @PatchMapping("/set-password")
    @Operation(summary = "设置用户密码", description = "为用户设置新密码（密码需要Base64编码）")
    public R<Void> setPassword(@Valid @RequestBody CredentialSetPasswordReq req) {
        credentialService.setPassword(req);
        return R.ok();
    }

    /**
     * 验证用户密码（通用接口）
     *
     * @param req 验证密码请求参数
     * @return 验证结果
     */
    @PostMapping("/verify-password")
    @Operation(summary = "验证用户密码", description = "验证用户密码，支持错误次数限制和临时冻结（密码需要Base64编码）")
    @SaIgnore
    public R<CredentialVerifyPasswordResp> verifyPassword(@Valid @RequestBody CredentialVerifyPasswordReq req) {
        CredentialVerifyPasswordResp result = credentialService.verifyPassword(req);
        return R.ok(result);
    }

    /**
     * 重置用户密码错误次数
     *
     * @param userType 用户类型（student/teacher）
     * @param phone    手机号
     * @return 操作结果
     */
    @PostMapping("/reset-error-count")
    @Operation(summary = "重置用户密码错误次数", description = "管理员重置用户的密码错误次数和冻结状态")
    public R<Void> resetErrorCount(
            @RequestParam String userType,
            @RequestParam String phone) {
        credentialService.resetErrorCount(userType, phone);
        return R.ok();
    }

    /**
     * 查询用户凭证状态
     *
     * @param userType 用户类型（student/teacher）
     * @param phone    手机号
     * @return 凭证状态信息
     */
    @GetMapping("/status")
    @Operation(summary = "查询用户凭证状态", description = "查询用户的凭证状态，包括错误次数、冻结状态等")
    public R<CredentialStatusResp> getCredentialStatus(
            @RequestParam String userType,
            @RequestParam String phone) {
        CredentialStatusResp result = credentialService.getCredentialStatus(userType, phone);
        return R.ok(result);
    }
}
