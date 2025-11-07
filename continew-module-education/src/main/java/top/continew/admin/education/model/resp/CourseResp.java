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

/**
 * 班级信息
 *
 * @author don
 * @since 2025/06/21 23:25
 */
@Data
@Schema(description = "班级信息")
public class CourseResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教室名称
     */
    @Schema(description = "教室名称")
    private String name;

    /**
     * 班主任ID
     */
    @Schema(description = "班主任ID")
    private Long mainTeacherId;

    /**
     * 班主任姓名
     */
    @Schema(description = "班主任姓名")
    private String mainTeacherName;

    /**
     * Classin班主任ID
     */
    @Schema(description = "Classin班主任ID")
    private String mainTeacherUid;

    /**
     * classin教室ID
     */
    @Schema(description = "classin教室ID")
    private Long courseUid;

    /**
     * 教室设置ID
     */
    @Schema(description = "教室设置ID")
    private Long courseSettingId;

    /**
     * 所属机构ID
     */
    @Schema(description = "所属机构ID")
    private Long institutionId;

    /**
     * 所属机构名称
     */
    @Schema(description = "所属机构名称")
    private String institutionName;

    /**
     * 班级老师列表
     */
    @Schema(description = "班级老师列表")
    private java.util.List<CourseTeacherResp> teachers;

    /**
     * 班级学生列表
     */
    @Schema(description = "班级学生列表")
    private java.util.List<CourseStudentResp> students;
}