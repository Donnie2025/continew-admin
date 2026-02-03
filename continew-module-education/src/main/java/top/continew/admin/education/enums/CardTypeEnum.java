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
 * 会员卡类型枚举
 *
 * @author don
 * @since 2025/01/07
 */
@Getter
@RequiredArgsConstructor
public enum CardTypeEnum {

    /**
     * 次卡有限期
     */
    TIMES_LIMITED("TL", "次卡有限期", "Times Limited"),

    /**
     * 次卡无限期
     */
    TIMES_UNLIMITED("TU", "次卡无限期", "Times Unlimited"),

    /**
     * 储蓄卡有限期
     */
    BALANCE_LIMITED("BL", "储蓄卡有限期", "Balance Limited"),

    /**
     * 储蓄卡无限期
     */
    BALANCE_UNLIMITED("BU", "储蓄卡无限期", "Balance Unlimited");

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 中文描述
     */
    private final String description;

    /**
     * 英文全称
     */
    private final String fullName;

    /**
     * 根据代码获取枚举
     *
     * @param code 类型代码
     * @return 会员卡类型枚举
     */
    public static CardTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (CardTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为次卡类型
     *
     * @param code 类型代码
     * @return true-次卡类型，false-储蓄卡类型
     */
    public static boolean isTimesCard(String code) {
        CardTypeEnum type = getByCode(code);
        return type == TIMES_LIMITED || type == TIMES_UNLIMITED;
    }

    /**
     * 判断是否为储蓄卡类型
     *
     * @param code 类型代码
     * @return true-储蓄卡类型，false-次卡类型
     */
    public static boolean isBalanceCard(String code) {
        CardTypeEnum type = getByCode(code);
        return type == BALANCE_LIMITED || type == BALANCE_UNLIMITED;
    }

    /**
     * 判断是否为有限期卡类型
     *
     * @param code 类型代码
     * @return true-有限期卡，false-无限期卡
     */
    public static boolean isLimitedCard(String code) {
        CardTypeEnum type = getByCode(code);
        return type == TIMES_LIMITED || type == BALANCE_LIMITED;
    }

    /**
     * 判断是否为无限期卡类型
     *
     * @param code 类型代码
     * @return true-无限期卡，false-有限期卡
     */
    public static boolean isUnlimitedCard(String code) {
        CardTypeEnum type = getByCode(code);
        return type == TIMES_UNLIMITED || type == BALANCE_UNLIMITED;
    }

    /**
     * 获取所有类型代码
     *
     * @return 类型代码数组
     */
    public static String[] getAllCodes() {
        CardTypeEnum[] types = values();
        String[] codes = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            codes[i] = types[i].getCode();
        }
        return codes;
    }
}
