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
import java.time.LocalDateTime;
import java.util.List;

/**
 * 批量创建课节参数
 *
 * @author don
 * @since 2025/11/16
 */
@Data
@Schema(description = "批量创建课节参数")
public class BatchLessonReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 课程ID
     */
    @Schema(description = "课程ID")
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /**
     * 课节名称前缀
     */
    @Schema(description = "课节名称前缀")
    @NotBlank(message = "课节名称前缀不能为空")
    @Length(max = 50, message = "课节名称前缀长度不能超过 {max} 个字符")
    private String namePrefix;

    /**
     * 主讲教师ID
     */
    @Schema(description = "主讲教师ID")
    @NotNull(message = "主讲教师ID不能为空")
    private Long teacherId;

    /**
     * 课堂时长（分钟）
     */
    @Schema(description = "课堂时长（分钟）")
    @NotNull(message = "课堂时长不能为空")
    @Min(value = 1, message = "课堂时长必须大于0")
    private Long duration;

    /**
     * 上台人数
     */
    @Schema(description = "上台人数")
    @Min(value = 1, message = "上台人数必须大于0")
    private Integer seatNum = 1;

    /**
     * 录制状态（0：不录制；1：录制）
     */
    @Schema(description = "录制状态（0：不录制；1：录制）")
    @NotNull(message = "录制状态不能为空")
    private Integer recordState;

    /**
     * 直播状态（0：不直播；1：直播）
     */
    @Schema(description = "直播状态（0：不直播；1：直播）")
    private Integer liveState = 0;

    /**
     * 公开状态（0：不公开；1：公开）
     */
    @Schema(description = "公开状态（0：不公开；1：公开）")
    private Integer openState = 0;

    /**
     * 课节时间列表
     */
    @Schema(description = "课节时间列表")
    @NotEmpty(message = "课节时间列表不能为空")
    private List<LessonTimeReq> lessonTimes;

    /**
     * 课节时间请求参数
     */
    @Data
    @Schema(description = "课节时间请求参数")
    public static class LessonTimeReq implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 课节名称后缀（如：第1节、第2节等）
         */
        @Schema(description = "课节名称后缀")
        @NotBlank(message = "课节名称后缀不能为空")
        private String nameSuffix;

        /**
         * 活动开始时间
         */
        @Schema(description = "活动开始时间")
        @NotNull(message = "活动开始时间不能为空")
        private LocalDateTime startTime;
    }
}
