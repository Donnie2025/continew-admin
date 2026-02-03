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
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serial;
import java.io.Serializable;

/**
 * 固定课创建请求
 *
 * @author Charles7c
 * @since 2024/12/28 17:15
 */
@Schema(description = "固定课创建请求")
public class FixedReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教师ID
     */
    @Schema(description = "教师ID", example = "1")
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;

    /**
     * 课程时长（分钟）
     */
    @Schema(description = "课程时长（分钟）", example = "25")
    @NotNull(message = "课程时长不能为空")
    @Min(value = 10, message = "课程时长不能少于10分钟")
    @Max(value = 120, message = "课程时长不能超过120分钟")
    private Integer durationMinutes;

    /**
     * 最大学生数
     */
    @Schema(description = "最大学生数", example = "1")
    @NotNull(message = "最大学生数不能为空")
    @Min(value = 1, message = "最大学生数不能少于1")
    @Max(value = 10, message = "最大学生数不能超过10")
    private Integer maxStudents;

    /**
     * 星期几：1-周一，2-周二，3-周三，4-周四，5-周五，6-周六，7-周日
     */
    @Schema(description = "星期几：1-周一，2-周二，3-周三，4-周四，5-周五，6-周六，7-周日", example = "1")
    @NotNull(message = "星期几不能为空")
    @Min(value = 1, message = "星期几必须在1-7之间")
    @Max(value = 7, message = "星期几必须在1-7之间")
    private Integer weekDay;

    /**
     * 开始时间（HH:MM格式）
     */
    @Schema(description = "开始时间（HH:MM格式）", example = "19:00")
    @NotNull(message = "开始时间不能为空")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "开始时间格式错误，应为HH:MM格式")
    @Length(max = 5, message = "开始时间长度不能超过5个字符")
    private String startTime;

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }

    public Integer getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(Integer weekDay) {
        this.weekDay = weekDay;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
