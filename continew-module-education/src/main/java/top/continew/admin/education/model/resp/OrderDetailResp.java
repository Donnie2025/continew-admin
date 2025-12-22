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

package top.continew.admin.education.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;
import java.io.Serial;
import java.time.*;
import java.math.BigDecimal;

/**
 * 订单详情信息
 *
 * @author don
 * @since 2025/11/26 22:05
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "订单详情信息")
public class OrderDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单编号
     */
    @Schema(description = "订单编号")
    @ExcelProperty(value = "订单编号")
    private String orderNo;

    /**
     * 学生ID
     */
    @Schema(description = "学生ID")
    @ExcelProperty(value = "学生ID")
    private Long stuId;

    /**
     * 学生姓名
     */
    @Schema(description = "学生姓名")
    @ExcelProperty(value = "学生姓名")
    private String stuName;

    /**
     * 会员卡ID
     */
    @Schema(description = "会员卡ID")
    @ExcelProperty(value = "会员卡ID")
    private Long cardId;

    /**
     * 会员卡标题
     */
    @Schema(description = "会员卡标题")
    @ExcelProperty(value = "会员卡标题")
    private String cardTitle;

    /**
     * 会员卡类型（TL:次卡有限期 TU:次卡无限期 BL:储蓄卡有限期 BU:储蓄卡无限期）
     */
    @Schema(description = "会员卡类型（TL:次卡有限期 TU:次卡无限期 BL:储蓄卡有限期 BU:储蓄卡无限期）")
    @ExcelProperty(value = "会员卡类型（TL:次卡有限期 TU:次卡无限期 BL:储蓄卡有限期 BU:储蓄卡无限期）")
    private String cardType;

    /**
     * 订单金额
     */
    @Schema(description = "订单金额")
    @ExcelProperty(value = "订单金额")
    private BigDecimal orderPrice;

    /**
     * 支付方式（wechat:微信支付, alipay:支付宝）
     */
    @Schema(description = "支付方式（wechat:微信支付, alipay:支付宝）")
    @ExcelProperty(value = "支付方式（wechat:微信支付, alipay:支付宝）")
    private String paymentType;

    /**
     * 订单状态（PENDING:待确认, COMPLETED:已完成, CANCELLED:已取消）
     */
    @Schema(description = "订单状态（PENDING:待确认, COMPLETED:已完成, CANCELLED:已取消）")
    @ExcelProperty(value = "订单状态（PENDING:待确认, COMPLETED:已完成, CANCELLED:已取消）")
    private String orderStatus;

    /**
     * 关联的学生会员卡ID（下单时创建空白卡）
     */
    @Schema(description = "关联的学生会员卡ID（下单时创建空白卡）")
    @ExcelProperty(value = "关联的学生会员卡ID（下单时创建空白卡）")
    private Long stuCardId;

    /**
     * 支付时间
     */
    @Schema(description = "支付时间")
    @ExcelProperty(value = "支付时间")
    private LocalDateTime paymentTime;

    /**
     * 确认时间
     */
    @Schema(description = "确认时间")
    @ExcelProperty(value = "确认时间")
    private LocalDateTime confirmTime;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 所属机构ID
     */
    @Schema(description = "所属机构ID")
    @ExcelProperty(value = "所属机构ID")
    private Long institutionId;
}