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

/**
 * 班级详情信息
 *
 * @author don
 * @since 2025/06/21 23:25
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "班级详情信息")
public class CourseDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教室名称
     */
    @Schema(description = "教室名称")
    @ExcelProperty(value = "教室名称")
    private String name;

    /**
     * 班主任ID
     */
    @Schema(description = "班主任ID")
    @ExcelProperty(value = "班主任ID")
    private Long mainTeacherId;

    /**
     * Classin班主任ID
     */
    @Schema(description = "Classin班主任ID")
    @ExcelProperty(value = "Classin班主任ID")
    private String mainTeacherUid;

    /**
     * 机构课程唯一标识
     */
    @Schema(description = "机构课程唯一标识")
    @ExcelProperty(value = "机构课程唯一标识")
    private String courseUnique;

    /**
     * classin教室ID
     */
    @Schema(description = "classin教室ID")
    @ExcelProperty(value = "classin教室ID")
    private Long courseUid;

    /**
     * 教室设置ID
     */
    @Schema(description = "教室设置ID")
    @ExcelProperty(value = "教室设置ID")
    private Long courseSettingId;

    /**
     * 状态（1：启用；2：禁用）
     */
    @Schema(description = "状态（1：启用；2：禁用）")
    @ExcelProperty(value = "状态（1：启用；2：禁用）")
    private Integer status;

    /**
     * 所属机构ID
     */
    @Schema(description = "所属机构ID")
    @ExcelProperty(value = "所属机构ID")
    private Long institutionId;

    /**
     * 代理机构编码
     */
    @Schema(description = "代理机构编码")
    @ExcelProperty(value = "代理机构编码")
    private String agentCode;

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

    /**
     * 备注
     */
    @Schema(description = "备注")
    @ExcelProperty(value = "备注")
    private String remark;
}