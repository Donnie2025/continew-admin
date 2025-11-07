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
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 班级老师关联请求参数
 *
 * @author don
 * @since 2025/11/01 00:00
 */
@Data
@Schema(description = "班级老师关联请求参数")
public class CourseTeacherReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教室ID
     */
    @Schema(description = "教室ID")
    @NotNull(message = "教室ID不能为空")
    private Long courseId;

    /**
     * 老师ID列表
     */
    @Schema(description = "老师ID列表")
    @NotNull(message = "老师ID列表不能为空")
    private List<Long> teacherIds;
}
