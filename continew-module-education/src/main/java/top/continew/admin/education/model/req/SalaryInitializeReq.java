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

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 薪资初始化请求
 *
 * @author don
 * @since 2025/10/25
 */
@Data
@Schema(description = "薪资初始化请求")
public class SalaryInitializeReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 起始日期（可选，默认为本周一）
     */
    @Schema(description = "起始日期")
    private LocalDate startDate;

    /**
     * 结束日期（可选，默认为本周日）
     */
    @Schema(description = "结束日期")
    private LocalDate endDate;
}
