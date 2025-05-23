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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 批量课程时间请求
 *
 * @author don
 * @since 2025/05/09 13:30
 */
@Data
@Schema(description = "批量课程时间请求")
public class BatchSlotReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "老师ID")
    private Long teacherId;

    @Schema(description = "老师姓名")
    private String teacherName;

    @Schema(description = "是否在线课程")
    private Boolean online;

    @Schema(description = "在线工具类型")
    private String tool;

    @Schema(description = "会议ID")
    private String meetingId;

    @Schema(description = "会议URL")
    private String meetingUrl;

    @Schema(description = "课程时长（单位为分钟）")
    private Integer duration;

    @Schema(description = "学生数量")
    private Integer studentCount;

    @Schema(description = "所属机构ID")
    private Long institutionId;

    @Schema(description = "日期列表，格式：yyyy-MM-dd")
    private List<String> dates;

    @Schema(description = "时间段列表，格式：HH:mm")
    private List<String> times;
}