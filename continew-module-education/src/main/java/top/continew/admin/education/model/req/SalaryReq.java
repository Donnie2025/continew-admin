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

package top.continew.admin.education.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;
import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.math.BigDecimal;

/**
 * 薪资创建或修改参数
 *
 * @author don
 * @since 2025/05/13 22:43
 */
@Data
@Schema(description = "薪资创建或修改参数")
public class SalaryReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教师ID
     */
    @Schema(description = "教师ID")
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;

    /**
     * 教师姓名
     */
    @Schema(description = "教师姓名")
    @Length(max = 50, message = "教师姓名长度不能超过 {max} 个字符")
    private String teacherName;

    /**
     * 起始日期
     */
    @Schema(description = "起始日期")
    @NotNull(message = "起始日期不能为空")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @Schema(description = "结束日期")
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    /**
     * 课程总数
     */
    @Schema(description = "课程总数")
    @NotNull(message = "课程总数不能为空")
    private Integer courseCount;

    /**
     * 课程总金额
     */
    @Schema(description = "课程总金额")
    private BigDecimal courseAmount;

    /**
     * 扣款金额
     */
    @Schema(description = "扣款金额")
    private BigDecimal deductionAmount;

    /**
     * 小费金额
     */
    @Schema(description = "小费金额")
    private BigDecimal tipAmount;

    /**
     * 状态（0：失效；1：生效）
     */
    @Schema(description = "状态（0：失效；1：生效）")
    private Integer status;

    /**
     * 是否结算（0：未结算；1：已结算）
     */
    @Schema(description = "是否结算（0：未结算；1：已结算）")
    private Integer isSettled;

    /**
     * 单价
     */
    @Schema(description = "单价")
    private Integer rate;

    /**
     * 所属组
     */
    @Schema(description = "所属组")
    @Length(max = 100, message = "所属组长度不能超过 {max} 个字符")
    private String groupName;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @Length(max = 255, message = "备注长度不能超过 {max} 个字符")
    private String remark;
}