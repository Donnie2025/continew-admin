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
import java.time.LocalDateTime;
import java.util.List;

/**
 * 固定课响应
 *
 * @author Charles7c
 * @since 2024/12/28 17:30
 */
@Data
@Schema(description = "固定课响应")
public class FixedResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

    /**
     * 教师ID
     */
    @Schema(description = "教师ID", example = "1")
    private Long teacherId;

    /**
     * 教师姓名
     */
    @Schema(description = "教师姓名", example = "张老师")
    private String teacherName;

    /**
     * 教师头像
     */
    @Schema(description = "教师头像")
    private String teacherAvatar;

    /**
     * 教师标签（# 号分隔）
     */
    @Schema(description = "教师标签", example = "#日常口语#发音纠正")
    private String teacherTags;

    /**
     * 课程时长（分钟）
     */
    @Schema(description = "课程时长（分钟）", example = "25")
    private Integer durationMinutes;

    /**
     * 最大学生数
     */
    @Schema(description = "最大学生数", example = "1")
    private Integer maxStudents;

    /**
     * 星期几：1-周一，2-周二，3-周三，4-周四，5-周五，6-周六，7-周日
     */
    @Schema(description = "星期几：1-周一，2-周二，3-周三，4-周四，5-周五，6-周六，7-周日", example = "1")
    private Integer weekDay;

    /**
     * 开始时间（HH:MM格式）
     */
    @Schema(description = "开始时间（HH:MM格式）", example = "19:00")
    private String startTime;

    /**
     * 状态：0-禁用，1-启用
     */
    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    /**
     * 已预约学生数
     */
    @Schema(description = "已预约学生数", example = "0")
    private Integer bookedCount;

    /**
     * 已预约学生姓名列表
     */
    @Schema(description = "已预约学生姓名列表")
    private List<String> studentNames;

    /**
     * 已预约学生手机号列表
     */
    @Schema(description = "已预约学生手机号列表")
    private List<String> studentPhones;

    /**
     * 是否已被当前学生预约（仅用于学生端）
     */
    @Schema(description = "是否已被当前学生预约", example = "false")
    private Boolean isBooked;

    /**
     * 是否是当前学生的预约（仅用于学生端）
     */
    @Schema(description = "是否是当前学生的预约", example = "false")
    private Boolean isMyBooking;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
