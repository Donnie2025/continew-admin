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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 凭证状态响应
 *
 * @author don
 * @since 2025/11/14
 */
@Data
@Builder
@Schema(description = "凭证状态响应")
public class CredentialStatusResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户类型
     */
    @Schema(description = "用户类型")
    private String userType;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    private String phone;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    private Boolean isActive;

    /**
     * 错误次数
     */
    @Schema(description = "错误次数")
    private Integer errorCount;

    /**
     * 最后错误时间
     */
    @Schema(description = "最后错误时间")
    private LocalDateTime lastErrorTime;

    /**
     * 是否被冻结
     */
    @Schema(description = "是否被冻结")
    private Boolean isFrozen;

    /**
     * 冻结到期时间
     */
    @Schema(description = "冻结到期时间")
    private LocalDateTime freezeUntil;

    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    /**
     * 密码最后更新时间
     */
    @Schema(description = "密码最后更新时间")
    private LocalDateTime passwordUpdatedTime;
}
