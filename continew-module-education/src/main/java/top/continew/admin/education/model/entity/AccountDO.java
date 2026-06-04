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
import java.time.LocalDate;

/**
 * 学生课时账户实体
 *
 * @author don
 * @since 2025/06/01
 */
@Data
@TableName("edu_account")
public class AccountDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学生姓名（快照）
     */
    private String studentName;

    /**
     * 账户类型（PAID:正常购买 GIFT:赠送课时 LEAVE:请假课时 FREEZE:冻结账户）
     */
    private String accountType;

    /**
     * 当前课时余额
     */
    private BigDecimal balance;

    /**
     * 到期日期（null 表示无限期）
     */
    private LocalDate expireDate;

    /**
     * 状态（1：正常；0：禁用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
