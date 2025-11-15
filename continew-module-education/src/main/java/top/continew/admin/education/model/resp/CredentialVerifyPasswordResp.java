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
 * 验证凭证密码响应
 *
 * @author don
 * @since 2025/11/14
 */
@Data
@Builder
@Schema(description = "验证凭证密码响应")
public class CredentialVerifyPasswordResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 验证是否成功
     */
    @Schema(description = "验证是否成功")
    private Boolean success;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户姓名
     */
    @Schema(description = "用户姓名")
    private String userName;

    /**
     * 用户类型
     */
    @Schema(description = "用户类型")
    private String userType;

    /**
     * 错误次数
     */
    @Schema(description = "错误次数")
    private Integer errorCount;

    /**
     * 剩余尝试次数
     */
    @Schema(description = "剩余尝试次数")
    private Integer remainingAttempts;

    /**
     * 是否被冻结
     */
    @Schema(description = "是否被冻结")
    private Boolean frozen;

    /**
     * 解冻时间
     */
    @Schema(description = "解冻时间")
    private LocalDateTime unfreezeTime;

    /**
     * 响应消息
     */
    @Schema(description = "响应消息")
    private String message;
}
