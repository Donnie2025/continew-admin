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
 * 课堂创建或修改参数
 *
 * @author don
 * @since 2025/06/24 23:39
 */
@Data
@Schema(description = "课堂创建或修改参数")
public class LessonReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 课程ID
     */
    @Schema(description = "课程ID")
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /**
     * ClassIn 课程ID
     */
    @Schema(description = "ClassIn 课程ID")
    @NotNull(message = "ClassIn 课程ID不能为空")
    private Long courseUid;

    /**
     * 课堂活动名称
     */
    @Schema(description = "课堂活动名称")
    @NotBlank(message = "课堂活动名称不能为空")
    @Length(max = 50, message = "课堂活动名称长度不能超过 {max} 个字符")
    private String name;

    /**
     * 主讲教师UID
     */
    @Schema(description = "主讲教师ID")
    @NotNull(message = "主讲教师ID不能为空")
    private Long teacherId;

    /**
     * 活动开始时间
     */
    @Schema(description = "活动开始时间")
    @NotNull(message = "活动开始时间不能为空")
    private LocalDateTime startTime;

    /**
     * 课堂时长（分钟）
     */
    @Schema(description = "课堂时长（分钟）")
    @NotNull(message = "课堂时长不能为空")
    @Min(value = 10, message = "课堂时长至少为10分钟")
    private Long duration;

    /**
     * 上台人数
     */
    @Schema(description = "上台人数")
    private Integer seatNum;

    /**
     * 录制状态
     */
    @Schema(description = "录制状态")
    private Integer recordState;

    /**
     * 直播状态
     */
    @Schema(description = "直播状态")
    private Integer liveState;

    /**
     * 公开状态
     */
    @Schema(description = "公开状态")
    private Integer openState;
}