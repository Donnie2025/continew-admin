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

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 学生密码验证请求参数
 *
 * @author don
 * @since 2025/11/14
 */
@Data
@Schema(description = "学生密码验证请求参数")
public class StudentVerifyPasswordReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    @NotBlank(message = "手机号码不能为空")
    @Length(max = 20, message = "手机号码长度不能超过 {max} 个字符")
    private String phone;

    /**
     * 密码（Base64编码）
     */
    @Schema(description = "密码（Base64编码）")
    @NotBlank(message = "密码不能为空")
    @Length(max = 200, message = "密码长度不能超过 {max} 个字符")
    private String password;
}
