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

/**
 * 我的预约信息
 *
 * @author continew-org
 * @since 2026-01-09
 */
@Data
@Schema(description = "我的预约信息")
public class MyBookingResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 预约ID
     */
    @Schema(description = "预约ID")
    private Long id;

    /**
     * 课程时间段ID
     */
    @Schema(description = "课程时间段ID")
    private Long slotId;

    /**
     * 预约日期（YYYYMMDD格式）
     */
    @Schema(description = "预约日期")
    private String slotDate;

    /**
     * 预约时间（HH:MM格式）
     */
    @Schema(description = "预约时间")
    private String slotTime;

    /**
     * 教师ID
     */
    @Schema(description = "教师ID")
    private Long teacherId;

    /**
     * 教师姓名
     */
    @Schema(description = "教师姓名")
    private String teacherName;

    /**
     * 教师头像
     */
    @Schema(description = "教师头像")
    private String teacherAvatar;

    /**
     * 会员卡ID
     */
    @Schema(description = "会员卡ID")
    private Long cardId;

    /**
     * 会员卡名称
     */
    @Schema(description = "会员卡名称")
    private String cardName;

    /**
     * 教材ID
     */
    @Schema(description = "教材ID")
    private Long materialId;

    /**
     * 教材名称
     */
    @Schema(description = "教材名称")
    private String materialName;

    /**
     * 教材级别
     */
    @Schema(description = "教材级别")
    private String materialLevel;

    /**
     * 课节ID
     */
    @Schema(description = "课节ID")
    private Long lessonId;

    /**
     * 课节名称
     */
    @Schema(description = "课节名称")
    private String lessonName;

    /**
     * 预约备注
     */
    @Schema(description = "预约备注")
    private String remark;

    /**
     * 预约状态（已完成、未开课、已取消等）
     */
    @Schema(description = "预约状态")
    private String bookingStatus;

    /**
     * 格式化的日期时间（用于前端显示）
     */
    @Schema(description = "格式化的日期时间")
    private String formattedDateTime;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
