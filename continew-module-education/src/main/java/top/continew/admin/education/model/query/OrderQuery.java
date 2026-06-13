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

package top.continew.admin.education.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;
import java.io.Serial;
import java.io.Serializable;

/**
 * 订单查询条件
 *
 * @author don
 * @since 2025/11/26 22:05
 */
@Data
@Schema(description = "订单查询条件")
public class OrderQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单编号
     */
    @Schema(description = "订单编号")
    @Query(type = QueryType.EQ)
    private String orderNo;

    /**
     * 学生姓名
     */
    @Schema(description = "学生姓名")
    @Query(type = QueryType.LIKE, columns = "student_name")
    private String studentName;

    /**
     * 会员卡ID
     */
    @Schema(description = "会员卡ID")
    @Query(type = QueryType.EQ)
    private Long cardId;

    /**
     * 会员卡标题
     */
    @Schema(description = "会员卡标题")
    @Query(type = QueryType.EQ)
    private String cardTitle;

    /**
     * 会员卡类型（TL:次卡有限期 TU:次卡无限期 BL:储蓄卡有限期 BU:储蓄卡无限期）
     */
    @Schema(description = "会员卡类型（TL:次卡有限期 TU:次卡无限期 BL:储蓄卡有限期 BU:储蓄卡无限期）")
    @Query(type = QueryType.EQ)
    private String cardType;

    /**
     * 支付方式（wechat:微信支付, alipay:支付宝）
     */
    @Schema(description = "支付方式（wechat:微信支付, alipay:支付宝）")
    @Query(type = QueryType.EQ)
    private String paymentType;

    /**
     * 支付类型（online-在线支付 qrcode-扫码支付 offline-线下支付）
     */
    @Schema(description = "支付类型（online-在线支付 qrcode-扫码支付 offline-线下支付）")
    @Query(type = QueryType.EQ)
    private String paymentMethod;

    /**
     * 订单状态（PENDING:待确认, COMPLETED:已完成, CANCELLED:已取消）
     */
    @Schema(description = "订单状态（PENDING:待确认, COMPLETED:已完成, CANCELLED:已取消）")
    @Query(type = QueryType.EQ)
    private String orderStatus;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    @Query(type = QueryType.EQ)
    private Long createUser;
}