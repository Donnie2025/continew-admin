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
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.BasePageResp;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.education.model.query.LessonQuery;
import top.continew.admin.education.model.req.LessonReq;
import top.continew.admin.education.model.resp.LessonDetailResp;
import top.continew.admin.education.model.resp.LessonResp;
import top.continew.admin.education.service.LessonService;
import top.continew.admin.education.model.entity.LessonDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.swagger.v3.oas.annotations.Operation;
import top.continew.admin.education.mapper.LessonMapper;
import top.continew.admin.education.mapper.CourseMapper;
import top.continew.admin.education.mapper.CourseStudentMapper;
import top.continew.admin.education.model.entity.CourseDO;
import lombok.RequiredArgsConstructor;

/**
 * 课堂管理 API
 *
 * @author don
 * @since 2025/06/24 23:39
 */
@Slf4j
@Tag(name = "课堂管理 API")
@RestController
@RequiredArgsConstructor
@CrudRequestMapping(value = "/education/lesson", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class LessonController extends BaseController<LessonService, LessonResp, LessonDetailResp, LessonQuery, LessonReq> {

    private final LessonMapper lessonMapper;
    private final CourseMapper courseMapper;
    private final CourseStudentMapper courseStudentMapper;

    /**
     * 重写分页查询，添加费用计算
     */
    @Override
    public BasePageResp<LessonResp> page(LessonQuery query, PageQuery pageQuery) {
        log.info("课堂管理分页查询开始");

        // 调用父类的分页查询
        BasePageResp<LessonResp> result = super.page(query, pageQuery);

        // 为每个课堂计算预估费用
        if (result != null && result.getList() != null) {
            log.info("开始为{}个课堂计算预估费用", result.getList().size());
            for (LessonResp resp : result.getList()) {
                // 通过mapper查询完整的实体数据
                LessonDO lesson = lessonMapper.selectById(resp.getId());
                if (lesson != null) {
                    Double cost = calculateEstimatedCost(lesson);
                    resp.setEstimatedCost(cost);
                    log.info("课堂[{}]费用计算完成: ¥{}", resp.getId(), cost);
                }
            }
        }

        return result;
    }

    @Operation(summary = "获取班级关联的教材ID")
    @GetMapping("/course-material/{courseId}")
    public Long getCourseMaterialId(@PathVariable Long courseId) {
        CourseDO course = courseMapper.selectById(courseId);
        return course != null ? course.getMaterialId() : null;
    }

    @Operation(summary = "设置课堂关联教材")
    @PutMapping("/{id}/material")
    public void setMaterial(@PathVariable Long id,
                            @RequestParam(required = false) Long materialId,
                            @RequestParam(required = false) String materialName) {
        LambdaUpdateWrapper<LessonDO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(LessonDO::getId, id);
        wrapper.set(LessonDO::getMaterialId, materialId);
        wrapper.set(LessonDO::getMaterialName, materialName);
        lessonMapper.update(null, wrapper);
    }

    /**
     * 计算课堂预估费用
     */
    private Double calculateEstimatedCost(LessonDO lesson) {
        log.info("开始计算课堂费用 - 课堂ID: {}, 名称: {}", lesson.getId(), lesson.getName());

        if (lesson.getDuration() == null || lesson.getDuration() <= 0) {
            log.warn("课堂时长无效 - 课堂ID: {}, 时长: {}", lesson.getId(), lesson.getDuration());
            return 0.0;
        }

        double totalCost = 0.0;
        long durationMinutes = lesson.getDuration();

        // 计算学生人数（通过课程关联的学生数量）
        long studentCountLong = courseStudentMapper
            .selectCount(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<top.continew.admin.education.model.entity.CourseStudentDO>()
                .eq("course_id", lesson.getCourseId()));
        int studentCount = (int)studentCountLong;
        // 如果没有学生，默认为1对1
        if (studentCount <= 0) {
            studentCount = 1;
        }

        log.info("课堂费用计算参数 - 课堂ID: {}, 课程ID: {}, 时长: {}分钟, 关联学生数: {}, 录制状态: {}", lesson.getId(), lesson
            .getCourseId(), durationMinutes, studentCount, lesson.getRecordState());

        // 1. 计算教学费用
        if (studentCount == 1) {
            // 1对1课程：每半小时为一个计费单位，不足半小时按半小时算，每多半小时加一块钱
            double halfHourUnits = Math.ceil(durationMinutes / 30.0);
            double teachingCost = halfHourUnits * 1.0;
            totalCost += teachingCost;
            log.info("1对1课程教学费用 - 课堂ID: {}, 半小时单位数: {}, 费用: {}元", lesson.getId(), halfHourUnits, teachingCost);
        } else {
            // 1对多课程：每半小时为一个计费单位，不足半小时按半小时算，每个学生每半小时2块钱
            double halfHourUnits = Math.ceil(durationMinutes / 30.0);
            double teachingCost = studentCount * 2.0 * halfHourUnits;
            totalCost += teachingCost;
            log.info("1对多课程教学费用 - 课堂ID: {}, 学生数: {}, 半小时单位数: {}, 费用: {}元", lesson
                .getId(), studentCount, halfHourUnits, teachingCost);
        }

        // 2. 计算录课费用
        if (lesson.getRecordState() != null && lesson.getRecordState() == 1) {
            // 录课费用：每半小时为一个计费单位，不足半小时按半小时算，每半小时1块钱
            double halfHourUnits = Math.ceil(durationMinutes / 30.0);
            double recordCost = halfHourUnits * 1.0;
            totalCost += recordCost;
            log.info("录课费用 - 课堂ID: {}, 半小时单位数: {}, 费用: {}元", lesson.getId(), halfHourUnits, recordCost);
        } else {
            log.info("无录课费用 - 课堂ID: {}, 录制状态: {}", lesson.getId(), lesson.getRecordState());
        }

        double finalCost = (double)Math.round(totalCost);
        log.info("课堂费用计算完成 - 课堂ID: {}, 总费用: {}元", lesson.getId(), finalCost);
        return finalCost;
    }
}