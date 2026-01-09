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
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.continew.admin.education.client.ClassinClient;
import top.continew.admin.education.helper.ClassinHelper;
import top.continew.admin.education.model.entity.ClassinUserDO;
import top.continew.admin.education.model.entity.InstitutionDO;
import top.continew.admin.education.model.entity.TeacherDO;
import top.continew.admin.education.model.req.classin.ClassinCourseAddReq;
import top.continew.admin.education.service.CourseTeacherService;
import top.continew.admin.education.service.CourseStudentService;
import top.continew.admin.education.service.TeacherService;
import top.continew.starter.core.validation.CheckUtils;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.CourseMapper;
import top.continew.admin.education.mapper.InstitutionMapper;
import top.continew.admin.education.model.entity.CourseDO;
import top.continew.admin.education.model.query.CourseQuery;
import top.continew.admin.education.model.req.CourseReq;
import top.continew.admin.education.model.resp.CourseDetailResp;
import top.continew.admin.education.model.resp.CourseResp;
import top.continew.admin.education.service.CourseService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.admin.education.mapper.TeacherMapper;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 班级业务实现
 *
 * @author don
 * @since 2025/06/21 23:25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends BaseServiceImpl<CourseMapper, CourseDO, CourseResp, CourseDetailResp, CourseQuery, CourseReq> implements CourseService {

    private final TeacherService teacherService;
    private final TeacherMapper teacherMapper;
    private final InstitutionMapper institutionMapper;
    private final ClassinClient classinClient;
    private final ClassinHelper classinHelper;
    private final CourseTeacherService courseTeacherService;
    private final CourseStudentService courseStudentService;

    @Override
    public Long create(CourseReq req) {
        CourseDO entity = BeanUtil.copyProperties(req, CourseDO.class);

        // 1. 处理班主任信息
        String mainTeacherUid = null;
        if (req.getMainTeacherId() != null && req.getInstitutionId() != null) {
            // 1.1 查询教师信息
            TeacherDO teacher = teacherMapper.selectById(req.getMainTeacherId());
            if (teacher != null) {
                // 1.2 查询或自动注册教师的 ClassIn 账号（使用课程所属机构）
                ClassinUserDO classinTeacher = classinHelper.registerTeacherIfAbsent(req
                    .getMainTeacherId(), teacher, req.getInstitutionId());
                if (classinTeacher != null) {
                    mainTeacherUid = classinTeacher.getClassinUid();
                    entity.setMainTeacherUid(mainTeacherUid);
                }
            }
        }

        // 2. 设置课程唯一标识 (用于对接)
        String courseUnique = UUID.randomUUID().toString().replace("-", "");
        entity.setCourseUnique(courseUnique);

        // 3. 调用 ClassIn 接口创建课程
        ClassinCourseAddReq classinReq = new ClassinCourseAddReq();
        classinReq.setCourseName(req.getName());
        classinReq.setCourseUniqueIdentity(courseUnique);
        classinReq.setMainTeacherUid(mainTeacherUid); // 如果为 null，ClassIn 不会设置班主任
        classinReq.setClassroomSettingId(req.getCourseSettingId());
        Long courseUid = classinClient.addCourse(classinReq);
        entity.setCourseUid(courseUid);

        // 4. 保存到本地数据库
        baseMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public void update(CourseReq req, Long id) {
        // 1. 查询原有课程信息
        CourseDO oldCourse = baseMapper.selectById(id);
        CheckUtils.throwIfNull(oldCourse, "课程不存在");

        // 2. 更新课程信息
        CourseDO entity = BeanUtil.copyProperties(req, CourseDO.class);
        entity.setId(id);

        // 3. 处理班主任信息变更
        String mainTeacherUid = oldCourse.getMainTeacherUid();
        Long institutionId = req.getInstitutionId() != null ? req.getInstitutionId() : oldCourse.getInstitutionId();
        if (req.getMainTeacherId() != null && institutionId != null) {
            // 3.1 查询教师信息
            TeacherDO teacher = teacherMapper.selectById(req.getMainTeacherId());
            if (teacher != null) {
                // 3.2 查询或自动注册教师的 ClassIn 账号（使用课程所属机构）
                ClassinUserDO classinTeacher = classinHelper.registerTeacherIfAbsent(req
                    .getMainTeacherId(), teacher, institutionId);
                if (classinTeacher != null) {
                    mainTeacherUid = classinTeacher.getClassinUid();
                    entity.setMainTeacherUid(mainTeacherUid);
                }
            }
        } else {
            // 如果没有传班主任ID，保持原值
            entity.setMainTeacherId(oldCourse.getMainTeacherId());
            entity.setMainTeacherUid(oldCourse.getMainTeacherUid());
        }

        // 4. 保留原有的ClassIn课程ID和唯一标识
        entity.setCourseUid(oldCourse.getCourseUid());
        entity.setCourseUnique(oldCourse.getCourseUnique());

        // 5. 调用 ClassIn 接口更新课程
        if (oldCourse.getCourseUid() != null) {
            try {
                ClassinCourseAddReq classinReq = new ClassinCourseAddReq();
                classinReq.setCourseName(req.getName());
                classinReq.setMainTeacherUid(mainTeacherUid);
                classinReq.setClassroomSettingId(req.getCourseSettingId());
                classinClient.editCourse(classinReq, oldCourse.getCourseUid());
                log.info("同步课程信息到ClassIn成功，课程ID：{}", id);
            } catch (Exception e) {
                log.error("同步课程信息到ClassIn失败，课程ID：{}，错误信息：{}", id, e.getMessage(), e);
                // 继续更新本地数据库，不因ClassIn同步失败而中断
            }
        } else {
            log.warn("课程未关联ClassIn，跳过同步，课程ID：{}", id);
        }

        // 6. 更新本地数据库
        baseMapper.updateById(entity);
    }

    @Override
    public CourseDetailResp get(Long id) {
        // 只获取班级基本信息，不查询关联的老师和学生（提升性能）
        return super.get(id);
    }

    @Override
    public PageResp<CourseResp> page(CourseQuery query, PageQuery pageQuery) {
        // 1. 查询分页数据
        PageResp<CourseResp> page = super.page(query, pageQuery);

        // 2. 填充班主任姓名、机构名称和数量统计
        List<CourseResp> records = page.getList();
        if (!records.isEmpty()) {
            // 2.1 收集所有课程ID
            List<Long> courseIds = records.stream()
                .map(CourseResp::getId)
                .collect(java.util.stream.Collectors.toList());

            // 2.2 批量统计教师和学生数量（优化：2次查询代替N次）
            java.util.Map<Long, Integer> teacherCountMap = courseTeacherService.countTeachersByCourseIds(courseIds);
            java.util.Map<Long, Integer> studentCountMap = courseStudentService.countStudentsByCourseIds(courseIds);

            // 2.3 收集所有班主任ID
            List<Long> mainTeacherIds = records.stream()
                .map(CourseResp::getMainTeacherId)
                .filter(id -> id != null)
                .distinct()
                .collect(java.util.stream.Collectors.toList());

            // 2.4 批量查询所有班主任信息（优化：1次查询代替N次）
            java.util.Map<Long, String> teacherNameMap = new java.util.HashMap<>();
            if (!mainTeacherIds.isEmpty()) {
                com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TeacherDO> wrapper = com.baomidou.mybatisplus.core.toolkit.Wrappers
                    .lambdaQuery(TeacherDO.class)
                    .in(TeacherDO::getId, mainTeacherIds);
                List<TeacherDO> teachers = teacherMapper.selectList(wrapper);
                teacherNameMap = teachers.stream()
                    .collect(java.util.stream.Collectors.toMap(TeacherDO::getId, TeacherDO::getName));
            }

            // 2.5 收集所有机构ID
            List<Long> institutionIds = records.stream()
                .map(CourseResp::getInstitutionId)
                .filter(id -> id != null)
                .distinct()
                .collect(java.util.stream.Collectors.toList());

            // 2.6 批量查询所有机构信息（优化：1次查询代替N次）
            java.util.Map<Long, String> institutionNameMap = new java.util.HashMap<>();
            if (!institutionIds.isEmpty()) {
                com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<InstitutionDO> wrapper = com.baomidou.mybatisplus.core.toolkit.Wrappers
                    .lambdaQuery(InstitutionDO.class)
                    .in(InstitutionDO::getId, institutionIds);
                List<InstitutionDO> institutions = institutionMapper.selectList(wrapper);
                institutionNameMap = institutions.stream()
                    .collect(java.util.stream.Collectors.toMap(InstitutionDO::getId, InstitutionDO::getName));
            }

            // 2.7 填充所有信息
            for (CourseResp record : records) {
                // 填充数量统计
                record.setTeacherCount(teacherCountMap.getOrDefault(record.getId(), 0));
                record.setStudentCount(studentCountMap.getOrDefault(record.getId(), 0));

                // 填充班主任姓名
                if (record.getMainTeacherId() != null) {
                    record.setMainTeacherName(teacherNameMap.get(record.getMainTeacherId()));
                }

                // 填充机构名称
                if (record.getInstitutionId() != null) {
                    record.setInstitutionName(institutionNameMap.get(record.getInstitutionId()));
                }
            }
        }

        return page;
    }

    @Override
    public List<CourseResp> listByMainTeacher(String teacherIdentifier, String name) {
        log.info("查询班主任班级列表，teacherIdentifier: {}, name: {}", teacherIdentifier, name);

        if (StrUtil.isBlank(teacherIdentifier)) {
            log.warn("teacherIdentifier为空，返回空列表");
            return new ArrayList<>();
        }

        Long mainTeacherId;
        try {
            // 直接解析为Long类型的用户ID
            mainTeacherId = Long.parseLong(teacherIdentifier);
            log.info("解析teacherIdentifier为用户ID: {}", mainTeacherId);
        } catch (NumberFormatException e) {
            log.error("teacherIdentifier格式错误，无法解析为数字ID: {}", teacherIdentifier);
            return new ArrayList<>();
        }

        // 构建查询条件
        LambdaQueryWrapper<CourseDO> wrapper = Wrappers.lambdaQuery(CourseDO.class)
            .eq(CourseDO::getMainTeacherId, mainTeacherId)
            .eq(CourseDO::getStatus, 1); // 只查询启用状态的班级

        // 如果提供了班级名称，添加模糊查询条件
        if (StrUtil.isNotBlank(name)) {
            wrapper.like(CourseDO::getName, name);
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(CourseDO::getCreateTime);

        // 查询数据
        List<CourseDO> courses = baseMapper.selectList(wrapper);

        // 转换为响应对象
        List<CourseResp> respList = courses.stream().map(course -> {
            CourseResp resp = BeanUtil.copyProperties(course, CourseResp.class);
            return resp;
        }).collect(Collectors.toList());

        // 批量填充相关信息
        if (!respList.isEmpty()) {
            // 收集所有课程ID
            List<Long> courseIds = respList.stream().map(CourseResp::getId).collect(Collectors.toList());

            // 批量统计教师和学生数量
            java.util.Map<Long, Integer> teacherCountMap = courseTeacherService.countTeachersByCourseIds(courseIds);
            java.util.Map<Long, Integer> studentCountMap = courseStudentService.countStudentsByCourseIds(courseIds);

            // 收集所有班主任ID和机构ID
            List<Long> teacherIds = respList.stream()
                .map(CourseResp::getMainTeacherId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

            List<Long> institutionIds = respList.stream()
                .map(CourseResp::getInstitutionId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

            // 批量查询教师和机构信息
            java.util.Map<Long, String> teacherNameMap = new java.util.HashMap<>();
            if (!teacherIds.isEmpty()) {
                LambdaQueryWrapper<TeacherDO> teacherWrapper = Wrappers.lambdaQuery(TeacherDO.class)
                    .in(TeacherDO::getId, teacherIds);
                List<TeacherDO> teachers = teacherMapper.selectList(teacherWrapper);
                teacherNameMap = teachers.stream().collect(Collectors.toMap(TeacherDO::getId, TeacherDO::getName));
            }

            java.util.Map<Long, String> institutionNameMap = new java.util.HashMap<>();
            if (!institutionIds.isEmpty()) {
                LambdaQueryWrapper<InstitutionDO> institutionWrapper = Wrappers.lambdaQuery(InstitutionDO.class)
                    .in(InstitutionDO::getId, institutionIds);
                List<InstitutionDO> institutions = institutionMapper.selectList(institutionWrapper);
                institutionNameMap = institutions.stream()
                    .collect(Collectors.toMap(InstitutionDO::getId, InstitutionDO::getName));
            }

            // 填充所有信息
            for (CourseResp resp : respList) {
                // 填充数量统计
                resp.setTeacherCount(teacherCountMap.getOrDefault(resp.getId(), 0));
                resp.setStudentCount(studentCountMap.getOrDefault(resp.getId(), 0));

                // 填充班主任姓名
                if (resp.getMainTeacherId() != null) {
                    resp.setMainTeacherName(teacherNameMap.get(resp.getMainTeacherId()));
                }

                // 填充机构名称
                if (resp.getInstitutionId() != null) {
                    resp.setInstitutionName(institutionNameMap.get(resp.getInstitutionId()));
                }
            }
        }

        return respList;
    }

    @Override
    public CourseResp getById(Long id) {
        CourseDO course = baseMapper.selectById(id);
        CheckUtils.throwIfNull(course, "班级不存在");

        CourseResp resp = BeanUtil.copyProperties(course, CourseResp.class);

        // 统计教师和学生数量
        resp.setTeacherCount(courseTeacherService.countTeachersByCourseId(id));
        resp.setStudentCount(courseStudentService.countStudentsByCourseId(id));

        // 填充班主任姓名
        if (course.getMainTeacherId() != null) {
            TeacherDO teacher = teacherMapper.selectById(course.getMainTeacherId());
            if (teacher != null) {
                resp.setMainTeacherName(teacher.getName());
            }
        }

        // 填充机构名称
        if (course.getInstitutionId() != null) {
            InstitutionDO institution = institutionMapper.selectById(course.getInstitutionId());
            if (institution != null) {
                resp.setInstitutionName(institution.getName());
            }
        }

        return resp;
    }
}