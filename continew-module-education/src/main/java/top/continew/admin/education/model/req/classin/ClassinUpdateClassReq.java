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

package top.continew.admin.education.model.req.classin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassIn 编辑课堂活动请求对象
 *
 * @author don
 * @since 2025/06/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassinUpdateClassReq {

    /**
     * 班级（课程）ID
     * 必填
     */
    private Long courseId;

    /**
     * 课堂活动ID
     * 必填
     */
    private Long activityId;

    /**
     * 单元ID
     * 非必填，修改课堂所属单元
     */
    private Long unitId;

    /**
     * 课堂活动名称
     * 非必填
     */
    private String name;

    /**
     * 主讲教师UID
     * 非必填，更换主讲教师
     */
    private Long teacherUid;

    /**
     * 活动开始时间
     * 非必填，Unix Epoch 时间戳（秒单位）
     */
    private Long startTime;

    /**
     * 活动结束时间
     * 非必填，Unix Epoch 时间戳（秒单位）
     */
    private Long endTime;

    /**
     * 发布标识
     * 非必填，2-发布给全部成员
     * 注意：此接口不支持修改发布状态，API接口不支持创建、修改课堂为草稿
     */
    private Integer publishFlag;

    /**
     * 录制类型
     * 非必填（与recordState、liveState、openState一组完整参数）
     * 0-云端录制，1-本地录制
     */
    private Integer recordType;

    /**
     * 录制状态
     * 非必填（与recordType、liveState、openState一组完整参数）
     * 0-不录制，1-录制
     */
    private Integer recordState;

    /**
     * 直播状态
     * 非必填（与recordType、recordState、openState一组完整参数）
     * 0-不开启网页直播，1-开启网页直播
     */
    private Integer liveState;

    /**
     * 公开状态
     * 非必填（与recordType、recordState、liveState一组完整参数）
     * 0-不公开，1-公开
     */
    private Integer openState;

    /**
     * 是否隐藏坐席区
     * 非必填
     * 0-显示坐席区，1-隐藏坐席区
     */
    private Integer cameraHide;

    /**
     * 录课封面
     * 非必填
     * 图片url或者是图片的base64内容，长度不能超过512000
     */
    private String recordCover;

    /**
     * 直播封面
     * 非必填
     * 图片url或者是图片的base64内容，长度不能超过512000
     */
    private String liveCover;

    /**
     * 直播介绍
     * 非必填
     * 长度不能超过2000
     */
    private String liveIntro;

    /**
     * 联席教师UID列表
     * 非必填
     * 格式：1234,4321,5678
     */
    private String teacherAssistantUids;

    /**
     * 上台人数
     * 非必填
     * 包括主讲教师，范围是[1,4]
     */
    private Integer stageNum;

    /**
     * 可上台人数
     * 非必填
     * 可上台人数，默认为0（不限制）
     */
    private Integer seatNum;

    /**
     * 课堂学科
     * 非必填
     */
    private Integer subject;

    /**
     * 是否是多摄
     * 非必填
     * 0-否，1-是
     */
    private Integer enableTwoCamera;

    /**
     * 云盘文件夹ID
     * 非必填
     * 关联课件所在的ClassIn云盘文件夹ID
     */
    private String cloudFolderId;
}