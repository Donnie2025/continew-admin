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

/**
 * 学生管理详情信息
 *
 * @author don
 * @since 2025/04/20 01:32
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "学生管理详情信息")
public class StudentDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 学生姓名
     */
    @Schema(description = "学生姓名")
    @ExcelProperty(value = "学生姓名")
    private String name;

    /**
     * 性别（0-未知 1-男 2-女）
     */
    @Schema(description = "性别（0-未知 1-男 2-女）")
    @ExcelProperty(value = "性别（0-未知 1-男 2-女）")
    private Integer gender;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    @ExcelProperty(value = "手机号码")
    private String phone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    @ExcelProperty(value = "邮箱")
    private String email;

    /**
     * 注册时间
     */
    @Schema(description = "注册时间")
    @ExcelProperty(value = "注册时间")
    private LocalDateTime registerTime;

    /**
     * 所属代理的ID
     */
    @Schema(description = "所属代理的ID")
    @ExcelProperty(value = "所属代理的ID")
    private Long agentId;

    /**
     * 头像地址
     */
    @Schema(description = "头像地址")
    @ExcelProperty(value = "头像地址")
    private String avatar;

    /**
     * 密码
     */
    @Schema(description = "密码")
    @ExcelProperty(value = "密码")
    private String password;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 状态（1：启用；2：禁用）
     */
    @Schema(description = "状态（1：启用；2：禁用）")
    @ExcelProperty(value = "状态（1：启用；2：禁用）")
    private Integer status;

    /**
     * 所属机构ID
     */
    @Schema(description = "所属机构ID")
    @ExcelProperty(value = "所属机构ID")
    private Long institutionId;
}