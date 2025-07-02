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
import top.continew.admin.education.model.req.StudentReq;
import top.continew.admin.education.model.req.ClassinUserReq;
import top.continew.admin.education.model.resp.StudentDetailResp;
import top.continew.admin.education.model.resp.StudentResp;
import top.continew.admin.education.service.StudentService;
import top.continew.admin.education.service.ClassinUserService;
import top.continew.admin.education.client.ClassinClient;
import top.continew.admin.education.constant.ClassinConstants;
import top.continew.admin.common.enums.DisEnableStatusEnum;

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
        registerClassinStudentIfAbsent(studentId, req);
        
        return studentId;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StudentReq req, Long id) {
        // 1. 更新学生信息
        super.update(req, id);
        
        // 2. 检查并创建ClassIn账号
        registerClassinStudentIfAbsent(id, req);
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
    
    /**
     * 检查并注册ClassIn学生账号
     *
     * @param studentId 学生ID
     * @param req 学生请求参数
     * @return ClassIn用户信息
     */
    private ClassinUserDO registerClassinStudentIfAbsent(Long studentId, StudentReq req) {
        // 1. 根据学生ID查询ClassIn用户
        ClassinUserDO classinUser = classinUserService.getByMemberIdAndUserType(studentId, ClassinConstants.USER_TYPE_STUDENT);
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