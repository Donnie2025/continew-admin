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
import top.continew.admin.education.model.query.StudentQuery;
import top.continew.admin.education.model.req.StudentBatchImportReq;
import top.continew.admin.education.model.req.StudentReq;
import top.continew.admin.education.model.resp.StudentBatchImportResp;
import top.continew.admin.education.model.resp.StudentDetailResp;
import top.continew.admin.education.model.resp.StudentResp;
import top.continew.admin.education.service.StudentService;

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
@RequiredArgsConstructor
public class StudentServiceImpl extends BaseServiceImpl<StudentMapper, StudentDO, StudentResp, StudentDetailResp, StudentQuery, StudentReq> implements StudentService {

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