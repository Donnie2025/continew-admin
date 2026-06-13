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

package top.continew.admin.education.mapper;

import org.apache.ibatis.annotations.Param;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.education.model.entity.FavoriteDO;

import java.util.List;

/**
 * 收藏 Mapper
 *
 * @author donnie
 * @since 2025/06/10
 */
public interface FavoriteMapper extends BaseMapper<FavoriteDO> {

    /**
     * 查询学生是否已收藏某资源
     *
     * @param studentId    学生ID
     * @param resourceType 资源类型
     * @param resourceId   资源ID
     * @return 收藏记录
     */
    FavoriteDO selectByStudentAndResource(@Param("studentId") Long studentId,
                                          @Param("resourceType") String resourceType,
                                          @Param("resourceId") Long resourceId);

    /**
     * 查询学生收藏的资源ID列表
     *
     * @param studentId    学生ID
     * @param resourceType 资源类型
     * @return 资源ID列表
     */
    List<Long> selectResourceIdsByStudent(@Param("studentId") Long studentId,
                                          @Param("resourceType") String resourceType);
}
