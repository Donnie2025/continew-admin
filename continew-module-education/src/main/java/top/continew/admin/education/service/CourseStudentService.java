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

import top.continew.admin.education.model.req.CourseStudentReq;
import top.continew.admin.education.model.resp.CourseStudentResp;

import java.util.List;

/**
 * 班级学生关联业务接口
 *
 * @author don
 * @since 2025/11/01 00:00
 */
public interface CourseStudentService {

    /**
     * 批量添加学生到班级
     *
     * @param req 班级学生关联请求参数
     */
    void addStudentsToCourse(CourseStudentReq req);

    /**
     * 从班级移除学生
     *
     * @param courseId  班级ID
     * @param studentId 学生ID
     */
    void removeStudentFromCourse(Long courseId, Long studentId);

    /**
     * 获取班级的学生列表
     *
     * @param courseId 班级ID
     * @return 学生列表
     */
    List<CourseStudentResp> listStudentsByCourseId(Long courseId);

    /**
     * 删除班级的所有学生关联
     *
     * @param courseId 班级ID
     */
    void deleteAllByCourseId(Long courseId);
}
