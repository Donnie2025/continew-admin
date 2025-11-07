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

package top.continew.admin.education.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 班级学生关联实体
 *
 * @author don
 * @since 2025/11/01 00:00
 */
@Data
@TableName("edu_course_student")
public class CourseStudentDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教室ID
     */
    private Long courseId;

    /**
     * 教室名称
     */
    private String courseName;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学生名称
     */
    private String studentName;

    /**
     * Vendor学生ID
     */
    private String studentUid;

    /**
     * 状态（1：启用；2：禁用）
     */
    private Integer status;
}
