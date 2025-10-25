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
 * 班级创建或修改参数
 *
 * @author don
 * @since 2025/06/21 23:25
 */
@Data
@Schema(description = "班级创建或修改参数")
public class CourseReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教室名称
     */
    @Schema(description = "教室名称")
    @NotBlank(message = "教室名称不能为空")
    @Length(max = 100, message = "教室名称长度不能超过 {max} 个字符")
    private String name;

    /**
     * 班主任手机号
     */
    @Schema(description = "班主任手机号")
    private String mainTeacherPhone;

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
}