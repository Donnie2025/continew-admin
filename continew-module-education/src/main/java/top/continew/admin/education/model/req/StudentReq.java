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
import org.springframework.web.multipart.MultipartFile;
import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 学生管理创建或修改参数
 *
 * @author don
 * @since 2025/04/20 01:32
 */
@Data
@Schema(description = "学生管理创建或修改参数")
public class StudentReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 学生姓名
     */
    @Schema(description = "学生姓名")
    @NotBlank(message = "学生姓名不能为空")
    @Length(max = 50, message = "学生姓名长度不能超过 {max} 个字符")
    private String name;

    /**
     * 性别（male-男 female-女）
     */
    @Schema(description = "性别（male-男 female-女）")
    private String gender;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    @NotBlank(message = "手机号码不能为空")
    @Length(max = 20, message = "手机号码长度不能超过 {max} 个字符")
    private String phone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    @Length(max = 100, message = "邮箱长度不能超过 {max} 个字符")
    private String email;

    /**
     * 所属代理的ID
     */
    @Schema(description = "所属代理的ID")
    private Long agentId;

    /**
     * 头像文件
     */
    @Schema(description = "头像文件")
    private MultipartFile avatarFile;

    /**
     * 头像地址
     */
    @Schema(description = "头像地址")
    @Length(max = 512, message = "头像地址长度不能超过 {max} 个字符")
    private String avatar;

    /**
     * 密码
     */
    @Schema(description = "密码")
    @Length(max = 100, message = "密码长度不能超过 {max} 个字符")
    private String password;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @Length(max = 1024, message = "备注长度不能超过 {max} 个字符")
    private String remark;

    /**
     * 所属机构ID
     */
    @Schema(description = "所属机构ID")
    private Long institutionId;
}