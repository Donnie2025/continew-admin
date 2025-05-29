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

/**
 * 预约创建或修改参数
 *
 * @author don
 * @since 2025/05/23 23:25
 */
@Data
@Schema(description = "预约创建或修改参数")
public class BookingReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属课程ID
     */
    @Schema(description = "所属课程ID")
    private Long slotId;

    /**
     * 所属学生ID
     */
    @Schema(description = "所属学生ID")
    private Long studentId;

    /**
     * 学生会员卡ID
     */
    @Schema(description = "学生会员卡ID")
    private Long stuCardId;

    /**
     * 教师ID
     */
    @Schema(description = "教师ID")
    private Long teacherId;

    /**
     * 
     */
    @Schema(description = "")
    @Length(max = 8, message = "长度不能超过 {max} 个字符")
    private String startDate;

    /**
     * 
     */
    @Schema(description = "")
    @Length(max = 5, message = "长度不能超过 {max} 个字符")
    private String startTime;

    /**
     * 所属学生姓名
     */
    @Schema(description = "所属学生姓名")
    @Length(max = 50, message = "所属学生姓名长度不能超过 {max} 个字符")
    private String studentName;

    /**
     * 预约手机号
     */
    @Schema(description = "预约手机号")
    @Length(max = 20, message = "预约手机号长度不能超过 {max} 个字符")
    private String phone;

    /**
     * 预约会员卡名称
     */
    @Schema(description = "预约会员卡名称")
    @Length(max = 100, message = "预约会员卡名称长度不能超过 {max} 个字符")
    private String cardName;

    /**
     * 操作人名字
     */
    @Schema(description = "操作人名字")
    @Length(max = 50, message = "操作人名字长度不能超过 {max} 个字符")
    private String operatorName;

    /**
     * 操作时间
     */
    @Schema(description = "操作时间")
    private LocalDateTime operateTime;

    /**
     * 教材ID
     */
    @Schema(description = "教材ID")
    private Long materialId;

    /**
     * 预约教材名字
     */
    @Schema(description = "预约教材名字")
    @Length(max = 100, message = "预约教材名字长度不能超过 {max} 个字符")
    private String materialName;

    /**
     * 预约备注
     */
    @Schema(description = "预约备注")
    @Length(max = 1024, message = "预约备注长度不能超过 {max} 个字符")
    private String remark;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    @NotNull(message = "创建人不能为空")
    private Long createUser;
}