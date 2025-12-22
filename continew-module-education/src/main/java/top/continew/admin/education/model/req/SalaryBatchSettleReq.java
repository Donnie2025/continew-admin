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
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 薪资批量结算请求参数
 *
 * @author don
 * @since 2024/12/21 22:30
 */
@Data
@Schema(description = "薪资批量结算请求参数")
public class SalaryBatchSettleReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 薪资记录ID列表
     */
    @Schema(description = "薪资记录ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "薪资记录ID列表不能为空")
    private List<Long> ids;

    /**
     * 备注信息
     */
    @Schema(description = "批量结算备注信息")
    private String remark;
}
