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
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 账户信息响应
 *
 * @author don
 * @since 2026/06/09
 */
@Data
@Schema(description = "账户信息")
public class AccountResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 账户ID
     */
    @Schema(description = "账户ID")
    private Long id;

    /**
     * 账户类型编码
     */
    @Schema(description = "账户类型编码")
    private String accountType;

    /**
     * 账户类型名称
     */
    @Schema(description = "账户类型名称")
    private String accountTypeName;

    /**
     * 余额
     */
    @Schema(description = "余额")
    private BigDecimal balance;

    /**
     * 到期日期
     */
    @Schema(description = "到期日期")
    private LocalDate expireDate;

    /**
     * 状态（1：正常；0：禁用）
     */
    @Schema(description = "状态")
    private Integer status;
}
