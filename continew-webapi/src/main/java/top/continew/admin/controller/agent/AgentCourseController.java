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

package top.continew.admin.controller.agent;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.BasePageResp;
import top.continew.starter.extension.crud.model.resp.IdResp;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.education.mapper.CourseMapper;
import top.continew.admin.education.model.entity.CourseDO;
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

import cn.hutool.core.util.StrUtil;
import top.continew.admin.common.context.UserContextHolder;

import java.util.Collections;
import java.util.List;

/**
 * 代理机构小班课管理 API
 *
 * @author don
 * @since 2025/01/09
 */
@Tag(name = "代理机构小班课管理 API")
@Validated
@RestController
@RequiredArgsConstructor
@CrudRequestMapping(value = "/agent/course", api = {Api.GET, Api.DELETE, Api.EXPORT})
public class AgentCourseController extends BaseController<CourseService, CourseResp, CourseDetailResp, CourseQuery, CourseReq> {

    private final CourseMapper courseMapper;
    private final CourseTeacherService courseTeacherService;
    private final CourseStudentService courseStudentService;
    private final LessonService lessonService;

    /**
     * 分页查询小班课（只返回youyan代理机构的数据）
     *
     * @param query     查询条件
     * @param pageQuery 分页查询条件
     * @return 分页列表信息
     */
    @Operation(summary = "分页查询小班课列表", description = "分页查询小班课列表")
    @GetMapping("/page")
    public BasePageResp<CourseResp> page(CourseQuery query, PageQuery pageQuery) {
        String agentCode = UserContextHolder.getAgentCode();
        if (StrUtil.isBlank(agentCode)) {
            return new BasePageResp<>(Collections.emptyList(), 0);
        }
        query.setAgentCode(agentCode);
        BasePageResp<CourseResp> result = baseService.page(query, pageQuery);
        result.getList().forEach(course -> {
            if (course.getTeachers() != null) {
                course.getTeachers().forEach(t -> t.setTeacherPhone(null));
            }
            if (course.getStudents() != null) {
                course.getStudents().forEach(s -> s.setStudentPhone(null));
            }
        });
        return result;
    }

    /**
     * 新增小班课（自动设置代理机构为youyan）
     *
     * @param req 请求参数
     * @return ID响应
     */
    @Operation(summary = "新增小班课", description = "新增小班课")
    @PostMapping
    public IdResp<Long> create(@RequestBody CourseReq req) {
        String agentCode = UserContextHolder.getAgentCode();
        // 调用Service创建课程
        Long id = baseService.create(req);

        // 自动设置代理机构编码为当前用户的 agentCode
        CourseDO updateEntity = new CourseDO();
        updateEntity.setId(id);
        updateEntity.setAgentCode(agentCode);
        courseMapper.updateById(updateEntity);

        return new IdResp<>(id);
    }

    /**
     * 修改小班课（保持代理机构为youyan）
     *
     * @param req 请求参数
     * @param id  ID
     */
    @Operation(summary = "修改小班课", description = "修改小班课")
    @PutMapping("/{id}")
    public void update(@RequestBody CourseReq req, @PathVariable Long id) {
        String agentCode = UserContextHolder.getAgentCode();
        // 调用Service更新课程
        baseService.update(req, id);

        // 保持代理机构编码为当前用户的 agentCode
        CourseDO updateEntity = new CourseDO();
        updateEntity.setId(id);
        updateEntity.setAgentCode(agentCode);
        courseMapper.updateById(updateEntity);
    }

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

    /**
     * 设置班级关联教材（仅更新 material_id/material_name，不同步 ClassIn）
     *
     * @param id           班级ID
     * @param materialId   教材ID（传 null 表示清除）
     * @param materialName 教材名称
     */
    @Operation(summary = "设置班级关联教材", description = "仅更新 material_id/material_name 字段，不触发 ClassIn 同步")
    @PutMapping("/{id}/material")
    public void setMaterial(@PathVariable Long id,
                            @RequestParam(required = false) Long materialId,
                            @RequestParam(required = false) String materialName) {
        com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<CourseDO> wrapper = new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<>();
        wrapper.eq(CourseDO::getId, id);
        wrapper.set(CourseDO::getMaterialId, materialId);
        wrapper.set(CourseDO::getMaterialName, materialName);
        courseMapper.update(null, wrapper);
    }
}
