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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.continew.admin.education.mapper.InstitutionMapper;
import top.continew.admin.education.model.entity.InstitutionDO;
import top.continew.admin.education.model.resp.InstitutionResp;
import top.continew.admin.education.service.InstitutionService;

import java.util.List;

/**
 * 机构业务实现
 *
 * @author don
 * @since 2025/11/07
 */
@Service
@RequiredArgsConstructor
public class InstitutionServiceImpl implements InstitutionService {

    private final InstitutionMapper institutionMapper;

    @Override
    public List<InstitutionResp> listActiveInstitutions() {
        // 查询所有启用的机构（status=1）
        LambdaQueryWrapper<InstitutionDO> wrapper = Wrappers.lambdaQuery(InstitutionDO.class)
            .eq(InstitutionDO::getStatus, 1)
            .orderByAsc(InstitutionDO::getCode);
        List<InstitutionDO> list = institutionMapper.selectList(wrapper);
        return BeanUtil.copyToList(list, InstitutionResp.class);
    }

    @Override
    public List<InstitutionResp> listAll() {
        // 查询所有机构
        LambdaQueryWrapper<InstitutionDO> wrapper = Wrappers.lambdaQuery(InstitutionDO.class)
            .orderByAsc(InstitutionDO::getCode);
        List<InstitutionDO> list = institutionMapper.selectList(wrapper);
        return BeanUtil.copyToList(list, InstitutionResp.class);
    }

    @Override
    public InstitutionResp getActiveInstitution() {
        // 查询激活的机构（is_active=1）
        LambdaQueryWrapper<InstitutionDO> wrapper = Wrappers.lambdaQuery(InstitutionDO.class)
            .eq(InstitutionDO::getIsActive, 1)
            .last("LIMIT 1");
        InstitutionDO institution = institutionMapper.selectOne(wrapper);
        return institution != null ? BeanUtil.copyProperties(institution, InstitutionResp.class) : null;
    }

    @Override
    public InstitutionResp getById(Long institutionId) {
        if (institutionId == null) {
            return null;
        }
        InstitutionDO institution = institutionMapper.selectById(institutionId);
        return institution != null ? BeanUtil.copyProperties(institution, InstitutionResp.class) : null;
    }
}
