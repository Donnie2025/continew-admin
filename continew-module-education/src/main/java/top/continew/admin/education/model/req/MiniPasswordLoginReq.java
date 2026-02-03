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

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 小程序密码登录请求参数
 *
 * @author don
 * @since 2025/1/4 16:00
 */
@Data
@Schema(description = "小程序密码登录请求参数")
public class MiniPasswordLoginReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     */
    @Schema(description = "手机号", example = "13601270199")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 密码（Base64编码）
     */
    @Schema(description = "密码（Base64编码）", example = "MTIzNDU2")
    @NotBlank(message = "密码不能为空")
    @Length(max = 200, message = "密码长度不能超过 {max} 个字符")
    private String password;

    /**
     * 用户类型
     */
    @Schema(description = "用户类型", example = "student", allowableValues = {"student", "teacher"})
    @NotBlank(message = "用户类型不能为空")
    private String userType;
}
