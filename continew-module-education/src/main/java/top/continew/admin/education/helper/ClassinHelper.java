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

package top.continew.admin.education.helper;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.continew.admin.common.enums.DisEnableStatusEnum;
import top.continew.admin.education.client.ClassinClient;
import top.continew.admin.education.constant.ClassinConstants;
import top.continew.admin.education.model.entity.ClassinUserDO;
import top.continew.admin.education.model.entity.StudentDO;
import top.continew.admin.education.model.entity.TeacherDO;
import top.continew.admin.education.model.req.ClassinUserReq;
import top.continew.admin.education.service.ClassinUserService;
import top.continew.admin.education.service.InstitutionService;
import top.continew.admin.education.util.InstitutionUtil;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClassinHelper {

    private final ClassinUserService classinUserService;
    private final ClassinClient classinClient;
    private final InstitutionService institutionService;

    public ClassinUserDO getClassinUser(Long memberId, String userType, String name, String phone, String email) {
        // 查询学生的 Classin 用户信息
        ClassinUserDO classinUser = classinUserService.getByMemberIdAndUserType(memberId, userType);
        if (classinUser == null) {
            log.info("未找到 Classin 用户信息，开始注册: memberId={}", memberId);
            // 构建注册请求参数
            ClassinUserReq registerReq = new ClassinUserReq();
            // 设置用户信息
            registerReq.setNickname(name);
            registerReq.setTelephone(phone);
            registerReq.setEmail(email);
            // 生成随机密码
            String randomPassword = UUID.randomUUID().toString().substring(0, 8);
            registerReq.setPassword(randomPassword);
            // 设置学生ID
            registerReq.setMemberId(memberId);
            // 设置用户类型为学生
            registerReq.setUserType(userType);
            // 设置机构ID - 使用InstitutionUtil获取有效的机构ID
            Long institutionId = InstitutionUtil.getEffectiveInstitutionId(institutionService, "为用户注册ClassIn设置");
            registerReq.setClassinInstitutionId(institutionId);
            try {
                // 调用注册接口
                Long classinUserId = classinUserService.create(registerReq);
                log.info("Classin 用户注册成功: memberId={}, classinUserId={}", memberId, classinUserId);

                // 重新查询用户信息
                classinUser = classinUserService.getByMemberIdAndUserType(memberId, userType);
            } catch (Exception e) {
                log.error("Classin 用户注册失败: memberId={}, error={}", memberId, e.getMessage(), e);
                throw e;
            }
        }
        return classinUser;
    }

    /**
     * 检查并注册学生的ClassIn账号（如果不存在或不属于指定机构）
     *
     * @param studentId     学生ID
     * @param student       学生信息
     * @param institutionId 机构ID
     * @return ClassIn用户信息，如果注册失败返回null
     */
    public ClassinUserDO registerStudentIfAbsent(Long studentId, StudentDO student, Long institutionId) {
        // 1. 查询学生在指定机构下的 ClassIn 用户信息
        ClassinUserDO classinUser = classinUserService
            .getByMemberIdAndUserTypeAndInstitution(studentId, ClassinConstants.USER_TYPE_STUDENT, institutionId);

        if (classinUser != null) {
            log.info("学生[{}]在机构[{}]下已有ClassIn账号，无需创建", student.getName(), institutionId);
            return classinUser;
        }

        log.info("学生[{}]在机构[{}]下没有ClassIn账号，开始自动创建", student.getName(), institutionId);

        try {
            // 2. 构建ClassIn用户请求参数
            ClassinUserReq classinUserReq = new ClassinUserReq();
            classinUserReq.setNickname(student.getName());

            // 优先使用手机号，其次使用邮箱
            if (StrUtil.isNotBlank(student.getPhone())) {
                classinUserReq.setTelephone(student.getPhone());
            } else if (StrUtil.isNotBlank(student.getEmail())) {
                classinUserReq.setEmail(student.getEmail());
            } else {
                log.warn("学生[{}]缺少手机号和邮箱，无法注册ClassIn账号", student.getName());
                return null;
            }

            // 设置随机密码
            classinUserReq.setPassword(RandomUtil.randomString(8));

            // 设置用户类型为学生
            classinUserReq.setUserType(ClassinConstants.USER_TYPE_STUDENT);

            // 设置机构ID
            classinUserReq.setClassinInstitutionId(institutionId);

            // 3. 调用ClassIn注册接口（使用指定机构的配置）
            String classinUid = classinClient.registerClassin(classinUserReq, institutionId);

            // 4. 保存ClassIn用户关联
            ClassinUserReq userReq = new ClassinUserReq();
            userReq.setMemberId(studentId);
            userReq.setUserType(ClassinConstants.USER_TYPE_STUDENT);
            userReq.setClassinUid(classinUid);
            userReq.setNickname(student.getName());
            userReq.setTelephone(student.getPhone());
            userReq.setEmail(student.getEmail());
            userReq.setPassword(classinUserReq.getPassword());
            userReq.setStatus(DisEnableStatusEnum.ENABLE.getValue());
            userReq.setClassinInstitutionId(institutionId);

            classinUserService.create(userReq);
            log.info("学生[{}]在机构[{}]下ClassIn账号自动创建成功，ClassIn UID: {}", student.getName(), institutionId, classinUid);

            // 5. 返回创建的ClassIn用户信息
            ClassinUserDO newClassinUser = new ClassinUserDO();
            newClassinUser.setMemberId(studentId);
            newClassinUser.setUserType(ClassinConstants.USER_TYPE_STUDENT);
            newClassinUser.setClassinUid(classinUid);
            newClassinUser.setNickname(student.getName());
            newClassinUser.setTelephone(student.getPhone());
            newClassinUser.setEmail(student.getEmail());
            newClassinUser.setPassword(classinUserReq.getPassword());
            newClassinUser.setStatus(DisEnableStatusEnum.ENABLE.getValue());
            newClassinUser.setClassinInstitutionId(institutionId);

            return newClassinUser;
        } catch (Exception e) {
            log.error("学生[{}]在机构[{}]下ClassIn账号自动创建失败: {}", student.getName(), institutionId, e.getMessage(), e);
            // 不抛出异常，允许调用方继续处理
            return null;
        }
    }

    /**
     * 检查并注册教师的ClassIn账号（如果不存在或不属于指定机构）
     *
     * @param teacherId     教师ID
     * @param teacher       教师信息
     * @param institutionId 机构ID
     * @return ClassIn用户信息，如果注册失败返回null
     */
    public ClassinUserDO registerTeacherIfAbsent(Long teacherId, TeacherDO teacher, Long institutionId) {
        // 1. 查询教师在指定机构下的 ClassIn 用户信息
        ClassinUserDO classinUser = classinUserService
            .getByMemberIdAndUserTypeAndInstitution(teacherId, ClassinConstants.USER_TYPE_TEACHER, institutionId);

        if (classinUser != null) {
            log.info("教师[{}]在机构[{}]下已有ClassIn账号，无需创建", teacher.getName(), institutionId);
            return classinUser;
        }

        log.info("教师[{}]在机构[{}]下没有ClassIn账号，开始自动创建", teacher.getName(), institutionId);

        try {
            // 2. 构建ClassIn用户请求参数
            ClassinUserReq classinUserReq = new ClassinUserReq();
            classinUserReq.setNickname(teacher.getName());

            // 优先使用手机号，其次使用邮箱
            if (StrUtil.isNotBlank(teacher.getPhone())) {
                classinUserReq.setTelephone(teacher.getPhone());
            } else if (StrUtil.isNotBlank(teacher.getEmail())) {
                classinUserReq.setEmail(teacher.getEmail());
            } else {
                log.warn("教师[{}]缺少手机号和邮箱，无法注册ClassIn账号", teacher.getName());
                return null;
            }

            // 设置随机密码
            classinUserReq.setPassword(RandomUtil.randomString(8));

            // 设置用户类型为教师
            classinUserReq.setUserType(ClassinConstants.USER_TYPE_TEACHER);

            // 设置机构ID
            classinUserReq.setClassinInstitutionId(institutionId);

            // 3. 调用ClassIn注册接口（使用指定机构的配置）
            String classinUid = classinClient.registerClassin(classinUserReq, institutionId);

            // 4. 保存ClassIn用户关联
            ClassinUserReq userReq = new ClassinUserReq();
            userReq.setMemberId(teacherId);
            userReq.setUserType(ClassinConstants.USER_TYPE_TEACHER);
            userReq.setClassinUid(classinUid); // 设置已获取的ClassIn UID，避免重复注册
            userReq.setNickname(teacher.getName());
            userReq.setTelephone(teacher.getPhone());
            userReq.setEmail(teacher.getEmail());
            userReq.setPassword(classinUserReq.getPassword());
            userReq.setStatus(DisEnableStatusEnum.ENABLE.getValue());
            userReq.setClassinInstitutionId(institutionId);

            // 调用create方法，由于已设置Classin UID，不会再次调用注册接口
            classinUserService.create(userReq);
            log.info("教师[{}]在机构[{}]下ClassIn账号自动创建成功，ClassIn UID: {}", teacher.getName(), institutionId, classinUid);

            // 5. 返回创建的ClassIn用户信息
            ClassinUserDO newClassinUser = new ClassinUserDO();
            newClassinUser.setMemberId(teacherId);
            newClassinUser.setUserType(ClassinConstants.USER_TYPE_TEACHER);
            newClassinUser.setClassinUid(classinUid);
            newClassinUser.setNickname(teacher.getName());
            newClassinUser.setTelephone(teacher.getPhone());
            newClassinUser.setEmail(teacher.getEmail());
            newClassinUser.setPassword(classinUserReq.getPassword());
            newClassinUser.setStatus(DisEnableStatusEnum.ENABLE.getValue());
            newClassinUser.setClassinInstitutionId(institutionId);

            return newClassinUser;
        } catch (Exception e) {
            log.error("教师[{}]在机构[{}]下ClassIn账号自动创建失败: {}", teacher.getName(), institutionId, e.getMessage(), e);
            // 不抛出异常，允许调用方继续处理
            return null;
        }
    }

}
