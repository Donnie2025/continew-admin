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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.TeacherMapper;
import top.continew.admin.education.model.entity.TeacherDO;
import top.continew.admin.education.model.entity.ClassinUserDO;
import top.continew.admin.education.model.query.TeacherQuery;
import top.continew.admin.education.model.req.TeacherReq;
import top.continew.admin.education.model.req.ClassinUserReq;
import top.continew.admin.education.model.resp.TeacherDetailResp;
import top.continew.admin.education.model.resp.TeacherResp;
import top.continew.admin.education.service.TeacherService;
import top.continew.admin.education.service.ClassinUserService;
import top.continew.admin.education.client.ClassinClient;
import top.continew.admin.education.constant.ClassinConstants;
import top.continew.admin.common.enums.DisEnableStatusEnum;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.stream.Collectors;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.RandomUtil;

/**
 * 教师业务实现
 *
 * @author donnie
 * @since 2025/04/04 18:33
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl extends BaseServiceImpl<TeacherMapper, TeacherDO, TeacherResp, TeacherDetailResp, TeacherQuery, TeacherReq> implements TeacherService {

    private final ClassinUserService classinUserService;
    private final ClassinClient classinClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(TeacherReq req) {
        // 1. 创建教师
        Long teacherId = super.create(req);

        // 2. 检查并创建ClassIn账号
        registerClassinTeacherIfAbsent(teacherId, req);

        return teacherId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TeacherReq req, Long id) {
        // 1. 更新教师信息
        super.update(req, id);

        // 2. 检查并创建ClassIn账号
        registerClassinTeacherIfAbsent(id, req);
    }

    @Override
    public List<TeacherResp> listActiveTeachers(String name) {
        LambdaQueryWrapper<TeacherDO> queryWrapper = new LambdaQueryWrapper<TeacherDO>().eq(TeacherDO::getStatus, 1)
            .like(name != null && !name.trim().isEmpty(), TeacherDO::getName, name)
            .orderByAsc(TeacherDO::getSort);

        return this.baseMapper.selectList(queryWrapper).stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public TeacherDO getByPhone(String phone) {
        return this.baseMapper.selectByPhone(phone);
    }

    /**
     * 检查并注册ClassIn教师账号
     *
     * @param teacherId 教师ID
     * @param req       教师请求参数
     * @return ClassIn用户信息
     */
    private ClassinUserDO registerClassinTeacherIfAbsent(Long teacherId, TeacherReq req) {
        // 1. 根据教师ID查询ClassIn用户
        ClassinUserDO classinUser = classinUserService
            .getByMemberIdAndUserType(teacherId, ClassinConstants.USER_TYPE_TEACHER);
        if (classinUser != null) {
            log.info("教师[{}]已有ClassIn账号，无需创建", teacherId);
            return classinUser;
        }

        log.info("教师[{}]没有ClassIn账号，开始创建", teacherId);

        // 2. 构建ClassIn用户请求参数
        ClassinUserReq classinUserReq = new ClassinUserReq();
        classinUserReq.setNickname(req.getName());

        // 优先使用手机号，其次使用邮箱
        if (StrUtil.isNotBlank(req.getPhone())) {
            classinUserReq.setTelephone(req.getPhone());
        } else if (StrUtil.isNotBlank(req.getEmail())) {
            classinUserReq.setEmail(req.getEmail());
        }

        // 设置随机密码（实际应用中可能需要更复杂的密码生成策略）
        classinUserReq.setPassword(RandomUtil.randomString(8));

        // 设置用户类型为教师
        classinUserReq.setUserType(ClassinConstants.USER_TYPE_TEACHER);

        // 3. 调用ClassIn注册接口
        try {
            String classinUid = classinClient.registerClassin(classinUserReq);

            // 4. 创建并保存ClassIn用户关联
            ClassinUserDO newClassinUser = new ClassinUserDO();
            newClassinUser.setMemberId(teacherId);
            newClassinUser.setUserType(ClassinConstants.USER_TYPE_TEACHER);
            newClassinUser.setClassinUid(classinUid);
            newClassinUser.setNickname(req.getName());
            newClassinUser.setTelephone(req.getPhone());
            newClassinUser.setEmail(req.getEmail());
            newClassinUser.setPassword(classinUserReq.getPassword());
            newClassinUser.setStatus(DisEnableStatusEnum.ENABLE.getValue());

            // 使用ClassinUserReq和create方法保存
            ClassinUserReq userReq = new ClassinUserReq();
            userReq.setMemberId(teacherId);
            userReq.setUserType(ClassinConstants.USER_TYPE_TEACHER);
            userReq.setClassinUid(classinUid);
            userReq.setNickname(req.getName());
            userReq.setTelephone(req.getPhone());
            userReq.setEmail(req.getEmail());
            userReq.setPassword(classinUserReq.getPassword());
            userReq.setStatus(DisEnableStatusEnum.ENABLE.getValue());

            // 设置ClassIn机构ID（从配置中获取或使用默认值）
            userReq.setClassinInstitutionId(1L); // 这里应该从配置中获取实际的机构ID

            classinUserService.create(userReq);
            log.info("教师[{}]ClassIn账号创建成功，ClassIn UID: {}", teacherId, classinUid);

            return newClassinUser;
        } catch (Exception e) {
            log.error("教师[{}]ClassIn账号创建失败: {}", teacherId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 将 TeacherDO 转换为 TeacherResp
     *
     * @param entity TeacherDO 实体
     * @return TeacherResp 响应对象
     */
    private TeacherResp convert(TeacherDO entity) {
        if (entity == null) {
            return null;
        }
        TeacherResp resp = new TeacherResp();
        BeanUtils.copyProperties(entity, resp);
        return resp;
    }
}