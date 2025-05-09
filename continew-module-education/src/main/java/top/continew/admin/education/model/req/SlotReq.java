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

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;
import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 课程管理创建或修改参数
 *
 * @author don
 * @since 2025/04/25 23:24
 */
@Data
@Schema(description = "课程管理创建或修改参数")
public class SlotReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属教师ID
     */
    @Schema(description = "所属教师ID")
    @NotNull(message = "所属教师ID不能为空")
    private Long teacherId;

    /**
     * 教师名字
     */
    @Schema(description = "教师名字")
    @NotBlank(message = "教师名字不能为空")
    @Length(max = 50, message = "教师名字长度不能超过 {max} 个字符")
    private String teacherName;

    /**
     * 开课日期（格式：YYYYMMDD）
     */
    @Schema(description = "开课日期（格式：YYYYMMDD）")
    @NotBlank(message = "开课日期（格式：YYYYMMDD）不能为空")
    @Length(max = 8, message = "开课日期（格式：YYYYMMDD）长度不能超过 {max} 个字符")
    private String startDate;

    /**
     * 开课时间（格式：HH:MM）
     */
    @Schema(description = "开课时间（格式：HH:MM）")
    @NotBlank(message = "开课时间（格式：HH:MM）不能为空")
    @Length(max = 5, message = "开课时间（格式：HH:MM）长度不能超过 {max} 个字符")
    private String startTime;

    /**
     * 课程时长（单位为分钟）
     */
    @Schema(description = "课程时长（单位为分钟）")
    private Integer duration;

    /**
     * 是否在线教室（0：否；1：是）
     */
    @Schema(description = "是否在线教室（0：否；1：是）")
    private Boolean isOnline;

    /**
     * 所属机构ID
     */
    @Schema(description = "所属机构ID")
    private Long institutionId;

    /**
     * 星期几（1：周一；2：周二；3：周三；4：周四；5：周五；6：周六；7：周日）
     */
    @Schema(description = "星期几（1-7，1表示周一，7表示周日）")
    private Integer weekday;
}