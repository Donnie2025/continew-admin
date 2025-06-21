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
 * 交易类型枚举
 *
 * @author don
 * @since 2025/05/11 14:20
 */
@Getter
@RequiredArgsConstructor
public enum TransactionTypeEnum {

    /**
     * 充值
     */
    CREDIT("credit", "充值"),

    /**
     * 扣费
     */
    DEBIT("debit", "扣费"),

    /**
     * 冻结
     */
    FREEZE("freeze", "冻结"),

    /**
     * 激活
     */
    ACTIVATE("activate", "激活"),

    /**
     * 取消约课
     */
    CANCEL("cancel", "取消约课"),

    /**
     * 首次绑卡
     */
    BIND("bind", "首次绑卡"),

    /**
     * 约课扣费
     */
    BOOK_DEBIT("book_debit", "约课扣费");

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;
}