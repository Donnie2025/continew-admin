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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.education.enums.AccountTypeEnum;
import top.continew.admin.education.model.resp.AccountResp;
import top.continew.admin.education.service.AccountService;

import java.util.List;

/**
 * 账户管理 API
 *
 * @author don
 * @since 2026/06/22
 */
@Tag(name = "账户管理 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/education/account")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "获取学生账户信息", description = "获取学生的PAID类型账户")
    @GetMapping("/student/{studentId}")
    public AccountResp getStudentAccount(@Parameter(description = "学生ID", required = true) @PathVariable Long studentId) {
        return accountService.getStudentAccountByType(studentId, AccountTypeEnum.PAID);
    }

    @Operation(summary = "获取学生所有账户", description = "获取学生的所有账户列表")
    @GetMapping("/student/{studentId}/all")
    public List<AccountResp> getStudentAccounts(@Parameter(description = "学生ID", required = true) @PathVariable Long studentId) {
        return accountService.getStudentAccounts(studentId);
    }
}
