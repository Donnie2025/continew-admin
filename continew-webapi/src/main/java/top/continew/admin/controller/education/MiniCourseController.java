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

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import top.continew.admin.education.model.req.BatchLessonReq;
import top.continew.admin.education.model.req.CourseStudentReq;
import top.continew.admin.education.model.req.CourseTeacherReq;
import top.continew.admin.education.model.req.LessonReq;
import top.continew.admin.education.model.resp.CourseResp;
import top.continew.admin.education.model.resp.CourseStudentResp;
import top.continew.admin.education.model.resp.CourseTeacherResp;
import top.continew.admin.education.model.resp.LessonResp;
import top.continew.admin.education.service.CourseService;
import top.continew.admin.education.service.CourseStudentService;
import top.continew.admin.education.service.CourseTeacherService;
import top.continew.admin.education.service.LessonService;

import java.util.List;

/**
 * 小程序班级管理 API
 *
 * @author don
 * @since 2025/11/16
 */
@Tag(name = "小程序班级管理 API")
@Validated
@RestController
@RequestMapping("/mini/course")
@RequiredArgsConstructor
@SaIgnore
public class MiniCourseController {

    private final CourseService courseService;
    private final CourseTeacherService courseTeacherService;
    private final CourseStudentService courseStudentService;
    private final LessonService lessonService;

    /**
     * 获取班主任的班级列表（支持按班级名搜索）
     *
     * @param teacherIdentifier 班主任ID或手机号
     * @param name              班级名称（可选，用于搜索）
     * @return 班级列表
     */
    @Operation(summary = "获取班主任的班级列表", description = "班主任查看自己负责的班级，支持按班级名搜索")
    @GetMapping("/list/{teacherIdentifier}")
    public List<CourseResp> listCoursesByTeacher(@PathVariable String teacherIdentifier,
                                                 @RequestParam(required = false) String name) {
        return courseService.listByMainTeacher(teacherIdentifier, name);
    }

    /**
     * 获取班级详情
     *
     * @param courseId 班级ID
     * @return 班级详情
     */
    @Operation(summary = "获取班级详情", description = "获取班级的详细信息")
    @GetMapping("/{courseId}")
    public CourseResp getCourseDetail(@PathVariable Long courseId) {
        return courseService.getById(courseId);
    }

    /**
     * 为班级关联老师
     *
     * @param req 班级老师关联请求参数
     */
    @Operation(summary = "为班级关联老师", description = "班主任为班级关联上课老师")
    @PostMapping("/teacher")
    public void addTeachersToCourse(@Valid @RequestBody CourseTeacherReq req) {
        courseTeacherService.addTeachersToCourse(req);
    }

    /**
     * 取消班级老师关联
     *
     * @param courseId  班级ID
     * @param teacherId 老师ID
     */
    @Operation(summary = "取消班级老师关联", description = "班主任取消班级的上课老师关联")
    @DeleteMapping("/teacher/{courseId}/{teacherId}")
    public void removeTeacherFromCourse(@PathVariable Long courseId, @PathVariable Long teacherId) {
        courseTeacherService.removeTeacherFromCourse(courseId, teacherId);
    }

    /**
     * 获取班级关联的老师列表
     *
     * @param courseId 班级ID
     * @return 老师列表
     */
    @Operation(summary = "获取班级关联的老师列表", description = "获取班级所有关联的上课老师")
    @GetMapping("/teacher/{courseId}")
    public List<CourseTeacherResp> listTeachersByCourseId(@PathVariable Long courseId) {
        return courseTeacherService.listTeachersByCourseId(courseId);
    }

    /**
     * 为班级关联学生
     *
     * @param req 班级学生关联请求参数
     */
    @Operation(summary = "为班级关联学生", description = "班主任为班级关联上课学生")
    @PostMapping("/student")
    public void addStudentsToCourse(@Valid @RequestBody CourseStudentReq req) {
        courseStudentService.addStudentsToCourse(req);
    }

    /**
     * 取消班级学生关联
     *
     * @param courseId  班级ID
     * @param studentId 学生ID
     */
    @Operation(summary = "取消班级学生关联", description = "班主任取消班级的上课学生关联")
    @DeleteMapping("/student/{courseId}/{studentId}")
    public void removeStudentFromCourse(@PathVariable Long courseId, @PathVariable Long studentId) {
        courseStudentService.removeStudentFromCourse(courseId, studentId);
    }

    /**
     * 获取班级关联的学生列表
     *
     * @param courseId 班级ID
     * @return 学生列表
     */
    @Operation(summary = "获取班级关联的学生列表", description = "获取班级所有关联的上课学生")
    @GetMapping("/student/{courseId}")
    public List<CourseStudentResp> listStudentsByCourseId(@PathVariable Long courseId) {
        return courseStudentService.listStudentsByCourseId(courseId);
    }

    /**
     * 新增课节
     *
     * @param req 课节请求参数
     * @return 课节ID
     */
    @Operation(summary = "新增课节", description = "班主任为班级新增课节")
    @PostMapping("/lesson")
    public Long addLesson(@Valid @RequestBody LessonReq req) {
        return lessonService.create(req);
    }

    /**
     * 批量新增课节
     *
     * @param req 批量课节请求参数
     */
    @Operation(summary = "批量新增课节", description = "班主任为班级批量新增课节")
    @PostMapping("/lesson/batch")
    public void addLessonsBatch(@Valid @RequestBody BatchLessonReq req) {
        lessonService.createBatch(req);
    }

    /**
     * 获取班级的课节列表
     *
     * @param courseId 班级ID
     * @return 课节列表
     */
    @Operation(summary = "获取班级的课节列表", description = "获取班级所有课节")
    @GetMapping("/lesson/{courseId}")
    public List<LessonResp> listLessonsByCourseId(@PathVariable Long courseId) {
        return lessonService.listByCourseId(courseId);
    }
}
