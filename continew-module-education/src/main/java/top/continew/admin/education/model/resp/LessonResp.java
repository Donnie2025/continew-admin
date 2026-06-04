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

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;
import java.io.Serial;
import java.time.*;

/**
 * 课堂信息
 *
 * @author don
 * @since 2025/06/24 23:39
 */
@Data
@Schema(description = "课堂信息")
public class LessonResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 课程ID
     */
    @Schema(description = "课程ID")
    private Long courseId;

    /**
     * ClassIn 课程ID
     */
    @Schema(description = "ClassIn 课程ID")
    private Long courseUid;

    /**
     * 所属代理机构编码
     */
    @Schema(description = "所属代理机构编码")
    private String agentCode;

    /**
     * ClassIn 活动ID
     */
    @Schema(description = "ClassIn 活动ID")
    private Long activityUid;

    /**
     * ClassIn 课堂ID
     */
    @Schema(description = "ClassIn 课堂ID")
    private Long classUid;

    /**
     * 单元ID
     */
    @Schema(description = "单元ID")
    private Long unitUid;

    /**
     * 课堂活动名称
     */
    @Schema(description = "课堂活动名称")
    private String name;

    /**
     * 主讲教师UID
     */
    @Schema(description = "主讲教师UID")
    private Long teacherUid;

    /**
     * 主讲教师ID
     */
    @Schema(description = "主讲教师ID")
    private Long teacherId;

    /**
     * 主讲教师名字
     */
    @Schema(description = "主讲教师名字")
    private String teacherName;

    /**
     * 活动开始时间
     */
    @Schema(description = "活动开始时间")
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    @Schema(description = "活动结束时间")
    private LocalDateTime endTime;

    /**
     * 课堂时长（分钟）
     */
    @Schema(description = "课堂时长（分钟）")
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

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 预估费用（元）
     */
    @Schema(description = "预估费用（元）")
    private Double estimatedCost;

    /**
     * 关联教材ID
     */
    @Schema(description = "关联教材ID")
    private Long materialId;

    /**
     * 关联教材名称
     */
    @Schema(description = "关联教材名称")
    private String materialName;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}