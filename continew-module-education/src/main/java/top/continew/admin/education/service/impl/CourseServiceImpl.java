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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.continew.admin.education.client.ClassinClient;
import top.continew.admin.education.constant.ClassinConstants;
import top.continew.admin.education.model.entity.ClassinUserDO;
import top.continew.admin.education.model.entity.TeacherDO;
import top.continew.admin.education.model.req.classin.ClassinCourseAddReq;
import top.continew.admin.education.service.ClassinUserService;
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
import java.util.UUID;

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
    private final ClassinClient classinClient;
    private final ClassinUserService classinUserService;

    @Override
    public Long create(CourseReq req) {
        CourseDO entity = BeanUtil.copyProperties(req, CourseDO.class);

        // 1. 处理班主任信息
        String mainTeacherUid = null;
        if (StrUtil.isNotBlank(req.getMainTeacherPhone())) {
            // 1.1 查询本地教师
            TeacherDO teacher = teacherService.getByPhone(req.getMainTeacherPhone());
            CheckUtils.throwIfNull(teacher, "手机号为 {} 的教师不存在", req.getMainTeacherPhone());
            entity.setMainTeacherId(teacher.getId());

            // 1.2 查询教师的 ClassIn 用户信息
            ClassinUserDO classinTeacher = classinUserService.getByMemberIdAndUserType(teacher
                .getId(), ClassinConstants.USER_TYPE_TEACHER);
            if (classinTeacher != null) {
                mainTeacherUid = classinTeacher.getClassinUid();
                entity.setMainTeacherUid(mainTeacherUid);
            } else {
                // 可根据业务需求决定是否要在此处为教师自动注册 Classin 账号
                log.warn("ID为 {} 的教师（{}）尚未关联 ClassIn 账号", teacher.getId(), teacher.getName());
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
}