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

package top.continew.admin.education.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 课时账户类型枚举
 *
 * @author don
 * @since 2025/06/01
 */
@Getter
@RequiredArgsConstructor
public enum AccountTypeEnum {

    /**
     * 正常购买账户
     */
    PAID("PAID", "正常购买"),

    /**
     * 赠送课时账户
     */
    GIFT("GIFT", "赠送课时"),

    /**
     * 请假课时账户
     */
    LEAVE("LEAVE", "请假课时"),

    /**
     * 冻结账户
     */
    FREEZE("FREEZE", "冻结账户");

    /**
     * 账户类型编码（存入 DB）
     */
    private final String code;

    /**
     * 账户类型描述
     */
    private final String desc;
}
