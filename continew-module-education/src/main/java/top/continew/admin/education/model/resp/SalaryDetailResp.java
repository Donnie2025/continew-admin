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
 * 薪资详情信息
 *
 * @author don
 * @since 2025/05/13 22:43
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "薪资详情信息")
public class SalaryDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教师ID
     */
    @Schema(description = "教师ID")
    @ExcelProperty(value = "教师ID")
    private Long teacherId;

    /**
     * 教师姓名
     */
    @Schema(description = "教师姓名")
    @ExcelProperty(value = "教师姓名")
    private String teacherName;

    /**
     * 起始日期
     */
    @Schema(description = "起始日期")
    @ExcelProperty(value = "起始日期")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @Schema(description = "结束日期")
    @ExcelProperty(value = "结束日期")
    private LocalDate endDate;

    /**
     * 课程总数
     */
    @Schema(description = "课程总数")
    @ExcelProperty(value = "课程总数")
    private Integer courseCount;

    /**
     * 课程总金额
     */
    @Schema(description = "课程总金额")
    @ExcelProperty(value = "课程总金额")
    private BigDecimal courseAmount;

    /**
     * 扣款金额
     */
    @Schema(description = "扣款金额")
    @ExcelProperty(value = "扣款金额")
    private BigDecimal deductionAmount;

    /**
     * 小费金额
     */
    @Schema(description = "小费金额")
    @ExcelProperty(value = "小费金额")
    private BigDecimal tipAmount;

    /**
     * 最终支付金额
     */
    @Schema(description = "最终支付金额")
    @ExcelProperty(value = "最终支付金额")
    private BigDecimal finalAmount;

    /**
     * 状态（0：失效；1：生效）
     */
    @Schema(description = "状态（0：失效；1：生效）")
    @ExcelProperty(value = "状态")
    private Integer status;

    /**
     * 是否结算（0：未结算；1：已结算）
     */
    @Schema(description = "是否结算（0：未结算；1：已结算）")
    @ExcelProperty(value = "是否结算")
    private Integer isSettled;

    /**
     * 单价
     */
    @Schema(description = "单价")
    @ExcelProperty(value = "单价")
    private Integer rate;

    /**
     * 所属组
     */
    @Schema(description = "所属组")
    @ExcelProperty(value = "所属组")
    private String groupName;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @ExcelProperty(value = "备注")
    private String remark;
}