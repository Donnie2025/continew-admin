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

package top.continew.admin.education.model.resp.classin;

import lombok.Data;

/**
 * ClassIn 创建课堂活动响应对象
 *
 * @author don
 * @since 2025/06/21
 */
@Data
public class ClassinCreateClassResp {

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 课堂ID
     */
    private Long classId;

    /**
     * 课堂名称
     */
    private String name;

    /**
     * 课堂直播播放器地址
     */
    private String liveUrl;

    /**
     * 拉流地址信息
     */
    private LiveInfo liveInfo;

    /**
     * 直播拉流地址信息
     */
    @Data
    public static class LiveInfo {
        /**
         * RTMP协议的拉流地址
         */
        private String RTMP;

        /**
         * HLS协议的拉流地址
         */
        private String HLS;

        /**
         * FLV协议的拉流地址
         */
        private String FLV;
    }
}