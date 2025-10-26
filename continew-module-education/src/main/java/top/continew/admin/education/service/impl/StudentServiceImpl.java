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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.StudentMapper;
import top.continew.admin.education.model.entity.StudentDO;
import top.continew.admin.education.model.entity.ClassinUserDO;
import top.continew.admin.education.model.query.StudentQuery;
import top.continew.admin.education.model.req.StudentBatchImportReq;
import top.continew.admin.education.model.req.StudentReq;
import top.continew.admin.education.model.req.ClassinUserReq;
import top.continew.admin.education.model.resp.StudentBatchImportResp;
import top.continew.admin.education.model.resp.StudentDetailResp;
import top.continew.admin.education.model.resp.StudentResp;
import top.continew.admin.education.service.StudentService;
import top.continew.admin.education.service.ClassinUserService;
import top.continew.admin.education.client.ClassinClient;
import top.continew.admin.education.constant.ClassinConstants;
import top.continew.admin.common.enums.DisEnableStatusEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.RandomUtil;

/**
 * 学生管理业务实现
 *
 * @author don
 * @since 2025/04/20 01:32
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends BaseServiceImpl<StudentMapper, StudentDO, StudentResp, StudentDetailResp, StudentQuery, StudentReq> implements StudentService {

    private final ClassinUserService classinUserService;
    private final ClassinClient classinClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(StudentReq req) {
        // 1. 创建学生
        Long studentId = super.create(req);

        // 2. 检查并创建ClassIn账号
        //        registerClassinStudentIfAbsent(studentId, req);

        return studentId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StudentReq req, Long id) {
        // 1. 更新学生信息
        super.update(req, id);

        // 2. 检查并创建ClassIn账号
        //        registerClassinStudentIfAbsent(id, req);
    }

    @Override
    public List<StudentResp> searchStudents(String keyword) {
        // 构建查询条件
        LambdaQueryWrapper<StudentDO> queryWrapper = Wrappers.lambdaQuery(StudentDO.class)
            .eq(StudentDO::getStatus, 1) // 状态为启用
            .orderByDesc(StudentDO::getCreateTime); // 按创建时间倒序

        // 如果关键字不为空，则添加名字或手机号的模糊查询条件
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper.like(StudentDO::getName, keyword)
                .or()
                .like(StudentDO::getPhone, keyword));
        }

        // 执行查询并转换为响应对象
        List<StudentDO> studentList = this.baseMapper.selectList(queryWrapper);
        return studentList.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentBatchImportResp batchImport(StudentBatchImportReq req) {
        log.info("开始批量导入学生数据");

        // 解析导入数据
        String[] lines = req.getImportData().split("\n");
        List<StudentBatchImportResp.ImportFailureDetail> failures = new ArrayList<>();
        int successCount = 0;

        // 逐行处理
        for (String line : lines) {
            if (StrUtil.isBlank(line)) {
                continue;
            }

            try {
                // 解析每行数据（格式：学生姓名\t手机号码）
                String[] parts = line.trim().split("\t");
                if (parts.length < 2) {
                    failures.add(StudentBatchImportResp.ImportFailureDetail.builder()
                        .studentName(line.trim())
                        .reason("数据格式错误，应为：学生姓名[Tab]手机号码")
                        .build());
                    continue;
                }

                String studentName = parts[0].trim();
                String phone = parts[1].trim();

                // 验证手机号格式（简单验证）
                if (!phone.matches("^1[3-9]\\d{9}$")) {
                    failures.add(StudentBatchImportResp.ImportFailureDetail.builder()
                        .studentName(studentName)
                        .phone(phone)
                        .reason("手机号码格式不正确")
                        .build());
                    continue;
                }

                // 检查学生是否已存在（通过手机号）
                StudentDO existingStudent = baseMapper.selectOne(Wrappers.lambdaQuery(StudentDO.class)
                    .eq(StudentDO::getPhone, phone));

                if (existingStudent != null) {
                    // 学生已存在，更新姓名
                    existingStudent.setName(studentName);
                    baseMapper.updateById(existingStudent);
                    log.debug("更新学生[{}]，手机号：{}", studentName, phone);
                } else {
                    // 创建新学生
                    StudentDO newStudent = new StudentDO();
                    newStudent.setName(studentName);
                    newStudent.setPhone(phone);
                    newStudent.setGender("male"); // 默认性别
                    newStudent.setRegisterTime(LocalDateTime.now());
                    newStudent.setPassword(RandomUtil.randomString(8)); // 生成随机密码
                    newStudent.setStatus(1); // 启用状态

                    // 设置默认机构ID（可根据实际情况调整）
                    newStudent.setInstitutionId(1L);

                    baseMapper.insert(newStudent);
                    log.debug("成功导入学生[{}]，手机号：{}", studentName, phone);
                }

                successCount++;

            } catch (Exception e) {
                log.error("导入数据出错：{}", line, e);
                String studentName = line.substring(0, Math.min(line.length(), 50));
                failures.add(StudentBatchImportResp.ImportFailureDetail.builder()
                    .studentName(studentName)
                    .reason("处理异常：" + e.getMessage())
                    .build());
            }
        }

        log.info("批量导入完成，成功：{}，失败：{}", successCount, failures.size());

        return StudentBatchImportResp.builder()
            .successCount(successCount)
            .failureCount(failures.size())
            .failures(failures)
            .build();
    }

    /**
     * 检查并注册ClassIn学生账号
     *
     * @param studentId 学生ID
     * @param req       学生请求参数
     * @return ClassIn用户信息
     */
    private ClassinUserDO registerClassinStudentIfAbsent(Long studentId, StudentReq req) {
        // 1. 根据学生ID查询ClassIn用户
        ClassinUserDO classinUser = classinUserService
            .getByMemberIdAndUserType(studentId, ClassinConstants.USER_TYPE_STUDENT);
        if (classinUser != null) {
            log.info("学生[{}]已有ClassIn账号，无需创建", studentId);
            return classinUser;
        }

        log.info("学生[{}]没有ClassIn账号，开始创建", studentId);

        // 2. 构建ClassIn用户请求参数
        ClassinUserReq classinUserReq = new ClassinUserReq();
        classinUserReq.setNickname(req.getName());

        // 优先使用手机号，其次使用邮箱
        if (StrUtil.isNotBlank(req.getPhone())) {
            classinUserReq.setTelephone(req.getPhone());
        } else if (StrUtil.isNotBlank(req.getEmail())) {
            classinUserReq.setEmail(req.getEmail());
        }

        // 设置密码，优先使用学生设置的密码，其次使用随机生成的密码
        String password = StrUtil.isNotBlank(req.getPassword()) ? req.getPassword() : RandomUtil.randomString(8);
        classinUserReq.setPassword(password);

        // 设置用户类型为学生
        classinUserReq.setUserType(ClassinConstants.USER_TYPE_STUDENT);

        // 3. 调用ClassIn注册接口
        try {
            String classinUid = classinClient.registerClassin(classinUserReq);

            // 4. 创建并保存ClassIn用户关联
            // 使用ClassinUserReq和create方法保存
            ClassinUserReq userReq = new ClassinUserReq();
            userReq.setMemberId(studentId);
            userReq.setUserType(ClassinConstants.USER_TYPE_STUDENT);
            userReq.setClassinUid(classinUid);
            userReq.setNickname(req.getName());
            userReq.setTelephone(req.getPhone());
            userReq.setEmail(req.getEmail());
            userReq.setPassword(classinUserReq.getPassword());
            userReq.setStatus(DisEnableStatusEnum.ENABLE.getValue());

            // 设置ClassIn机构ID（从配置中获取或使用默认值，或者使用学生的机构ID）
            userReq.setClassinInstitutionId(req.getInstitutionId() != null ? req.getInstitutionId() : 1L);

            classinUserService.create(userReq);
            log.info("学生[{}]ClassIn账号创建成功，ClassIn UID: {}", studentId, classinUid);

            // 创建ClassinUserDO对象并返回，仅用于方法返回
            ClassinUserDO newClassinUser = new ClassinUserDO();
            newClassinUser.setMemberId(studentId);
            newClassinUser.setUserType(ClassinConstants.USER_TYPE_STUDENT);
            newClassinUser.setClassinUid(classinUid);
            newClassinUser.setNickname(req.getName());
            newClassinUser.setTelephone(req.getPhone());
            newClassinUser.setEmail(req.getEmail());
            newClassinUser.setPassword(classinUserReq.getPassword());
            newClassinUser.setStatus(DisEnableStatusEnum.ENABLE.getValue());

            return newClassinUser;
        } catch (Exception e) {
            log.error("学生[{}]ClassIn账号创建失败: {}", studentId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 将 StudentDO 转换为 StudentResp
     *
     * @param entity StudentDO 实体
     * @return StudentResp 响应对象
     */
    private StudentResp convert(StudentDO entity) {
        if (entity == null) {
            return null;
        }
        StudentResp resp = new StudentResp();
        BeanUtils.copyProperties(entity, resp);
        return resp;
    }
}