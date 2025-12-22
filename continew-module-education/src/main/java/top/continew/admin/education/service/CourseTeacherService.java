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

package top.continew.admin.education.service;

import top.continew.admin.education.model.req.CourseTeacherReq;
import top.continew.admin.education.model.resp.CourseTeacherResp;

import java.util.List;
import java.util.Map;

/**
 * 班级老师关联业务接口
 *
 * @author don
 * @since 2025/11/01 00:00
 */
public interface CourseTeacherService {

    /**
     * 批量添加老师到班级
     *
     * @param req 班级老师关联请求参数
     */
    void addTeachersToCourse(CourseTeacherReq req);

    /**
     * 从班级移除老师
     *
     * @param courseId  班级ID
     * @param teacherId 老师ID
     */
    void removeTeacherFromCourse(Long courseId, Long teacherId);

    /**
     * 获取班级的老师列表
     *
     * @param courseId 班级ID
     * @return 老师列表
     */
    List<CourseTeacherResp> listTeachersByCourseId(Long courseId);

    /**
     * 删除班级的所有老师关联
     *
     * @param courseId 班级ID
     */
    void deleteAllByCourseId(Long courseId);

    /**
     * 统计班级的教师数量
     *
     * @param courseId 班级ID
     * @return 教师数量
     */
    int countTeachersByCourseId(Long courseId);

    /**
     * 批量统计多个班级的教师数量
     *
     * @param courseIds 班级ID列表
     * @return 班级ID -> 教师数量的映射
     */
    Map<Long, Integer> countTeachersByCourseIds(List<Long> courseIds);
}
