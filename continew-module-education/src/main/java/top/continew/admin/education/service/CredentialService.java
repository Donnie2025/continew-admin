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

import top.continew.admin.education.model.req.CredentialSetPasswordReq;
import top.continew.admin.education.model.req.CredentialVerifyPasswordReq;
import top.continew.admin.education.model.resp.CredentialStatusResp;
import top.continew.admin.education.model.resp.CredentialVerifyPasswordResp;

/**
 * 用户凭证服务接口
 *
 * @author don
 * @since 2025/11/14
 */
public interface CredentialService {

    /**
     * 设置用户密码
     *
     * @param req 设置密码请求参数
     */
    void setPassword(CredentialSetPasswordReq req);

    /**
     * 验证用户密码
     *
     * @param req 验证密码请求参数
     * @return 验证结果
     */
    CredentialVerifyPasswordResp verifyPassword(CredentialVerifyPasswordReq req);

    /**
     * 重置用户密码错误次数
     *
     * @param userType 用户类型
     * @param phone    手机号
     */
    void resetErrorCount(String userType, String phone);

    /**
     * 查询用户凭证状态
     *
     * @param userType 用户类型
     * @param phone    手机号
     * @return 凭证状态
     */
    CredentialStatusResp getCredentialStatus(String userType, String phone);
}
