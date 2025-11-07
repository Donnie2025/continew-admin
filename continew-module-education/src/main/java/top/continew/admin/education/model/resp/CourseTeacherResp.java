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
import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;

/**
 * 班级老师关联信息
 *
 * @author don
 * @since 2025/11/01 00:00
 */
@Data
@Schema(description = "班级老师关联信息")
public class CourseTeacherResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教室ID
     */
    @Schema(description = "教室ID")
    private Long courseId;

    /**
     * 教室名称
     */
    @Schema(description = "教室名称")
    private String courseName;

    /**
     * 老师ID
     */
    @Schema(description = "老师ID")
    private Long teacherId;

    /**
     * 老师名称
     */
    @Schema(description = "老师名称")
    private String teacherName;

    /**
     * Vendor老师ID
     */
    @Schema(description = "Vendor老师ID")
    private String teacherUid;

    /**
     * 老师手机号
     */
    @Schema(description = "老师手机号")
    private String teacherPhone;

    /**
     * 老师邮箱
     */
    @Schema(description = "老师邮箱")
    private String teacherEmail;

    /**
     * 状态（1：启用；2：禁用）
     */
    @Schema(description = "状态（1：启用；2：禁用）")
    private Integer status;
}
