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

import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学生密码验证响应
 *
 * @author don
 * @since 2025/11/14
 */
@Data
@Builder
@Schema(description = "学生密码验证响应")
public class StudentVerifyPasswordResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 验证是否成功
     */
    @Schema(description = "验证是否成功")
    private Boolean success;

    /**
     * 学生ID（验证成功时返回）
     */
    @Schema(description = "学生ID")
    private Long studentId;

    /**
     * 学生姓名（验证成功时返回）
     */
    @Schema(description = "学生姓名")
    private String studentName;

    /**
     * 错误次数
     */
    @Schema(description = "当前错误次数")
    private Integer errorCount;

    /**
     * 剩余尝试次数
     */
    @Schema(description = "剩余尝试次数")
    private Integer remainingAttempts;

    /**
     * 是否被冻结
     */
    @Schema(description = "账户是否被冻结")
    private Boolean frozen;

    /**
     * 冻结解除时间
     */
    @Schema(description = "冻结解除时间")
    private LocalDateTime unfreezeTime;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    private String message;
}
