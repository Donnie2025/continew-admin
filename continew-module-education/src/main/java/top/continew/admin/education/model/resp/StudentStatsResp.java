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

/**
 * 学生首页学习统计响应
 *
 * @author don
 * @since 2026/05
 */
@Data
@Builder
@Schema(description = "学生学习统计")
public class StudentStatsResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "剩余课程（所有启用次卡的 balance 之和）", example = "12")
    private Integer remaining;

    @Schema(description = "待上课程（当前时间之后的有效预约数量）", example = "3")
    private Integer pending;

    @Schema(description = "已完成课程（当前时间之前的有效预约数量）", example = "8")
    private Integer completed;

    @Schema(description = "固定课数量（edu_fixed_booking 中状态为启用的数量）", example = "2")
    private Integer fixedCount;

    @Schema(description = "是否提示余额不足（剩余课程 < 固定课数量 时为 true）", example = "false")
    private Boolean lowBalance;
}
