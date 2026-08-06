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
 * 教师注册请求参数
 *
 * @author donnie
 * @since 2026/06/17
 */
@Data
@Schema(description = "教师注册请求参数")
public class TeacherRegisterReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教师姓名
     */
    @Schema(description = "教师姓名")
    @NotBlank(message = "教师姓名不能为空")
    @Length(max = 50, message = "教师姓名长度不能超过 {max} 个字符")
    private String name;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    @NotBlank(message = "手机号码不能为空")
    @Length(max = 20, message = "手机号码长度不能超过 {max} 个字符")
    private String phone;

    /**
     * 登录密码（Base64编码）
     */
    @Schema(description = "登录密码")
    @NotBlank(message = "登录密码不能为空")
    @Length(max = 200, message = "登录密码长度不能超过 {max} 个字符")
    private String password;

    /**
     * Classin账号
     */
    @Schema(description = "Classin账号")
    @Length(max = 100, message = "Classin账号长度不能超过 {max} 个字符")
    private String classinAccount;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    @Length(max = 100, message = "邮箱长度不能超过 {max} 个字符")
    private String email;

    /**
     * 头像地址
     */
    @Schema(description = "头像地址")
    @NotBlank(message = "头像不能为空")
    @Length(max = 512, message = "头像地址长度不能超过 {max} 个字符")
    private String avatar;

    /**
     * 视频地址
     */
    @Schema(description = "视频地址")
    @NotBlank(message = "介绍视频不能为空")
    @Length(max = 512, message = "视频地址长度不能超过 {max} 个字符")
    private String videoUrl;

    /**
     * 描述（专业背景、教学经验等）
     */
    @Schema(description = "描述")
    @NotBlank(message = "描述不能为空")
    @Length(max = 1024, message = "描述长度不能超过 {max} 个字符")
    private String description;

    /**
     * 支付渠道（GCash、Maya、Bank）
     */
    @Schema(description = "支付渠道")
    @Length(max = 20, message = "支付渠道长度不能超过 {max} 个字符")
    private String paymentChannel;

    /**
     * 账号
     */
    @Schema(description = "账号")
    @Length(max = 100, message = "账号长度不能超过 {max} 个字符")
    private String accountNumber;

    /**
     * 账户名
     */
    @Schema(description = "账户名")
    @Length(max = 100, message = "账户名长度不能超过 {max} 个字符")
    private String accountName;

    /**
     * 收款二维码地址
     */
    @Schema(description = "收款二维码地址")
    @Length(max = 512, message = "收款二维码地址长度不能超过 {max} 个字符")
    private String qrCode;

    /**
     * 银行名称（当payment_channel为Bank时使用）
     */
    @Schema(description = "银行名称")
    @Length(max = 100, message = "银行名称长度不能超过 {max} 个字符")
    private String bankName;

    /**
     * 性别（male-男 female-女）
     */
    @Schema(description = "性别（male-男 female-女）")
    private String gender;
}
