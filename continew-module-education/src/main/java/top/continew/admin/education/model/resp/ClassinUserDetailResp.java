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
 * Classin用户详情信息
 *
 * @author donnie
 * @since 2025/04/12 20:49
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "Classin用户详情信息")
public class ClassinUserDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    @ExcelProperty(value = "昵称")
    private String nickname;

    /**
     * 成员类型（0：不是成员；1：学生；2：老师）
     */
    @Schema(description = "成员类型（0：不是成员；1：学生；2：老师）")
    @ExcelProperty(value = "成员类型（0：不是成员；1：学生；2：老师）")
    private Integer userType;

    /**
     * Classin唯一映射关系
     */
    @Schema(description = "Classin唯一映射关系")
    @ExcelProperty(value = "Classin唯一映射关系")
    private String uid;

    /**
     * 密码
     */
    @Schema(description = "密码")
    @ExcelProperty(value = "密码")
    private String password;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    @ExcelProperty(value = "手机号")
    private String telephone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    @ExcelProperty(value = "邮箱")
    private String email;

    /**
     * 关联学生ID
     */
    @Schema(description = "关联学生ID")
    @ExcelProperty(value = "关联学生ID")
    private Long studentId;

    /**
     * 关联教师ID
     */
    @Schema(description = "关联教师ID")
    @ExcelProperty(value = "关联教师ID")
    private Long teacherId;

    /**
     * 关联Classin机构ID
     */
    @Schema(description = "关联Classin机构ID")
    @ExcelProperty(value = "关联Classin机构ID")
    private Long classinInstitutionId;

    /**
     * 状态（1：启用；2：禁用）
     */
    @Schema(description = "状态（1：启用；2：禁用）")
    @ExcelProperty(value = "状态（1：启用；2：禁用）")
    private Integer status;
}