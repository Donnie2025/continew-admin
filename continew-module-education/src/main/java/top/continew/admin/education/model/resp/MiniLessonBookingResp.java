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

/**
 * 小程序课节预约信息
 *
 * @author don
 * @since 2025/04/18
 */
@Data
@Schema(description = "小程序课节预约信息")
public class MiniLessonBookingResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 课节ID
     */
    @Schema(description = "课节ID")
    private Long id;

    /**
     * 日期（YYYY-MM-DD）
     */
    @Schema(description = "日期（YYYY-MM-DD）", example = "2025-01-28")
    private String date;

    /**
     * 开始时间（HH:MM）
     */
    @Schema(description = "开始时间（HH:MM）", example = "09:00")
    private String startTime;

    /**
     * 结束时间（HH:MM）
     */
    @Schema(description = "结束时间（HH:MM）", example = "09:30")
    private String endTime;

    /**
     * 课节名称
     */
    @Schema(description = "课节名称", example = "优言绘本共读(Elephant and Piggie) - 17")
    private String name;

    /**
     * 关联教材名称
     */
    @Schema(description = "关联教材名称")
    private String materialName;

    /**
     * 教材预览链接
     */
    @Schema(description = "教材预览链接")
    private String lessonUrl;

    /**
     * 所属课本名称（BOOK层级）
     */
    @Schema(description = "所属课本名称")
    private String textbookName;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 状态（upcoming/completed/cancelled）
     */
    @Schema(description = "状态", example = "upcoming", allowableValues = {"upcoming", "completed", "cancelled"})
    private String status;
}
