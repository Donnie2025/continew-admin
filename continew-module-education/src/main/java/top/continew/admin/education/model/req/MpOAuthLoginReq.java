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

/**
 * 微信公众号 OAuth 登录请求参数
 *
 * @author don
 * @since 2025/05/02
 */
@Data
@Schema(description = "微信公众号 OAuth 登录请求参数")
public class MpOAuthLoginReq {

    @Schema(description = "微信授权 code", example = "061abcde1234567890")
    @NotBlank(message = "授权 code 不能为空")
    private String code;

    @Schema(description = "用户昵称（可选）", example = "张三")
    private String nickname;

    @Schema(description = "用户头像 URL（可选）", example = "https://thirdwx.qlogo.cn/xxx")
    private String avatar;
}
