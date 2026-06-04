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

package top.continew.admin.education.constant;

/**
 * ClassIn 常量类
 *
 * @author don
 * @since 2024/03/21
 */
public class ClassinConstants {

    /**
     * 用户类型：学生
     */
    public static final String USER_TYPE_STUDENT = "STUDENT";

    /**
     * 用户类型：老师
     */
    public static final String USER_TYPE_TEACHER = "TEACHER";

    // ==================== ClassIn 课堂活动配置常量 ====================

    /**
     * 录制类型：录制教室
     */
    public static final int RECORD_TYPE_CLASSROOM = 0;

    /**
     * 录制类型：录制现场
     */
    public static final int RECORD_TYPE_LIVE_SCENE = 1;

    /**
     * 录制类型：两个都录
     */
    public static final int RECORD_TYPE_BOTH = 2;

    /**
     * 录制状态：开启录制
     */
    public static final int RECORD_STATE_ENABLED = 1;

    /**
     * 录制状态：不录制
     */
    public static final int RECORD_STATE_DISABLED = 0;

    /**
     * 直播状态：开启直播
     */
    public static final int LIVE_STATE_ENABLED = 1;

    /**
     * 直播状态：不开启直播
     */
    public static final int LIVE_STATE_DISABLED = 0;

    /**
     * 公开状态：公开
     */
    public static final int OPEN_STATE_PUBLIC = 1;

    /**
     * 公开状态：不公开
     */
    public static final int OPEN_STATE_PRIVATE = 0;

    /**
     * 摄像头：显示摄像头
     */
    public static final int CAMERA_SHOW = 0;

    /**
     * 摄像头：隐藏摄像头
     */
    public static final int CAMERA_HIDE = 1;

    /**
     * 默认单元ID
     */
    public static final Long DEFAULT_UNIT_ID = 1L;

    // ==================== 视频质量配置 ====================

    /**
     * 视频质量：非高清
     */
    public static final int VIDEO_QUALITY_NORMAL = 0;

    /**
     * 视频质量：高清
     */
    public static final int VIDEO_QUALITY_HD = 1;

    /**
     * 视频质量：全高清
     */
    public static final int VIDEO_QUALITY_FULL_HD = 2;

    // ==================== 学生上台配置 ====================

    /**
     * 学生进入教室：不自动上台
     */
    public static final int AUTO_ONSTAGE_DISABLED = 0;

    /**
     * 学生进入教室：自动上台
     */
    public static final int AUTO_ONSTAGE_ENABLED = 1;

    // ==================== 双摄模式配置 ====================

    /**
     * 双摄模式：不开启
     */
    public static final int DUAL_CAMERA_DISABLED = 0;

    /**
     * 双摄模式：开启全高清副摄像头
     */
    public static final int DUAL_CAMERA_FULL_HD = 3;

    // ==================== 学习报告查看配置 ====================

    /**
     * 互相查看学习报告：不允许
     */
    public static final int ALLOW_CHECK_DISABLED = 0;

    /**
     * 互相查看学习报告：允许
     */
    public static final int ALLOW_CHECK_ENABLED = 1;

    // ==================== OMO站播配置 ====================

    /**
     * OMO站播：关闭
     */
    public static final int OMO_BROADCAST_DISABLED = 0;

    /**
     * OMO站播：开启
     */
    public static final int OMO_BROADCAST_ENABLED = 1;

    // ==================== 默认课堂配置 ====================

    /**
     * 默认座位数（1v6，包含老师）
     */
    public static final int DEFAULT_SEAT_NUM = 7;
}