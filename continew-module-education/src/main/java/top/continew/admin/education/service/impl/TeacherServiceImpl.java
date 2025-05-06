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

import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.TeacherMapper;
import top.continew.admin.education.model.entity.TeacherDO;
import top.continew.admin.education.model.query.TeacherQuery;
import top.continew.admin.education.model.req.TeacherReq;
import top.continew.admin.education.model.resp.TeacherDetailResp;
import top.continew.admin.education.model.resp.TeacherResp;
import top.continew.admin.education.service.TeacherService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 教师业务实现
 *
 * @author donnie
 * @since 2025/04/04 18:33
 */
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl extends BaseServiceImpl<TeacherMapper, TeacherDO, TeacherResp, TeacherDetailResp, TeacherQuery, TeacherReq> implements TeacherService {

    @Override
    public List<TeacherResp> listActiveTeachers() {
        return this.baseMapper.selectList(
            new LambdaQueryWrapper<TeacherDO>()
                .eq(TeacherDO::getStatus, 1)
                .orderByAsc(TeacherDO::getSort)
        ).stream()
            .map(this::convert)
            .collect(Collectors.toList());
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