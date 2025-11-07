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
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.education.client.ClassinClient;
import top.continew.admin.education.constant.ClassinConstants;
import top.continew.admin.education.model.entity.ClassinUserDO;
import top.continew.admin.education.model.entity.TeacherDO;
import top.continew.admin.education.model.req.ClassinUserReq;
import top.continew.admin.education.model.req.classin.ClassinCourseAddReq;
import top.continew.admin.education.service.ClassinUserService;
import top.continew.admin.education.service.CourseTeacherService;
import top.continew.admin.education.service.CourseStudentService;
import top.continew.admin.education.service.TeacherService;
import top.continew.starter.core.validation.CheckUtils;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.CourseMapper;
import top.continew.admin.education.model.entity.CourseDO;
import top.continew.admin.education.model.query.CourseQuery;
import top.continew.admin.education.model.req.CourseReq;
import top.continew.admin.education.model.resp.CourseDetailResp;
import top.continew.admin.education.model.resp.CourseResp;
import top.continew.admin.education.service.CourseService;
import top.continew.starter.extension.crud.model.query.PageQuery;
import top.continew.starter.extension.crud.model.resp.PageResp;
import top.continew.admin.education.mapper.TeacherMapper;
import java.util.UUID;
import java.util.List;

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
    private final ClassinClient classinClient;
    private final ClassinUserService classinUserService;
    private final CourseTeacherService courseTeacherService;
    private final CourseStudentService courseStudentService;

    @Override
    public Long create(CourseReq req) {
        CourseDO entity = BeanUtil.copyProperties(req, CourseDO.class);

        // 1. 处理班主任信息
        String mainTeacherUid = null;
        if (req.getMainTeacherId() != null) {
            // 1.1 查询或自动注册教师的 ClassIn 账号
            ClassinUserDO classinTeacher = registerTeacherIfAbsent(req.getMainTeacherId());
            if (classinTeacher != null) {
                mainTeacherUid = classinTeacher.getClassinUid();
                entity.setMainTeacherUid(mainTeacherUid);
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
        if (req.getMainTeacherId() != null) {
            // 3.1 查询或自动注册教师的 ClassIn 账号
            ClassinUserDO classinTeacher = registerTeacherIfAbsent(req.getMainTeacherId());
            if (classinTeacher != null) {
                mainTeacherUid = classinTeacher.getClassinUid();
                entity.setMainTeacherUid(mainTeacherUid);
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

        // 2. 只填充班主任姓名，不查询关联的老师和学生（提升列表页性能）
        List<CourseResp> records = page.getList();
        if (!records.isEmpty()) {
            // 2.1 收集所有班主任ID
            List<Long> mainTeacherIds = records.stream()
                .map(CourseResp::getMainTeacherId)
                .filter(id -> id != null)
                .distinct()
                .collect(java.util.stream.Collectors.toList());

            // 2.2 批量查询所有班主任信息（优化：1次查询代替N次）
            if (!mainTeacherIds.isEmpty()) {
                com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TeacherDO> wrapper = com.baomidou.mybatisplus.core.toolkit.Wrappers
                    .lambdaQuery(TeacherDO.class)
                    .in(TeacherDO::getId, mainTeacherIds);
                List<TeacherDO> teachers = teacherMapper.selectList(wrapper);
                java.util.Map<Long, String> teacherNameMap = teachers.stream()
                    .collect(java.util.stream.Collectors.toMap(TeacherDO::getId, TeacherDO::getName));

                // 2.3 填充班主任姓名
                for (CourseResp record : records) {
                    if (record.getMainTeacherId() != null) {
                        record.setMainTeacherName(teacherNameMap.get(record.getMainTeacherId()));
                    }
                }
            }
        }

        return page;
    }

    /**
     * 检查并注册教师的ClassIn账号（如果不存在）
     *
     * @param teacherId 教师ID
     * @return ClassIn用户信息，如果注册失败返回null
     */
    private ClassinUserDO registerTeacherIfAbsent(Long teacherId) {
        // 1. 查询教师的 ClassIn 用户信息
        ClassinUserDO classinTeacher = classinUserService
            .getByMemberIdAndUserType(teacherId, ClassinConstants.USER_TYPE_TEACHER);

        if (classinTeacher != null) {
            log.info("教师[{}]已有ClassIn账号，无需创建", teacherId);
            return classinTeacher;
        }

        log.info("教师[{}]没有ClassIn账号，开始自动创建", teacherId);

        try {
            // 2. 查询教师信息
            TeacherDO teacher = teacherMapper.selectById(teacherId);
            CheckUtils.throwIfNull(teacher, "教师不存在，ID: {}", teacherId);

            // 3. 构建ClassIn用户请求参数
            ClassinUserReq classinUserReq = new ClassinUserReq();
            classinUserReq.setNickname(teacher.getName());

            // 优先使用手机号，其次使用邮箱
            if (StrUtil.isNotBlank(teacher.getPhone())) {
                classinUserReq.setTelephone(teacher.getPhone());
            } else if (StrUtil.isNotBlank(teacher.getEmail())) {
                classinUserReq.setEmail(teacher.getEmail());
            } else {
                log.warn("教师[{}]缺少手机号和邮箱，无法注册ClassIn账号", teacherId);
                return null;
            }

            // 设置随机密码
            classinUserReq.setPassword(RandomUtil.randomString(8));

            // 设置用户类型为教师
            classinUserReq.setUserType(ClassinConstants.USER_TYPE_TEACHER);

            // 4. 调用ClassIn注册接口
            String classinUid = classinClient.registerClassin(classinUserReq);

            // 5. 保存ClassIn用户关联
            ClassinUserReq userReq = new ClassinUserReq();
            userReq.setMemberId(teacherId);
            userReq.setUserType(ClassinConstants.USER_TYPE_TEACHER);
            userReq.setClassinUid(classinUid);
            userReq.setNickname(teacher.getName());
            userReq.setTelephone(teacher.getPhone());
            userReq.setEmail(teacher.getEmail());
            userReq.setPassword(classinUserReq.getPassword());
            userReq.setStatus(DisEnableStatusEnum.ENABLE.getValue());
            userReq.setClassinInstitutionId(1L); // 默认机构ID

            classinUserService.create(userReq);
            log.info("教师[{}]ClassIn账号自动创建成功，ClassIn UID: {}", teacherId, classinUid);

            // 6. 返回创建的ClassIn用户信息
            ClassinUserDO newClassinUser = new ClassinUserDO();
            newClassinUser.setMemberId(teacherId);
            newClassinUser.setUserType(ClassinConstants.USER_TYPE_TEACHER);
            newClassinUser.setClassinUid(classinUid);
            newClassinUser.setNickname(teacher.getName());
            newClassinUser.setTelephone(teacher.getPhone());
            newClassinUser.setEmail(teacher.getEmail());
            newClassinUser.setPassword(classinUserReq.getPassword());
            newClassinUser.setStatus(DisEnableStatusEnum.ENABLE.getValue());

            return newClassinUser;
        } catch (Exception e) {
            log.error("教师[{}]ClassIn账号自动创建失败: {}", teacherId, e.getMessage(), e);
            // 不抛出异常，允许课程创建继续进行
            return null;
        }
    }
}