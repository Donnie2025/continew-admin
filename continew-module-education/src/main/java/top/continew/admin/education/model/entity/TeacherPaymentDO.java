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

/**
 * 教师收款信息实体
 *
 * @author donnie
 * @since 2026/06/17
 */
@Data
@TableName("edu_teacher_payment")
public class TeacherPaymentDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 教师姓名（冗余字段）
     */
    private String teacherName;

    /**
     * 支付渠道（GCash、Maya、Bank）
     */
    private String paymentChannel;

    /**
     * 账号
     */
    private String accountNumber;

    /**
     * 账户名
     */
    private String accountName;

    /**
     * 收款二维码地址
     */
    private String qrCode;

    /**
     * 银行名称（当payment_channel为Bank时使用）
     */
    private String bankName;

    /**
     * 单价（从edu_teacher表迁移）
     */
    private Integer rate;

    /**
     * 是否默认收款方式（0：否；1：是）
     */
    private Integer isDefault;

    /**
     * 状态（0：禁用；1：启用）
     */
    private Integer status;
}
