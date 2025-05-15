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
import java.time.*;

/**
 * 订单创建或修改参数
 *
 * @author don
 * @since 2025/05/10 22:11
 */
@Data
@Schema(description = "订单创建或修改参数")
public class TransactionReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 学生会员卡绑定表ID
     */
    @Schema(description = "学生会员卡绑定表ID")
    @NotNull(message = "学生会员卡绑定表ID不能为空")
    private Long stuCardId;

    /**
     * 学生ID
     */
    @Schema(description = "学生ID")
    @NotNull(message = "学生ID不能为空")
    private Long stuId;

    /**
     * 会员卡ID
     */
    @Schema(description = "会员卡ID")
    @NotNull(message = "会员卡ID不能为空")
    private Long cardId;

    /**
     * 变动类型（credit:充值, debit:扣费, freeze:冻结, activate:激活, cancel:取消约课, bind:首次绑卡, book_debit:约课扣费）
     */
    @Schema(description = "变动类型（credit:充值, debit:扣费, freeze:冻结, activate:激活, cancel:取消约课, bind:首次绑卡, book_debit:约课扣费）")
    @NotBlank(message = "变动类型（credit:充值, debit:扣费, freeze:冻结, activate:激活, cancel:取消约课, bind:首次绑卡, book_debit:约课扣费）不能为空")
    @Length(max = 32, message = "变动类型（credit:充值, debit:扣费, freeze:冻结, activate:激活, cancel:取消约课, bind:首次绑卡, book_debit:约课扣费）长度不能超过 {max} 个字符")
    private String type;
}