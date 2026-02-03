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

package top.continew.admin.education.constants;

/**
 * 会员卡类型常量
 *
 * @author don
 * @since 2025/01/07
 */
public final class CardTypeConstants {

    private CardTypeConstants() {
        // 防止实例化
    }

    /**
     * 次卡有限期 - Times Limited
     */
    public static final String TIMES_LIMITED = "TL";

    /**
     * 次卡无限期 - Times Unlimited
     */
    public static final String TIMES_UNLIMITED = "TU";

    /**
     * 储蓄卡有限期 - Balance Limited
     */
    public static final String BALANCE_LIMITED = "BL";

    /**
     * 储蓄卡无限期 - Balance Unlimited
     */
    public static final String BALANCE_UNLIMITED = "BU";

    /**
     * 所有卡类型数组
     */
    public static final String[] ALL_TYPES = {TIMES_LIMITED, TIMES_UNLIMITED, BALANCE_LIMITED, BALANCE_UNLIMITED};

    /**
     * 次卡类型数组
     */
    public static final String[] TIMES_TYPES = {TIMES_LIMITED, TIMES_UNLIMITED};

    /**
     * 储蓄卡类型数组
     */
    public static final String[] BALANCE_TYPES = {BALANCE_LIMITED, BALANCE_UNLIMITED};

    /**
     * 有限期卡类型数组
     */
    public static final String[] LIMITED_TYPES = {TIMES_LIMITED, BALANCE_LIMITED};

    /**
     * 无限期卡类型数组
     */
    public static final String[] UNLIMITED_TYPES = {TIMES_UNLIMITED, BALANCE_UNLIMITED};

    /**
     * 判断是否为次卡类型
     *
     * @param type 卡类型
     * @return true-次卡类型，false-储蓄卡类型
     */
    public static boolean isTimesCard(String type) {
        return TIMES_LIMITED.equals(type) || TIMES_UNLIMITED.equals(type);
    }

    /**
     * 判断是否为储蓄卡类型
     *
     * @param type 卡类型
     * @return true-储蓄卡类型，false-次卡类型
     */
    public static boolean isBalanceCard(String type) {
        return BALANCE_LIMITED.equals(type) || BALANCE_UNLIMITED.equals(type);
    }

    /**
     * 判断是否为有限期卡类型
     *
     * @param type 卡类型
     * @return true-有限期卡，false-无限期卡
     */
    public static boolean isLimitedCard(String type) {
        return TIMES_LIMITED.equals(type) || BALANCE_LIMITED.equals(type);
    }

    /**
     * 判断是否为无限期卡类型
     *
     * @param type 卡类型
     * @return true-无限期卡，false-有限期卡
     */
    public static boolean isUnlimitedCard(String type) {
        return TIMES_UNLIMITED.equals(type) || BALANCE_UNLIMITED.equals(type);
    }

    /**
     * 获取卡类型中文描述
     *
     * @param type 卡类型
     * @return 中文描述
     */
    public static String getDescription(String type) {
        switch (type) {
            case TIMES_LIMITED:
                return "次卡有限期";
            case TIMES_UNLIMITED:
                return "次卡无限期";
            case BALANCE_LIMITED:
                return "储蓄卡有限期";
            case BALANCE_UNLIMITED:
                return "储蓄卡无限期";
            default:
                return "未知类型";
        }
    }

    /**
     * 获取卡类型英文全称
     *
     * @param type 卡类型
     * @return 英文全称
     */
    public static String getFullName(String type) {
        switch (type) {
            case TIMES_LIMITED:
                return "Times Limited";
            case TIMES_UNLIMITED:
                return "Times Unlimited";
            case BALANCE_LIMITED:
                return "Balance Limited";
            case BALANCE_UNLIMITED:
                return "Balance Unlimited";
            default:
                return "Unknown Type";
        }
    }

    /**
     * 验证卡类型是否有效
     *
     * @param type 卡类型
     * @return true-有效，false-无效
     */
    public static boolean isValidType(String type) {
        if (type == null) {
            return false;
        }
        for (String validType : ALL_TYPES) {
            if (validType.equals(type)) {
                return true;
            }
        }
        return false;
    }
}
