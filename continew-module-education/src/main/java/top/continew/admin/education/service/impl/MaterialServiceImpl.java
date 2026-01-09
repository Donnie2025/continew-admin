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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.MaterialMapper;
import top.continew.admin.education.model.entity.MaterialDO;
import top.continew.admin.education.model.query.MaterialQuery;
import top.continew.admin.education.model.req.MaterialReq;
import top.continew.admin.education.model.req.MaterialSortReq;
import top.continew.admin.education.model.resp.MaterialDetailResp;
import top.continew.admin.education.model.resp.MaterialResp;
import top.continew.admin.education.model.resp.MaterialStatisticsResp;
import top.continew.admin.education.service.MaterialService;
import cn.dev33.satoken.stp.StpUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 教材业务实现
 *
 * @author don
 * @since 2025/12/29 21:22
 */
@Service
@RequiredArgsConstructor
public class MaterialServiceImpl extends BaseServiceImpl<MaterialMapper, MaterialDO, MaterialResp, MaterialDetailResp, MaterialQuery, MaterialReq> implements MaterialService {

    @Override
    public List<MaterialResp> listByCategory(String category) {
        LambdaQueryWrapper<MaterialDO> queryWrapper = Wrappers.<MaterialDO>lambdaQuery();
        queryWrapper.eq(MaterialDO::getCategory, category)
            .eq(MaterialDO::getStatus, true)
            .orderByAsc(MaterialDO::getSort);

        List<MaterialDO> materialList = baseMapper.selectList(queryWrapper);
        return materialList.stream().map(this::toResp).collect(Collectors.toList());
    }

    @Override
    public MaterialStatisticsResp getStatistics() {
        MaterialStatisticsResp statistics = new MaterialStatisticsResp();

        // 总数统计
        statistics.setTotalCount(baseMapper.selectCount(null));

        // 启用/禁用统计
        LambdaQueryWrapper<MaterialDO> enabledWrapper = Wrappers.<MaterialDO>lambdaQuery();
        enabledWrapper.eq(MaterialDO::getStatus, true);
        statistics.setEnabledCount(baseMapper.selectCount(enabledWrapper));

        LambdaQueryWrapper<MaterialDO> disabledWrapper = Wrappers.<MaterialDO>lambdaQuery();
        disabledWrapper.eq(MaterialDO::getStatus, false);
        statistics.setDisabledCount(baseMapper.selectCount(disabledWrapper));

        // 前端展示统计
        LambdaQueryWrapper<MaterialDO> displayWrapper = Wrappers.<MaterialDO>lambdaQuery();
        displayWrapper.eq(MaterialDO::getIsShow, true);
        statistics.setDisplayCount(baseMapper.selectCount(displayWrapper));

        // 按分类统计
        List<MaterialDO> allMaterials = baseMapper.selectList(null);
        Map<String, Long> categoryStats = allMaterials.stream()
            .collect(Collectors.groupingBy(MaterialDO::getCategory, Collectors.counting()));
        statistics.setCategoryStats(categoryStats);

        // 按级别统计
        Map<String, Long> levelStats = allMaterials.stream()
            .collect(Collectors.groupingBy(MaterialDO::getLevel, Collectors.counting()));
        statistics.setLevelStats(levelStats);

        // 最近7天创建的教材数
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LambdaQueryWrapper<MaterialDO> recentWrapper = Wrappers.<MaterialDO>lambdaQuery();
        recentWrapper.ge(MaterialDO::getCreateTime, sevenDaysAgo);
        statistics.setRecentCount(baseMapper.selectCount(recentWrapper));

        return statistics;
    }

    @Override
    public void batchUpdateSort(List<MaterialSortReq> sortList) {
        for (MaterialSortReq sortReq : sortList) {
            MaterialDO material = new MaterialDO();
            material.setId(sortReq.getId());
            material.setSort(sortReq.getSort());
            baseMapper.updateById(material);
        }
    }

    @Override
    protected void afterCreate(MaterialReq req, MaterialDO entity) {
        boolean needUpdate = false;

        // 如果前端没有传递status，设置默认值为启用状态
        if (req.getStatus() == null) {
            entity.setStatus(true); // true表示启用
            needUpdate = true;
        }

        // 如果前端没有传递创建人，设置为当前登录用户
        if (req.getCreateUser() == null) {
            try {
                Long currentUserId = StpUtil.getLoginIdAsLong();
                entity.setCreateUser(currentUserId);
                needUpdate = true;
            } catch (Exception e) {
                // 如果获取登录用户失败，使用默认值
                entity.setCreateUser(1L);
                needUpdate = true;
            }
        }

        // 如果前端没有传递创建时间，设置为当前时间
        if (req.getCreateTime() == null) {
            entity.setCreateTime(LocalDateTime.now());
            needUpdate = true;
        }

        // 如果有字段需要更新，执行更新操作
        if (needUpdate) {
            baseMapper.updateById(entity);
        }

        super.afterCreate(req, entity);
    }

    /**
     * 转换为响应对象
     */
    private MaterialResp toResp(MaterialDO materialDO) {
        MaterialResp resp = new MaterialResp();
        resp.setId(materialDO.getId());
        resp.setCode(materialDO.getCode());
        resp.setName(materialDO.getName());
        resp.setLevel(materialDO.getLevel());
        resp.setCategory(materialDO.getCategory());
        resp.setCoverImg(materialDO.getCoverImg());
        resp.setDescription(materialDO.getDescription());
        resp.setIsShow(materialDO.getIsShow());
        resp.setSort(materialDO.getSort());
        resp.setStatus(materialDO.getStatus() ? 1 : 0);
        resp.setUpdateUser(materialDO.getUpdateUser());
        resp.setUpdateTime(materialDO.getUpdateTime());
        return resp;
    }
}