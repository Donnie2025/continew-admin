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

package top.continew.admin.education.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.education.client.ClassinClient;
import top.continew.admin.education.helper.ClassinHelper;
import top.continew.admin.education.mapper.CourseMapper;
import top.continew.admin.education.mapper.CourseTeacherMapper;
import top.continew.admin.education.mapper.TeacherMapper;
import top.continew.admin.education.model.entity.ClassinUserDO;
import top.continew.admin.education.model.entity.CourseDO;
import top.continew.admin.education.model.entity.CourseTeacherDO;
import top.continew.admin.education.model.entity.TeacherDO;
import top.continew.admin.education.model.req.CourseTeacherReq;
import top.continew.admin.education.model.resp.CourseTeacherResp;
import top.continew.admin.education.service.CourseTeacherService;
import top.continew.starter.core.validation.CheckUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 班级老师关联业务实现
 *
 * @author don
 * @since 2025/11/01 00:00
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseTeacherServiceImpl implements CourseTeacherService {

    private final CourseTeacherMapper courseTeacherMapper;
    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;
    private final ClassinHelper classinHelper;
    private final ClassinClient classinClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTeachersToCourse(CourseTeacherReq req) {
        Long courseId = req.getCourseId();

        // 1. 验证班级是否存在
        CourseDO course = courseMapper.selectById(courseId);
        CheckUtils.throwIfNull(course, "班级不存在");

        // 2. 批量添加老师
        List<CourseTeacherDO> courseTeachers = new ArrayList<>();
        for (Long teacherId : req.getTeacherIds()) {
            // 2.1 检查是否已经关联
            LambdaQueryWrapper<CourseTeacherDO> wrapper = Wrappers.lambdaQuery(CourseTeacherDO.class)
                .eq(CourseTeacherDO::getCourseId, courseId)
                .eq(CourseTeacherDO::getTeacherId, teacherId);
            CourseTeacherDO existing = courseTeacherMapper.selectOne(wrapper);
            if (existing != null) {
                log.warn("老师ID {} 已经关联到班级ID {}", teacherId, courseId);
                continue;
            }

            // 2.2 查询老师信息
            TeacherDO teacher = teacherMapper.selectById(teacherId);
            CheckUtils.throwIfNull(teacher, "老师不存在：{}", teacherId);

            // 2.3 查询或自动注册老师的 ClassIn 账号（使用课程所属机构）
            ClassinUserDO classinUser = null;
            if (course.getInstitutionId() != null) {
                classinUser = classinHelper.registerTeacherIfAbsent(teacherId, teacher, course.getInstitutionId());
            }
            String teacherUid = classinUser != null ? classinUser.getClassinUid() : null;

            // 2.4 如果有 ClassIn courseUid，将老师添加到 ClassIn 课程中
            if (course.getCourseUid() != null && teacherUid != null && course.getInstitutionId() != null) {
                try {
                    addTeacherToClassinCourse(course.getCourseUid(), teacherUid, teacher.getName(), course
                        .getInstitutionId());
                    log.info("成功将老师[{}]添加到ClassIn课程[{}]", teacher.getName(), course.getName());
                } catch (Exception e) {
                    log.error("将老师[{}]添加到ClassIn课程失败: {}", teacher.getName(), e.getMessage(), e);
                    // 不抛出异常，允许本地关联继续
                }
            }

            // 2.5 创建关联关系
            CourseTeacherDO courseTeacher = new CourseTeacherDO();
            courseTeacher.setCourseId(courseId);
            courseTeacher.setCourseName(course.getName());
            courseTeacher.setTeacherId(teacherId);
            courseTeacher.setTeacherName(teacher.getName());
            courseTeacher.setTeacherUid(teacherUid);
            courseTeacher.setStatus(1);

            courseTeachers.add(courseTeacher);
        }

        // 3. 批量插入
        if (!courseTeachers.isEmpty()) {
            for (CourseTeacherDO courseTeacher : courseTeachers) {
                courseTeacherMapper.insert(courseTeacher);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTeacherFromCourse(Long courseId, Long teacherId) {
        // 1. 查询关联关系，获取教师信息
        LambdaQueryWrapper<CourseTeacherDO> wrapper = Wrappers.lambdaQuery(CourseTeacherDO.class)
            .eq(CourseTeacherDO::getCourseId, courseId)
            .eq(CourseTeacherDO::getTeacherId, teacherId);
        CourseTeacherDO courseTeacher = courseTeacherMapper.selectOne(wrapper);

        if (courseTeacher == null) {
            log.warn("班级[{}]和老师[{}]的关联关系不存在", courseId, teacherId);
            return;
        }

        // 2. 查询课程信息，获取 courseUid
        CourseDO course = courseMapper.selectById(courseId);

        // 3. 如果有 ClassIn courseUid 和 teacherUid，调用 ClassIn 接口移除教师
        if (course != null && course.getCourseUid() != null && courseTeacher.getTeacherUid() != null && course
            .getInstitutionId() != null) {
            try {
                classinClient.removeCourseTeacher(course.getCourseUid(), courseTeacher.getTeacherUid(), course
                    .getInstitutionId());
                log.info("成功调用ClassIn API移除教师[{}]从课程[{}]", courseTeacher.getTeacherName(), course.getName());
            } catch (Exception e) {
                log.error("调用ClassIn API移除教师[{}]从课程[{}]失败: {}", courseTeacher.getTeacherName(), course.getName(), e
                    .getMessage(), e);
                // 不抛出异常，允许本地删除继续
            }
        }

        // 4. 删除本地关联关系
        courseTeacherMapper.delete(wrapper);
    }

    @Override
    public List<CourseTeacherResp> listTeachersByCourseId(Long courseId) {
        // 1. 查询关联关系列表
        LambdaQueryWrapper<CourseTeacherDO> wrapper = Wrappers.lambdaQuery(CourseTeacherDO.class)
            .eq(CourseTeacherDO::getCourseId, courseId)
            .orderByDesc(CourseTeacherDO::getCreateTime);
        List<CourseTeacherDO> list = courseTeacherMapper.selectList(wrapper);

        if (list.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 批量查询所有老师详情（只查一次数据库）
        List<Long> teacherIds = list.stream()
            .map(CourseTeacherDO::getTeacherId)
            .distinct()
            .collect(Collectors.toList());
        LambdaQueryWrapper<TeacherDO> teacherWrapper = Wrappers.lambdaQuery(TeacherDO.class)
            .in(TeacherDO::getId, teacherIds);
        List<TeacherDO> teachers = teacherMapper.selectList(teacherWrapper);
        Map<Long, TeacherDO> teacherMap = teachers.stream().collect(Collectors.toMap(TeacherDO::getId, t -> t));

        // 3. 组装响应数据
        List<CourseTeacherResp> respList = new ArrayList<>();
        for (CourseTeacherDO courseTeacher : list) {
            CourseTeacherResp resp = BeanUtil.copyProperties(courseTeacher, CourseTeacherResp.class);

            // 从Map中获取老师信息，填充手机号和邮箱
            TeacherDO teacher = teacherMap.get(courseTeacher.getTeacherId());
            if (teacher != null) {
                resp.setTeacherPhone(teacher.getPhone());
                resp.setTeacherEmail(teacher.getEmail());
            }

            respList.add(resp);
        }
        return respList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllByCourseId(Long courseId) {
        LambdaQueryWrapper<CourseTeacherDO> wrapper = Wrappers.lambdaQuery(CourseTeacherDO.class)
            .eq(CourseTeacherDO::getCourseId, courseId);
        courseTeacherMapper.delete(wrapper);
    }

    @Override
    public int countTeachersByCourseId(Long courseId) {
        LambdaQueryWrapper<CourseTeacherDO> wrapper = Wrappers.lambdaQuery(CourseTeacherDO.class)
            .eq(CourseTeacherDO::getCourseId, courseId);
        return Math.toIntExact(courseTeacherMapper.selectCount(wrapper));
    }

    @Override
    public Map<Long, Integer> countTeachersByCourseIds(List<Long> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 查询所有相关的课程教师关联记录
        LambdaQueryWrapper<CourseTeacherDO> wrapper = Wrappers.lambdaQuery(CourseTeacherDO.class)
            .in(CourseTeacherDO::getCourseId, courseIds)
            .select(CourseTeacherDO::getCourseId); // 只查询courseId字段，提升性能

        List<CourseTeacherDO> courseTeachers = courseTeacherMapper.selectList(wrapper);

        // 按courseId分组统计数量
        Map<Long, Integer> countMap = courseTeachers.stream()
            .collect(Collectors.groupingBy(CourseTeacherDO::getCourseId, Collectors.collectingAndThen(Collectors
                .counting(), Math::toIntExact)));

        // 确保所有courseId都有对应的统计结果，没有关联教师的课程返回0
        for (Long courseId : courseIds) {
            countMap.putIfAbsent(courseId, 0);
        }

        return countMap;
    }

    /**
     * 将教师添加到ClassIn课程中
     *
     * @param courseUid     ClassIn课程ID
     * @param teacherUid    ClassIn教师UID
     * @param teacherName   教师姓名
     * @param institutionId 机构ID
     */
    private void addTeacherToClassinCourse(Long courseUid, String teacherUid, String teacherName, Long institutionId) {
        try {
            // 使用API v2接口添加课程教师
            classinClient.addCourseTeacher(courseUid, Collections.singletonList(teacherUid), institutionId);
            log.info("成功调用ClassIn API添加教师[{}]到课程[{}]", teacherName, courseUid);
        } catch (Exception e) {
            log.error("调用ClassIn API添加教师[{}]到课程[{}]失败: {}", teacherName, courseUid, e.getMessage());
            throw e;
        }
    }
}
