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

package top.continew.admin.education.constant;

/**
 * 交易类型常量
 *
 * @author continew-org
 * @since 2025-01-02
 */
public final class TransactionType {

    /**
     * 充值
     */
    public static final String CREDIT = "credit";

    /**
     * 扣费
     */
    public static final String DEBIT = "debit";

    /**
     * 冻结
     */
    public static final String FREEZE = "freeze";

    /**
     * 激活
     */
    public static final String ACTIVATE = "activate";

    /**
     * 取消约课
     */
    public static final String CANCEL = "cancel";

    /**
     * 首次绑卡
     */
    public static final String BIND = "bind";

    /**
     * 约课扣费
     */
    public static final String BOOK_DEBIT = "book_debit";

    private TransactionType() {
        // 工具类，禁止实例化
    }
}
