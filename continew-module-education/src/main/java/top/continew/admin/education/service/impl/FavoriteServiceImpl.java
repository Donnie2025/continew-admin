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
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.education.mapper.FavoriteMapper;
import top.continew.admin.education.model.entity.FavoriteDO;
import top.continew.admin.education.service.FavoriteService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 收藏业务实现
 *
 * @author donnie
 * @since 2025/06/10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean favorite(Long studentId, String resourceType, Long resourceId, String resourceName) {
        // 检查是否已存在
        FavoriteDO existing = favoriteMapper.selectByStudentAndResource(studentId, resourceType, resourceId);

        if (existing != null) {
            // 如果已存在且状态为禁用，则启用
            if (existing.getStatus() == 0) {
                existing.setStatus(1);
                existing.setUpdateTime(LocalDateTime.now());
                return favoriteMapper.updateById(existing) > 0;
            }
            // 已经收藏，返回true
            return true;
        }

        // 创建新收藏记录
        FavoriteDO favorite = new FavoriteDO();
        favorite.setStudentId(studentId);
        favorite.setResourceType(resourceType);
        favorite.setResourceId(resourceId);
        favorite.setResourceName(resourceName);
        favorite.setStatus(1);
        favorite.setCreateTime(LocalDateTime.now());
        favorite.setUpdateTime(LocalDateTime.now());

        return favoriteMapper.insert(favorite) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unfavorite(Long studentId, String resourceType, Long resourceId) {
        FavoriteDO existing = favoriteMapper.selectByStudentAndResource(studentId, resourceType, resourceId);

        if (existing == null || existing.getStatus() == 0) {
            return true;
        }

        // 软删除：将状态设置为0
        existing.setStatus(0);
        existing.setUpdateTime(LocalDateTime.now());
        return favoriteMapper.updateById(existing) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggle(Long studentId, String resourceType, Long resourceId, String resourceName) {
        FavoriteDO existing = favoriteMapper.selectByStudentAndResource(studentId, resourceType, resourceId);

        if (existing != null && existing.getStatus() == 1) {
            // 已收藏，取消收藏
            unfavorite(studentId, resourceType, resourceId);
            return false;
        } else {
            // 未收藏，添加收藏
            favorite(studentId, resourceType, resourceId, resourceName);
            return true;
        }
    }

    @Override
    public boolean isFavorited(Long studentId, String resourceType, Long resourceId) {
        FavoriteDO favorite = favoriteMapper.selectByStudentAndResource(studentId, resourceType, resourceId);
        return favorite != null && favorite.getStatus() == 1;
    }

    @Override
    public List<Long> getFavoriteResourceIds(Long studentId, String resourceType) {
        return favoriteMapper.selectResourceIdsByStudent(studentId, resourceType);
    }
}
