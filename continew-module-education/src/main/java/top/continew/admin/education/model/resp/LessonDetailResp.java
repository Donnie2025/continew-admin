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

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;
import java.io.Serial;
import java.time.*;

/**
 * 课堂详情信息
 *
 * @author don
 * @since 2025/06/24 23:39
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "课堂详情信息")
public class LessonDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 课程ID
     */
    @Schema(description = "课程ID")
    @ExcelProperty(value = "课程ID")
    private Long courseId;

    /**
     * ClassIn 课程ID
     */
    @Schema(description = "ClassIn 课程ID")
    @ExcelProperty(value = "ClassIn 课程ID")
    private Long courseUid;

    /**
     * ClassIn 活动ID
     */
    @Schema(description = "ClassIn 活动ID")
    @ExcelProperty(value = "ClassIn 活动ID")
    private Long activityUid;

    /**
     * ClassIn 课堂ID
     */
    @Schema(description = "ClassIn 课堂ID")
    @ExcelProperty(value = "ClassIn 课堂ID")
    private Long classUid;

    /**
     * 单元ID
     */
    @Schema(description = "单元ID")
    @ExcelProperty(value = "单元ID")
    private Long unitUid;

    /**
     * 课堂活动名称
     */
    @Schema(description = "课堂活动名称")
    @ExcelProperty(value = "课堂活动名称")
    private String name;

    /**
     * 主讲教师UID
     */
    @Schema(description = "主讲教师UID")
    @ExcelProperty(value = "主讲教师UID")
    private Long teacherUid;

    /**
     * 活动开始时间
     */
    @Schema(description = "活动开始时间")
    @ExcelProperty(value = "活动开始时间")
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    @Schema(description = "活动结束时间")
    @ExcelProperty(value = "活动结束时间")
    private LocalDateTime endTime;

    /**
     * 上台人数
     */
    @Schema(description = "上台人数")
    @ExcelProperty(value = "上台人数")
    private Integer seatNum;

    /**
     * 录制状态
     */
    @Schema(description = "录制状态")
    @ExcelProperty(value = "录制状态")
    private Integer recordState;

    /**
     * 直播状态
     */
    @Schema(description = "直播状态")
    @ExcelProperty(value = "直播状态")
    private Integer liveState;

    /**
     * 公开状态
     */
    @Schema(description = "公开状态")
    @ExcelProperty(value = "公开状态")
    private Integer openState;

    /**
     * 课堂唯一标识
     */
    @Schema(description = "课堂唯一标识")
    @ExcelProperty(value = "课堂唯一标识")
    private String uniqueIdentity;

    /**
     * 课堂直播播放器地址
     */
    @Schema(description = "课堂直播播放器地址")
    @ExcelProperty(value = "课堂直播播放器地址")
    private String liveUrl;

    /**
     * RTMP协议的拉流地址
     */
    @Schema(description = "RTMP协议的拉流地址")
    @ExcelProperty(value = "RTMP协议的拉流地址")
    private String rtmpUrl;

    /**
     * HLS协议的拉流地址
     */
    @Schema(description = "HLS协议的拉流地址")
    @ExcelProperty(value = "HLS协议的拉流地址")
    private String hlsUrl;

    /**
     * FLV协议的拉流地址
     */
    @Schema(description = "FLV协议的拉流地址")
    @ExcelProperty(value = "FLV协议的拉流地址")
    private String flvUrl;

    /**
     * 状态
     */
    @Schema(description = "状态")
    @ExcelProperty(value = "状态")
    private Integer status;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    @ExcelProperty(value = "创建者")
    private String createBy;

    /**
     * 更新者
     */
    @Schema(description = "更新者")
    @ExcelProperty(value = "更新者")
    private String updateBy;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @ExcelProperty(value = "备注")
    private String remark;
}