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
 * 创建或修改Classin用户参数
 *
 * @author donnie
 * @since 2025/04/12 20:49
 */
@Data
@Schema(description = "创建或修改Classin用户参数")
public class ClassinUserReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    @NotBlank(message = "昵称不能为空")
    @Length(max = 50, message = "昵称长度不能超过 {max} 个字符")
    private String nickname;

    /**
     * 成员类型（0：不是成员；1：学生；2：老师）
     */
    @Schema(description = "成员类型（0：不是成员；1：学生；2：老师）")
    @NotNull(message = "成员类型（0：不是成员；1：学生；2：老师）不能为空")
    private Integer userType;

    /**
     * Classin唯一映射关系
     */
    @Schema(description = "Classin唯一映射关系")
    @Length(max = 50, message = "Classin唯一映射关系长度不能超过 {max} 个字符")
    private String uid;

    /**
     * 密码
     */
    @Schema(description = "密码")
    @Length(max = 100, message = "密码长度不能超过 {max} 个字符")
    private String password;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    @Length(max = 20, message = "手机号长度不能超过 {max} 个字符")
    private String telephone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    @Length(max = 100, message = "邮箱长度不能超过 {max} 个字符")
    private String email;

    /**
     * 关联Classin机构ID
     */
    @Schema(description = "关联Classin机构ID")
    @NotNull(message = "关联Classin机构ID不能为空")
    private Long classinInstitutionId;

    /**
     * 关联学生ID
     */
    @Schema(description = "关联学生ID")
    private Long studentId;
}