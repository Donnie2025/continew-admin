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
 * 会员卡交易流水实体
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
     * 学生课时账户ID
     */
    private Long accountId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学生姓名（快照）
     */
    private String studentName;

    /**
     * 会员卡名称（快照）
     */
    private String cardTitle;

    /**
     * 交易类型：
     * bind - 首次购买会员卡（C，有 cash_amount）
     * recharge - 续费充值（C，有 cash_amount）
     * consume - 上课消费扣减课时（D，cash_amount=null）
     * refund - 退款退课时（C，有 cash_amount）
     * expire - 到期清零（D，cash_amount=null）
     * adjust - 人工调整课时（C/D，cash_amount=null）
     */
    private String transType;

    /**
     * 借贷方向：C=Credit 入账/增加余额，D=Debit 出账/减少余额
     */
    private String direction;

    /**
     * 课时变动数量（始终为正数，direction 标明方向）
     */
    private BigDecimal amount;

    /**
     * 交易后课时余额快照
     */
    private BigDecimal balance;

    /**
     * 实际现金金额（购买/退款时使用）
     */
    private BigDecimal cashAmount;

    /**
     * 备注
     */
    private String remark;
}
