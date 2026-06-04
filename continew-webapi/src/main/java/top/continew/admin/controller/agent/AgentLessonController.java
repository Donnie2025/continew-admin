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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.education.model.query.LessonQuery;
import top.continew.admin.education.model.req.LessonReq;
import top.continew.admin.education.model.resp.LessonDetailResp;
import top.continew.admin.education.model.resp.LessonResp;
import top.continew.admin.education.service.LessonService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.BasePageResp;
import top.continew.starter.extension.crud.model.resp.IdResp;

import cn.hutool.core.util.StrUtil;
import top.continew.admin.common.context.UserContextHolder;
import top.continew.admin.education.mapper.CourseMapper;
import top.continew.admin.education.mapper.LessonMapper;
import top.continew.admin.education.model.entity.CourseDO;
import top.continew.admin.education.model.entity.LessonDO;

import java.util.Collections;
import java.util.List;

/**
 * 代理课堂管理 API
 *
 * @author don
 * @since 2025/01/09
 */
@Tag(name = "代理课堂管理 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/agent/lesson")
public class AgentLessonController {

    private final LessonService lessonService;
    private final CourseMapper courseMapper;
    private final LessonMapper lessonMapper;

    /**
     * 分页查询代理课节列表
     *
     * @param query     查询条件
     * @param pageQuery 分页查询条件
     * @return 分页列表信息
     */
    @Operation(summary = "分页查询代理课节列表", description = "分页查询代理课节列表")
    @GetMapping
    public BasePageResp<LessonResp> page(LessonQuery query, PageQuery pageQuery) {
        String agentCode = UserContextHolder.getAgentCode();
        if (StrUtil.isBlank(agentCode)) {
            return new BasePageResp<>(Collections.emptyList(), 0);
        }
        query.setAgentCode(agentCode);
        return lessonService.page(query, pageQuery);
    }

    /**
     * 查询代理课节列表（不分页）
     *
     * @param query 查询条件
     * @return 课节列表
     */
    @Operation(summary = "查询代理课节列表", description = "查询代理课节列表（不分页）")
    @GetMapping("/list")
    public List<LessonResp> list(LessonQuery query) {
        String agentCode = UserContextHolder.getAgentCode();
        if (StrUtil.isBlank(agentCode)) {
            return Collections.emptyList();
        }
        query.setAgentCode(agentCode);
        return lessonService.list(query, null);
    }

    /**
     * 查询代理课节详情
     *
     * @param id 课节ID
     * @return 课节详情
     */
    @Operation(summary = "查询代理课节详情", description = "查询代理课节详情")
    @GetMapping("/{id}")
    public LessonDetailResp get(@PathVariable Long id) {
        return lessonService.get(id);
    }

    /**
     * 新增代理课节
     *
     * @param req 请求参数
     * @return ID响应
     */
    @Operation(summary = "新增代理课节", description = "新增代理课节")
    @PostMapping
    public IdResp<Long> create(@Valid @RequestBody LessonReq req) {
        req.setAgentCode(UserContextHolder.getAgentCode());
        Long id = lessonService.create(req);
        return new IdResp<>(id);
    }

    /**
     * 修改代理课节
     *
     * @param req 请求参数
     * @param id  课节ID
     */
    @Operation(summary = "修改代理课节", description = "修改代理课节")
    @PutMapping("/{id}")
    public void update(@Valid @RequestBody LessonReq req, @PathVariable Long id) {
        lessonService.update(req, id);
    }

    /**
     * 删除代理课节
     *
     * @param ids 课节ID数组
     */
    @Operation(summary = "删除代理课节", description = "删除代理课节")
    @DeleteMapping
    public void delete(@RequestBody List<Long> ids) {
        lessonService.delete(ids);
    }

    /**
     * 获取班级关联的教材ID
     *
     * @param courseId 班级ID
     * @return 教材ID（可能为 null）
     */
    @Operation(summary = "获取班级关联的教材ID")
    @GetMapping("/course-material/{courseId}")
    public Long getCourseMaterialId(@PathVariable Long courseId) {
        CourseDO course = courseMapper.selectById(courseId);
        return course != null ? course.getMaterialId() : null;
    }

    /**
     * 设置课堂关联教材（仅更新 material_id/material_name）
     *
     * @param id           课堂ID
     * @param materialId   教材ID（传 null 表示清除）
     * @param materialName 教材名称
     */
    @Operation(summary = "设置课堂关联教材")
    @PutMapping("/{id}/material")
    public void setMaterial(@PathVariable Long id,
                            @RequestParam(required = false) Long materialId,
                            @RequestParam(required = false) String materialName) {
        com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<LessonDO> wrapper = new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<>();
        wrapper.eq(LessonDO::getId, id);
        wrapper.set(LessonDO::getMaterialId, materialId);
        wrapper.set(LessonDO::getMaterialName, materialName);
        lessonMapper.update(null, wrapper);
    }
}
