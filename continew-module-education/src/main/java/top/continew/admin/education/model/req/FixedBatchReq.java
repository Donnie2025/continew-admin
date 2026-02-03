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
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 批量创建固定课信息
 *
 * @author Charles7c
 * @since 2024/11/17 22:00
 */
@Data
@Schema(description = "批量创建固定课信息")
public class FixedBatchReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教师ID
     */
    @Schema(description = "教师ID", example = "1")
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;

    /**
     * 星期列表：1-周一，2-周二，3-周三，4-周四，5-周五，6-周六，7-周日
     */
    @Schema(description = "星期列表", example = "[1,2,3]")
    @NotEmpty(message = "星期列表不能为空")
    private List<Integer> weekDays;

    /**
     * 开始时间列表（HH:MM格式）
     */
    @Schema(description = "开始时间列表", example = "[\"08:00\",\"09:00\"]")
    @NotEmpty(message = "开始时间列表不能为空")
    private List<String> startTimes;

    /**
     * 课程时长（分钟）
     */
    @Schema(description = "课程时长（分钟）", example = "60")
    @NotNull(message = "课程时长不能为空")
    private Integer durationMinutes;

    /**
     * 最大学生数
     */
    @Schema(description = "最大学生数", example = "1")
    @NotNull(message = "最大学生数不能为空")
    private Integer maxStudents;
}
