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
    PAID("PAID", "普通账户"),

    /**
     * 赠送课时账户
     */
    GIFT("GIFT", "赠送账户"),

    /**
     * 请假课时账户
     */
    LEAVE("LEAVE", "临时请假次数"),

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

    /**
     * 根据编码获取账户类型名称
     *
     * @param code 账户类型编码
     * @return 账户类型描述，未找到返回编码本身
     */
    public static String getDescByCode(String code) {
        for (AccountTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type.desc;
            }
        }
        return code;
    }

    /**
     * 根据编码获取枚举实例
     *
     * @param code 账户类型编码
     * @return 枚举实例，未找到返回null
     */
    public static AccountTypeEnum fromCode(String code) {
        for (AccountTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
