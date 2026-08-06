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
     * 所属代理商编码
     */
    @Schema(description = "所属代理商编码")
    @Length(max = 50, message = "代理商编码长度不能超过 {max} 个字符")
    private String agentCode;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    @NotBlank(message = "手机号码不能为空")
    @Length(max = 20, message = "手机号码长度不能超过 {max} 个字符")
    private String phone;

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
}