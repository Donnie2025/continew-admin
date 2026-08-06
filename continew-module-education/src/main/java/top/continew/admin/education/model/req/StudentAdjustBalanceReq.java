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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 学生余额调整请求
 *
 * @author don
 * @since 2026/07/05
 */
@Data
@Schema(description = "学生余额调整请求")
public class StudentAdjustBalanceReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 调整金额（课时数量）
     */
    @Schema(description = "调整金额（课时数量）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "调整金额不能为空")
    @Positive(message = "调整金额必须大于0")
    private BigDecimal amount;

    /**
     * 现金金额
     */
    @Schema(description = "现金金额（充值时必填）")
    private BigDecimal cashAmount;

    /**
     * 调整类型：INCREASE-充值增加，DECREASE-扣费减少
     */
    @Schema(description = "调整类型：INCREASE-充值增加，DECREASE-扣费减少", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "调整类型不能为空")
    private String type;

    /**
     * 备注说明
     */
    @Schema(description = "备注说明")
    private String remark;
}
