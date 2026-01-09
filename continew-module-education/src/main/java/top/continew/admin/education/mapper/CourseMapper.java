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

package top.continew.admin.education.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.education.model.entity.CourseDO;

/**
 * 班级 Mapper
 *
 * @author don
 * @since 2025/06/21 23:25
 */
public interface CourseMapper extends BaseMapper<CourseDO> {

    /**
     * 查找学生和老师的共同课程
     * 使用INNER JOIN一次性查询，性能更优
     *
     * @param teacherId 老师ID
     * @param studentId 学生ID
     * @return 共同课程ID列表
     */
    @Select("SELECT DISTINCT ct.course_id " + "FROM edu_course_teacher ct " + "INNER JOIN edu_course_student cs ON ct.course_id = cs.course_id " + "WHERE ct.teacher_id = #{teacherId} " + "AND cs.student_id = #{studentId} " + "AND ct.status = 1 " + "AND cs.status = 1 " + "ORDER BY ct.course_id DESC " + "LIMIT 1")
    Long findCommonCourse(@Param("teacherId") Long teacherId, @Param("studentId") Long studentId);
}