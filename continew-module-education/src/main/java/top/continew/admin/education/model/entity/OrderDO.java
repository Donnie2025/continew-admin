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

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.time.*;
import java.math.BigDecimal;

/**
 * 订单实体
 *
 * @author don
 * @since 2025/11/26 22:05
 */
@Data
@TableName("edu_order")
public class OrderDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 学生ID
     */
    private Long stuId;

    /**
     * 学生姓名
     */
    private String stuName;

    /**
     * 会员卡ID
     */
    private Long cardId;

    /**
     * 会员卡标题
     */
    private String cardTitle;

    /**
     * 会员卡类型（TL:次卡有限期 TU:次卡无限期 BL:储蓄卡有限期 BU:储蓄卡无限期）
     */
    private String cardType;

    /**
     * 订单金额
     */
    private BigDecimal orderPrice;

    /**
     * 支付方式（wechat:微信支付, alipay:支付宝）
     */
    private String paymentType;

    /**
     * 订单状态（PENDING:待确认, COMPLETED:已完成, CANCELLED:已取消）
     */
    private String orderStatus;

    /**
     * 关联的学生会员卡ID（下单时创建空白卡）
     */
    private Long stuCardId;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 确认时间
     */
    private LocalDateTime confirmTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 所属机构ID
     */
    private Long institutionId;
}
