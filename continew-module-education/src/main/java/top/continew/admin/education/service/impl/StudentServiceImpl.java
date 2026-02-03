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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import top.continew.admin.education.model.query.StudentQuery;
import top.continew.admin.education.model.req.StudentBatchImportReq;
import top.continew.admin.education.model.req.StudentReq;
import top.continew.admin.education.model.resp.StudentBatchImportResp;
import top.continew.admin.education.model.resp.StudentDetailResp;
import top.continew.admin.education.model.resp.StudentResp;
import top.continew.admin.education.service.StudentService;
import top.continew.admin.education.service.ClassinUserService;
import top.continew.admin.education.service.InstitutionService;
import top.continew.admin.education.util.InstitutionUtil;
import top.continew.admin.education.client.ClassinClient;
import top.continew.admin.education.model.entity.ClassinUserDO;
import top.continew.admin.education.constant.ClassinConstants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import cn.hutool.core.util.StrUtil;

/**
 * 学生管理业务实现
 *
 * @author don
 * @since 2025/04/20 01:32
 */
@Slf4j
@Service
public class StudentServiceImpl extends BaseServiceImpl<StudentMapper, StudentDO, StudentResp, StudentDetailResp, StudentQuery, StudentReq> implements StudentService {

    private final ClassinClient classinClient;
    private final ClassinUserService classinUserService;
    private final InstitutionService institutionService;

    public StudentServiceImpl(ClassinClient classinClient, ClassinUserService classinUserService, InstitutionService institutionService) {
        this.classinClient = classinClient;
        this.classinUserService = classinUserService;
        this.institutionService = institutionService;
    }

    @Override
    protected QueryWrapper<StudentDO> buildQueryWrapper(StudentQuery query) {
        QueryWrapper<StudentDO> queryWrapper = super.buildQueryWrapper(query);
        
        // 默认只显示启用状态的学生
        queryWrapper.eq("status", 1);
        
        log.debug("学生查询条件: {}", queryWrapper.getTargetSql());
        return queryWrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(StudentReq req) {
        return super.create(req);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StudentReq req, Long id) {
        super.update(req, id);
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
                    // 注意：密码管理已迁移到 CredentialService，不再在此处设置密码
                    newStudent.setStatus(1); // 启用状态

                    // 使用统一的机构ID获取逻辑
                    Long institutionId = InstitutionUtil.getEffectiveInstitutionId(institutionService, "为导入学生设置");
                    newStudent.setInstitutionId(institutionId);

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

    @Override
    public StudentDO getByOpenid(String openid) {
        if (StrUtil.isBlank(openid)) {
            return null;
        }
        LambdaQueryWrapper<StudentDO> wrapper = Wrappers.<StudentDO>lambdaQuery();
        wrapper.eq(StudentDO::getOpenid, openid);
        return this.getOne(wrapper);
    }

    @Override
    public StudentDO getByPhone(String phone) {
        if (StrUtil.isBlank(phone)) {
            return null;
        }
        LambdaQueryWrapper<StudentDO> wrapper = Wrappers.<StudentDO>lambdaQuery();
        wrapper.eq(StudentDO::getPhone, phone);
        return this.getOne(wrapper);
    }

    @Override
    public StudentDO getById(Long id) {
        if (id == null) {
            return null;
        }
        return baseMapper.selectById(id);
    }

    @Override
    public boolean saveStudent(StudentDO student) {
        return this.save(student);
    }

    @Override
    public boolean updateStudent(StudentDO student) {
        return this.updateById(student);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStudentNameAndSyncClassin(Long studentId, String newName) {
        try {
            // 1. 参数校验
            if (studentId == null) {
                log.error("学生ID不能为空");
                return false;
            }
            if (StrUtil.isBlank(newName)) {
                log.error("新姓名不能为空");
                return false;
            }

            // 2. 查询学生信息
            StudentDO student = this.getById(studentId);
            if (student == null) {
                log.error("学生不存在，ID: {}", studentId);
                return false;
            }

            // 3. 更新本地学生姓名
            String oldName = student.getName();
            student.setName(newName);
            boolean updateResult = this.updateById(student);
            if (!updateResult) {
                log.error("更新学生姓名失败，ID: {}", studentId);
                return false;
            }

            log.info("本地更新学生姓名成功: ID={}, oldName={}, newName={}", studentId, oldName, newName);

            // 4. 同步到ClassIn（如果学生已关联ClassIn账号）
            try {
                // 获取学生的ClassIn用户信息
                ClassinUserDO classinUser = classinUserService.getByMemberIdAndUserType(studentId, ClassinConstants.USER_TYPE_STUDENT);
                
                if (classinUser != null && StrUtil.isNotBlank(classinUser.getClassinUid())) {
                    // 获取机构ID（从ClassIn用户记录中获取）
                    Long institutionId = classinUser.getClassinInstitutionId();
                    if (institutionId != null) {
                        // 调用ClassIn API更新学生姓名
                        classinClient.editSchoolStudent(classinUser.getClassinUid(), newName, institutionId);
                        log.info("ClassIn同步学生姓名成功: studentId={}, classinUid={}, newName={}", 
                            studentId, classinUser.getClassinUid(), newName);
                    } else {
                        log.warn("学生关联的ClassIn用户缺少机构ID，无法同步到ClassIn: studentId={}", studentId);
                    }
                } else {
                    log.info("学生未关联ClassIn账号，跳过ClassIn同步: studentId={}", studentId);
                }
            } catch (Exception e) {
                // ClassIn同步失败不影响本地更新的成功
                log.error("同步学生姓名到ClassIn失败，但本地更新成功: studentId={}, newName={}, error={}", 
                    studentId, newName, e.getMessage());
            }

            return true;
        } catch (Exception e) {
            log.error("更新学生姓名并同步ClassIn失败: studentId={}, newName={}, error={}", 
                studentId, newName, e.getMessage(), e);
            return false;
        }
    }
}