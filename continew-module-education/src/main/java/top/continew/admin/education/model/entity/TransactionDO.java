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

package top.continew.admin.education.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 订单实体
 *
 * @author don
 * @since 2025/05/10 22:11
 */
@Data
@TableName("edu_transaction")
public class TransactionDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 学生会员卡绑定表ID
     */
    private Long stuCardId;

    /**
     * 学生ID
     */
    private Long stuId;

    /**
     * 学生姓名
     */
    private String stuName;

    /**
     * 会员卡标题
     */
    private String cardTitle;

    /**
     * 交易类型
     */
    private String transType;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 变动前余额
     */
    private BigDecimal beforeAmt;

    /**
     * 变动后余额
     */
    private BigDecimal afterAmt;

    /**
     * 备注
     */
    private String remark;
}
