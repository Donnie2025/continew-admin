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
import top.continew.admin.education.model.query.TeacherQuery;
import top.continew.admin.education.model.req.TeacherReq;
import top.continew.admin.education.model.resp.TeacherDetailResp;
import top.continew.admin.education.model.resp.TeacherResp;
import top.continew.admin.education.service.TeacherService;
import top.continew.admin.common.enums.DisEnableStatusEnum;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;
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
            .orderByAsc(TeacherDO::getSort)
            .orderByDesc(TeacherDO::getUpdateTime);
        
        // 不限制返回数量，让前端能够显示所有符合条件的老师
        return this.baseMapper.selectList(queryWrapper).stream().map(this::convert).collect(Collectors.toList());
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
            .and(wrapper -> wrapper.like(TeacherDO::getName, keyword).or().like(TeacherDO::getPhone, keyword))
            .orderByAsc(TeacherDO::getSort)
            .orderByDesc(TeacherDO::getUpdateTime)
            .last("LIMIT 20"); // 限制返回数量

        return this.baseMapper.selectList(queryWrapper).stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    protected QueryWrapper<TeacherDO> buildQueryWrapper(TeacherQuery query) {
        QueryWrapper<TeacherDO> queryWrapper = super.buildQueryWrapper(query);
        // 添加默认排序：按sort字段升序排列，如果sort相同则按update_time倒序
        queryWrapper.orderByAsc("sort").orderByDesc("update_time");
        return queryWrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setTop(Long id) {
        TeacherDO teacher = this.baseMapper.selectById(id);
        if (teacher == null) {
            throw new RuntimeException("教师不存在");
        }
        
        // 将sort字段设置为1实现置顶
        teacher.setSort(1);
        this.baseMapper.updateById(teacher);
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