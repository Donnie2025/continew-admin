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
import top.continew.admin.education.mapper.CourseStudentMapper;
import top.continew.admin.education.mapper.StudentMapper;
import top.continew.admin.education.model.entity.ClassinUserDO;
import top.continew.admin.education.model.entity.CourseDO;
import top.continew.admin.education.model.entity.CourseStudentDO;
import top.continew.admin.education.model.entity.StudentDO;
import top.continew.admin.education.model.req.CourseStudentReq;
import top.continew.admin.education.model.resp.CourseStudentResp;
import top.continew.admin.education.service.ClassinUserService;
import top.continew.admin.education.service.CourseStudentService;
import top.continew.starter.core.validation.CheckUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 班级学生关联业务实现
 *
 * @author don
 * @since 2025/11/01 00:00
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseStudentServiceImpl implements CourseStudentService {

    private final CourseStudentMapper courseStudentMapper;
    private final CourseMapper courseMapper;
    private final StudentMapper studentMapper;
    private final ClassinUserService classinUserService;
    private final ClassinClient classinClient;
    private final ClassinHelper classinHelper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addStudentsToCourse(CourseStudentReq req) {
        Long courseId = req.getCourseId();

        // 1. 验证班级是否存在
        CourseDO course = courseMapper.selectById(courseId);
        CheckUtils.throwIfNull(course, "班级不存在");

        // 2. 批量添加学生
        List<CourseStudentDO> courseStudents = new ArrayList<>();
        List<String> studentUidsToAdd = new ArrayList<>();

        for (Long studentId : req.getStudentIds()) {
            // 2.1 检查是否已经关联
            LambdaQueryWrapper<CourseStudentDO> wrapper = Wrappers.lambdaQuery(CourseStudentDO.class)
                .eq(CourseStudentDO::getCourseId, courseId)
                .eq(CourseStudentDO::getStudentId, studentId);
            CourseStudentDO existing = courseStudentMapper.selectOne(wrapper);
            if (existing != null) {
                log.warn("学生ID {} 已经关联到班级ID {}", studentId, courseId);
                continue;
            }

            // 2.2 查询学生信息
            StudentDO student = studentMapper.selectById(studentId);
            CheckUtils.throwIfNull(student, "学生不存在：{}", studentId);

            // 2.3 确保学生在当前机构下有ClassIn账号（如果没有则自动注册）
            String studentUid = null;
            if (course.getInstitutionId() != null) {
                ClassinUserDO classinUser = classinHelper.registerStudentIfAbsent(studentId, student, course
                    .getInstitutionId());
                studentUid = classinUser != null ? classinUser.getClassinUid() : null;
            }

            // 2.4 创建关联关系
            CourseStudentDO courseStudent = new CourseStudentDO();
            courseStudent.setCourseId(courseId);
            courseStudent.setCourseName(course.getName());
            courseStudent.setStudentId(studentId);
            courseStudent.setStudentName(student.getName());
            courseStudent.setStudentUid(studentUid);
            courseStudent.setStatus(1);

            courseStudents.add(courseStudent);

            // 收集需要添加到ClassIn的学生UID
            if (studentUid != null) {
                studentUidsToAdd.add(studentUid);
            }
        }

        // 3. 如果有ClassIn courseUid且有学生需要添加，批量调用ClassIn接口
        if (course.getCourseUid() != null && !studentUidsToAdd.isEmpty() && course.getInstitutionId() != null) {
            try {
                classinClient.addCourseStudentMultiple(course.getCourseUid(), studentUidsToAdd, course
                    .getInstitutionId(), 1); // 1表示学生身份
                log.info("成功批量添加{}个学生到ClassIn课程[{}]", studentUidsToAdd.size(), course.getName());
            } catch (Exception e) {
                log.error("批量添加学生到ClassIn课程失败: {}", e.getMessage(), e);
                // 不抛出异常，允许本地关联继续
            }
        }

        // 4. 批量插入本地关联关系
        if (!courseStudents.isEmpty()) {
            for (CourseStudentDO courseStudent : courseStudents) {
                courseStudentMapper.insert(courseStudent);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeStudentFromCourse(Long courseId, Long studentId) {
        // 1. 查询要删除的关联关系（包含ClassIn信息）
        LambdaQueryWrapper<CourseStudentDO> wrapper = Wrappers.lambdaQuery(CourseStudentDO.class)
            .eq(CourseStudentDO::getCourseId, courseId)
            .eq(CourseStudentDO::getStudentId, studentId);
        CourseStudentDO courseStudent = courseStudentMapper.selectOne(wrapper);

        if (courseStudent == null) {
            log.warn("要移除的学生关联不存在: courseId={}, studentId={}", courseId, studentId);
            return;
        }

        // 2. 查询课程信息（获取ClassIn courseUid和机构ID）
        CourseDO course = courseMapper.selectById(courseId);
        if (course == null) {
            log.warn("课程不存在: courseId={}", courseId);
            return;
        }

        // 3. 从ClassIn移除学生（如果课程关联了ClassIn且学生有ClassIn账号）
        if (course.getCourseUid() != null && courseStudent.getStudentUid() != null && course
            .getInstitutionId() != null) {
            try {
                classinClient.removeCourseStudent(course.getCourseUid(), courseStudent.getStudentUid(), course
                    .getInstitutionId());
                log.info("成功从ClassIn课程移除学生: courseName={}, studentName={}, studentUid={}", course
                    .getName(), courseStudent.getStudentName(), courseStudent.getStudentUid());
            } catch (Exception e) {
                log.error("从ClassIn课程移除学生失败: courseName={}, studentName={}, error={}", course.getName(), courseStudent
                    .getStudentName(), e.getMessage(), e);
                // 不抛出异常，继续删除本地关联
            }
        } else {
            log.info("课程未关联ClassIn或学生无ClassIn账号，跳过ClassIn同步: courseName={}, studentName={}", course
                .getName(), courseStudent.getStudentName());
        }

        // 4. 删除本地关联关系
        courseStudentMapper.delete(wrapper);
        log.info("成功移除学生关联: courseName={}, studentName={}", course.getName(), courseStudent.getStudentName());
    }

    @Override
    public List<CourseStudentResp> listStudentsByCourseId(Long courseId) {
        // 1. 查询关联关系列表
        LambdaQueryWrapper<CourseStudentDO> wrapper = Wrappers.lambdaQuery(CourseStudentDO.class)
            .eq(CourseStudentDO::getCourseId, courseId)
            .orderByDesc(CourseStudentDO::getCreateTime);
        List<CourseStudentDO> list = courseStudentMapper.selectList(wrapper);

        if (list.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 批量查询所有学生详情（只查一次数据库）
        List<Long> studentIds = list.stream()
            .map(CourseStudentDO::getStudentId)
            .distinct()
            .collect(Collectors.toList());
        LambdaQueryWrapper<StudentDO> studentWrapper = Wrappers.lambdaQuery(StudentDO.class)
            .in(StudentDO::getId, studentIds);
        List<StudentDO> students = studentMapper.selectList(studentWrapper);
        Map<Long, StudentDO> studentMap = students.stream().collect(Collectors.toMap(StudentDO::getId, s -> s));

        // 3. 组装响应数据
        List<CourseStudentResp> respList = new ArrayList<>();
        for (CourseStudentDO courseStudent : list) {
            CourseStudentResp resp = BeanUtil.copyProperties(courseStudent, CourseStudentResp.class);

            // 从Map中获取学生信息，填充名称、手机号和邮箱
            StudentDO student = studentMap.get(courseStudent.getStudentId());
            if (student != null) {
                resp.setStudentName(student.getName());
                resp.setStudentPhone(student.getPhone());
                resp.setStudentEmail(student.getEmail());
            }

            respList.add(resp);
        }
        return respList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllByCourseId(Long courseId) {
        LambdaQueryWrapper<CourseStudentDO> wrapper = Wrappers.lambdaQuery(CourseStudentDO.class)
            .eq(CourseStudentDO::getCourseId, courseId);
        courseStudentMapper.delete(wrapper);
    }

    @Override
    public Map<Long, List<CourseStudentResp>> listStudentsByCourseIds(List<Long> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            return Collections.emptyMap();
        }

        LambdaQueryWrapper<CourseStudentDO> wrapper = Wrappers.lambdaQuery(CourseStudentDO.class)
            .in(CourseStudentDO::getCourseId, courseIds)
            .orderByDesc(CourseStudentDO::getCreateTime);
        List<CourseStudentDO> list = courseStudentMapper.selectList(wrapper);

        if (list.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> studentIds = list.stream()
            .map(CourseStudentDO::getStudentId)
            .distinct()
            .collect(Collectors.toList());
        LambdaQueryWrapper<StudentDO> studentWrapper = Wrappers.lambdaQuery(StudentDO.class)
            .in(StudentDO::getId, studentIds);
        Map<Long, StudentDO> studentMap = studentMapper.selectList(studentWrapper)
            .stream()
            .collect(Collectors.toMap(StudentDO::getId, s -> s));

        Map<Long, List<CourseStudentResp>> resultMap = new java.util.HashMap<>();
        for (CourseStudentDO cs : list) {
            CourseStudentResp resp = BeanUtil.copyProperties(cs, CourseStudentResp.class);
            StudentDO student = studentMap.get(cs.getStudentId());
            if (student != null) {
                resp.setStudentName(student.getName());
                resp.setStudentPhone(student.getPhone());
                resp.setStudentEmail(student.getEmail());
            }
            resultMap.computeIfAbsent(cs.getCourseId(), k -> new ArrayList<>()).add(resp);
        }
        return resultMap;
    }

    @Override
    public int countStudentsByCourseId(Long courseId) {
        LambdaQueryWrapper<CourseStudentDO> wrapper = Wrappers.lambdaQuery(CourseStudentDO.class)
            .eq(CourseStudentDO::getCourseId, courseId);
        return Math.toIntExact(courseStudentMapper.selectCount(wrapper));
    }

    @Override
    public Map<Long, Integer> countStudentsByCourseIds(List<Long> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 查询所有相关的课程学生关联记录
        LambdaQueryWrapper<CourseStudentDO> wrapper = Wrappers.lambdaQuery(CourseStudentDO.class)
            .in(CourseStudentDO::getCourseId, courseIds)
            .select(CourseStudentDO::getCourseId); // 只查询courseId字段，提升性能

        List<CourseStudentDO> courseStudents = courseStudentMapper.selectList(wrapper);

        // 按courseId分组统计数量
        Map<Long, Integer> countMap = courseStudents.stream()
            .collect(Collectors.groupingBy(CourseStudentDO::getCourseId, Collectors.collectingAndThen(Collectors
                .counting(), Math::toIntExact)));

        // 确保所有courseId都有对应的统计结果，没有关联学生的课程返回0
        for (Long courseId : courseIds) {
            countMap.putIfAbsent(courseId, 0);
        }

        return countMap;
    }
}
