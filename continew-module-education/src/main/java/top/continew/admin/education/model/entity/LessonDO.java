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

package top.continew.admin.education.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.time.*;

/**
 * 课堂实体
 *
 * @author don
 * @since 2025/06/24 23:39
 */
@Data
@TableName("edu_lesson")
public class LessonDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * ClassIn 课程ID
     */
    private Long courseUid;

    /**
     * ClassIn 活动ID
     */
    private Long activityUid;

    /**
     * ClassIn 课堂ID
     */
    private Long classUid;

    /**
     * 单元ID
     */
    private Long unitUid;

    /**
     * 课堂活动名称
     */
    private String name;

    /**
     * 主讲教师ID
     */
    private Long teacherId;

    /**
     * 主讲教师UID
     */
    private Long teacherUid;

    /**
     * 主讲教师名字
     */
    private String teacherName;

    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;

    /**
     * 课堂时长（分钟）
     */
    private Long duration;

    /**
     * 上台人数
     */
    private Integer seatNum;

    /**
     * 录制状态
     */
    private Integer recordState;

    /**
     * 直播状态
     */
    private Integer liveState;

    /**
     * 公开状态
     */
    private Integer openState;

    /**
     * 课堂唯一标识
     */
    private String uniqueIdentity;

    /**
     * 课堂直播播放器地址
     */
    private String liveUrl;

    /**
     * RTMP协议的拉流地址
     */
    private String rtmpUrl;

    /**
     * HLS协议的拉流地址
     */
    private String hlsUrl;

    /**
     * FLV协议的拉流地址
     */
    private String flvUrl;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
