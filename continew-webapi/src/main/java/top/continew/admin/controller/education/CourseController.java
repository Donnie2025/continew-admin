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

package top.continew.admin.controller.education;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.education.model.query.CourseQuery;
import top.continew.admin.education.model.req.CourseReq;
import top.continew.admin.education.model.req.CourseTeacherReq;
import top.continew.admin.education.model.req.CourseStudentReq;
import top.continew.admin.education.model.resp.CourseDetailResp;
import top.continew.admin.education.model.resp.CourseResp;
import top.continew.admin.education.model.resp.CourseTeacherResp;
import top.continew.admin.education.model.resp.CourseStudentResp;
import top.continew.admin.education.model.resp.LessonResp;
import top.continew.admin.education.service.CourseService;
import top.continew.admin.education.service.CourseTeacherService;
import top.continew.admin.education.service.CourseStudentService;
import top.continew.admin.education.service.LessonService;

import java.util.List;

/**
 * 班级管理 API
 *
 * @author don
 * @since 2025/06/21 23:25
 */
@Tag(name = "班级管理 API")
@Validated
@RestController
@RequiredArgsConstructor
@CrudRequestMapping(value = "/education/course", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class CourseController extends BaseController<CourseService, CourseResp, CourseDetailResp, CourseQuery, CourseReq> {

    private final CourseTeacherService courseTeacherService;
    private final CourseStudentService courseStudentService;
    private final LessonService lessonService;

    /**
     * 为班级添加老师
     *
     * @param req 班级老师关联请求参数
     */
    @Operation(summary = "为班级添加老师", description = "为班级添加老师")
    @PostMapping("/teacher")
    public void addTeachersToCourse(@Valid @RequestBody CourseTeacherReq req) {
        courseTeacherService.addTeachersToCourse(req);
    }

    /**
     * 从班级移除老师
     *
     * @param courseId  班级ID
     * @param teacherId 老师ID
     */
    @Operation(summary = "从班级移除老师", description = "从班级移除老师")
    @DeleteMapping("/teacher/{courseId}/{teacherId}")
    public void removeTeacherFromCourse(@PathVariable Long courseId, @PathVariable Long teacherId) {
        courseTeacherService.removeTeacherFromCourse(courseId, teacherId);
    }

    /**
     * 获取班级的老师列表
     *
     * @param courseId 班级ID
     * @return 老师列表
     */
    @Operation(summary = "获取班级的老师列表", description = "获取班级的老师列表")
    @GetMapping("/teacher/{courseId}")
    public List<CourseTeacherResp> listTeachersByCourseId(@PathVariable Long courseId) {
        return courseTeacherService.listTeachersByCourseId(courseId);
    }

    /**
     * 为班级添加学生
     *
     * @param req 班级学生关联请求参数
     */
    @Operation(summary = "为班级添加学生", description = "为班级添加学生")
    @PostMapping("/student")
    public void addStudentsToCourse(@Valid @RequestBody CourseStudentReq req) {
        courseStudentService.addStudentsToCourse(req);
    }

    /**
     * 从班级移除学生
     *
     * @param courseId  班级ID
     * @param studentId 学生ID
     */
    @Operation(summary = "从班级移除学生", description = "从班级移除学生")
    @DeleteMapping("/student/{courseId}/{studentId}")
    public void removeStudentFromCourse(@PathVariable Long courseId, @PathVariable Long studentId) {
        courseStudentService.removeStudentFromCourse(courseId, studentId);
    }

    /**
     * 获取班级的学生列表
     *
     * @param courseId 班级ID
     * @return 学生列表
     */
    @Operation(summary = "获取班级的学生列表", description = "获取班级的学生列表")
    @GetMapping("/student/{courseId}")
    public List<CourseStudentResp> listStudentsByCourseId(@PathVariable Long courseId) {
        return courseStudentService.listStudentsByCourseId(courseId);
    }

    /**
     * 获取班级的课节列表
     *
     * @param courseId 班级ID
     * @return 课节列表
     */
    @Operation(summary = "获取班级的课节列表", description = "获取班级的课节列表")
    @GetMapping("/lesson/{courseId}")
    public List<LessonResp> listLessonsByCourseId(@PathVariable Long courseId) {
        return lessonService.listByCourseId(courseId);
    }

}