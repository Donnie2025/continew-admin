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

import top.continew.admin.education.model.resp.AccountResp;

import java.util.List;
import java.util.Map;

/**
 * 账户业务接口
 *
 * @author continew-org
 * @since 2026-06-09
 */
public interface AccountService {

    /**
     * 获取当前登录学生的账户余额信息
     * 返回格式：{accountId: xxx, balance: xxx}
     *
     * @return 账户余额信息
     */
    Map<String, Object> getCurrentStudentAccountBalance();

    /**
     * 获取学生的所有账户列表
     *
     * @param studentId 学生ID
     * @return 账户列表
     */
    List<AccountResp> getStudentAccounts(Long studentId);
}
