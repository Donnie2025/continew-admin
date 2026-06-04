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
     * 首次购买（Credit）
     */
    BIND("bind", "首次购买"),

    /**
     * 续费充値（Credit）
     */
    RECHARGE("recharge", "续费充値"),

    /**
     * 上课消费扣减课时（Debit）
     */
    CONSUME("consume", "上课消费"),

    /**
     * 退款退课时（Credit）
     */
    REFUND("refund", "退款退课时"),

    /**
     * 到期清零（Debit）
     */
    EXPIRE("expire", "到期清零"),

    /**
     * 人工调整（Credit/Debit）
     */
    ADJUST("adjust", "人工调整"),

    /**
     * 取消预约退还课时（Credit）
     */
    CANCEL("cancel", "取消预约");

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;
}