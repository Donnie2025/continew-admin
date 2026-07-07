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

import top.continew.admin.education.enums.UserType;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.TeacherMapper;
import top.continew.admin.education.mapper.SlotMapper;
import top.continew.admin.education.model.entity.SlotDO;
import top.continew.admin.education.model.entity.TeacherDO;
import top.continew.admin.education.model.query.TeacherQuery;
import top.continew.admin.education.model.req.TeacherReq;
import top.continew.admin.education.model.req.TeacherRegisterReq;
import top.continew.admin.education.model.resp.TeacherDetailResp;
import top.continew.admin.education.model.resp.TeacherResp;
import top.continew.admin.education.model.resp.TeacherPublicResp;
import top.continew.admin.education.service.TeacherService;
import top.continew.admin.education.service.TeacherPaymentService;
import top.continew.admin.education.service.CredentialService;
import top.continew.admin.education.model.req.CredentialSetPasswordReq;
import top.continew.admin.common.enums.DisEnableStatusEnum;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import cn.hutool.core.util.StrUtil;

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

    private final SlotMapper slotMapper;
    private final TeacherPaymentService teacherPaymentService;
    private final CredentialService credentialService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(TeacherReq req) {
        return super.create(req);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TeacherReq req, Long id) {
        super.update(req, id);
    }

    @Override
    public List<TeacherResp> listActiveTeachers(String name) {
        LambdaQueryWrapper<TeacherDO> queryWrapper = new LambdaQueryWrapper<TeacherDO>().eq(TeacherDO::getStatus, 1)
            .like(name != null && !name.trim().isEmpty(), TeacherDO::getName, name)
            .orderByDesc(TeacherDO::getSort)
            .orderByDesc(TeacherDO::getCreateTime);
        return this.baseMapper.selectList(queryWrapper).stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> listActiveTeachersPage(String name,
                                                      String startDate,
                                                      String startTimeFrom,
                                                      String startTimeTo,
                                                      int page,
                                                      int pageSize) {
        LambdaQueryWrapper<TeacherDO> queryWrapper = new LambdaQueryWrapper<TeacherDO>().eq(TeacherDO::getStatus, 1)
            .eq(TeacherDO::getIsShow, 1)
            .like(name != null && !name.trim().isEmpty(), TeacherDO::getName, name);

        // 按指定日期+时间范围筛选有可约课时的教师
        if (StrUtil.isNotBlank(startDate) && StrUtil.isNotBlank(startTimeFrom)) {
            LambdaQueryWrapper<SlotDO> slotWrapper = new LambdaQueryWrapper<SlotDO>()
                .eq(SlotDO::getStartDate, startDate)
                .ge(SlotDO::getStartTime, startTimeFrom)
                .le(SlotDO::getStartTime, StrUtil.isNotBlank(startTimeTo) ? startTimeTo : startTimeFrom)
                .eq(SlotDO::getStatus, 1);
            List<Long> teacherIds = slotMapper.selectList(slotWrapper)
                .stream()
                .map(SlotDO::getTeacherId)
                .distinct()
                .collect(Collectors.toList());
            if (teacherIds.isEmpty()) {
                Map<String, Object> emptyMap = new HashMap<>();
                emptyMap.put("list", Collections.emptyList());
                emptyMap.put("total", 0L);
                emptyMap.put("page", page);
                emptyMap.put("pageSize", pageSize);
                emptyMap.put("hasMore", false);
                return emptyMap;
            }
            queryWrapper.in(TeacherDO::getId, teacherIds);
        }

        queryWrapper.orderByDesc(TeacherDO::getSort).orderByDesc(TeacherDO::getCreateTime);

        Page<TeacherDO> pageObj = new Page<>(page, pageSize);
        IPage<TeacherDO> result = this.baseMapper.selectPage(pageObj, queryWrapper);

        List<TeacherResp> list = result.getRecords().stream().map(this::convert).collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("total", result.getTotal());
        map.put("page", page);
        map.put("pageSize", pageSize);
        map.put("hasMore", (long)page * pageSize < result.getTotal());
        return map;
    }

    @Override
    public TeacherDO getByPhone(String phone) {
        return this.baseMapper.selectByPhone(phone);
    }

    @Override
    public List<TeacherResp> searchTeachers(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return List.of();
        }

        LambdaQueryWrapper<TeacherDO> queryWrapper = new LambdaQueryWrapper<TeacherDO>()
            .eq(TeacherDO::getStatus, DisEnableStatusEnum.ENABLE.getValue())
            .eq(TeacherDO::getIsShow, 1)
            .and(wrapper -> wrapper.like(TeacherDO::getName, keyword).or().like(TeacherDO::getPhone, keyword))
            .orderByDesc(TeacherDO::getSort)
            .orderByDesc(TeacherDO::getCreateTime)
            .last("LIMIT 20"); // 限制返回数量

        return this.baseMapper.selectList(queryWrapper).stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    protected QueryWrapper<TeacherDO> buildQueryWrapper(TeacherQuery query) {
        QueryWrapper<TeacherDO> queryWrapper = super.buildQueryWrapper(query);
        // 添加默认排序：按sort字段降序排列（数字越大越靠前），如果sort相同则按create_time倒序
        queryWrapper.orderByDesc("sort").orderByDesc("create_time");
        return queryWrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setTop(Long id) {
        TeacherDO teacher = this.baseMapper.selectById(id);
        if (teacher == null) {
            throw new RuntimeException("教师不存在");
        }
        teacher.setSort(1);
        this.baseMapper.updateById(teacher);
    }

    @Override
    public TeacherDO getById(Long id) {
        if (id == null) {
            return null;
        }
        return this.baseMapper.selectById(id);
    }

    @Override
    public TeacherPublicResp getPublicInfo(Long id) {
        TeacherDetailResp detail = this.get(id);
        if (detail == null) {
            return null;
        }
        TeacherPublicResp publicResp = new TeacherPublicResp();
        BeanUtils.copyProperties(detail, publicResp);
        return publicResp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(TeacherRegisterReq req) {
        // Check if phone already exists
        String phoneWithPrefix = req.getPhone();
        if (!phoneWithPrefix.startsWith("0063-")) {
            phoneWithPrefix = "0063-" + phoneWithPrefix;
        }

        TeacherDO existingTeacher = this.getByPhone(phoneWithPrefix);
        if (existingTeacher != null) {
            throw new RuntimeException("该手机号已被注册");
        }

        // Create new teacher entity
        TeacherDO teacher = new TeacherDO();
        teacher.setName(req.getName());
        teacher.setPhone(phoneWithPrefix);
        teacher.setEmail(req.getEmail());
        teacher.setAvatar(req.getAvatar());
        teacher.setVideoUrl(req.getVideoUrl());
        teacher.setDescription(req.getDescription());
        teacher.setGender(req.getGender());

        // Set default values - active immediately, no review required
        teacher.setStatus(1); // 1: active
        teacher.setIsShow(1); // Show to students
        teacher.setIsFixed(0);
        teacher.setScore(5); // Default score
        teacher.setRate(0); // Will be set by admin
        teacher.setShowSalary(0);
        teacher.setSort(999); // Default sort order

        this.baseMapper.insert(teacher);
        log.info("New teacher registered: {} (ID: {}), status: active", teacher.getName(), teacher.getId());

        // Save password to edu_credential table
        if (StrUtil.isNotBlank(req.getPassword())) {
            try {
                CredentialSetPasswordReq passwordReq = new CredentialSetPasswordReq();
                passwordReq.setUserId(teacher.getId());
                passwordReq.setUserType(UserType.TEACHER.getValue());
                passwordReq.setPassword(req.getPassword());
                credentialService.setPassword(passwordReq);
                log.info("Teacher password set successfully for ID: {}", teacher.getId());
            } catch (Exception e) {
                log.error("Failed to set password for teacher ID: {}", teacher.getId(), e);
                // 不抛出异常，密码设置失败不影响注册流程
            }
        }

        // Save payment information if provided
        if (StrUtil.isNotBlank(req.getPaymentChannel())) {
            teacherPaymentService.saveOrUpdate(teacher.getId(), teacher.getName(), req.getPaymentChannel(), req
                .getAccountNumber(), req.getAccountName(), req.getQrCode(), req.getBankName(), 0  // rate starts at 0, will be set by admin
            );
        }

        return teacher.getId();
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