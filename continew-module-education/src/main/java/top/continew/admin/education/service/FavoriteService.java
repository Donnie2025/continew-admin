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

package top.continew.admin.education.service;

import java.util.List;

/**
 * 收藏业务接口
 *
 * @author donnie
 * @since 2025/06/10
 */
public interface FavoriteService {

    /**
     * 收藏资源
     *
     * @param studentId    学生ID
     * @param resourceType 资源类型（teacher-教师, material-教材）
     * @param resourceId   资源ID
     * @param resourceName 资源名称
     * @return 是否成功
     */
    boolean favorite(Long studentId, String resourceType, Long resourceId, String resourceName);

    /**
     * 取消收藏
     *
     * @param studentId    学生ID
     * @param resourceType 资源类型
     * @param resourceId   资源ID
     * @return 是否成功
     */
    boolean unfavorite(Long studentId, String resourceType, Long resourceId);

    /**
     * 切换收藏状态（如果已收藏则取消，未收藏则添加）
     *
     * @param studentId    学生ID
     * @param resourceType 资源类型
     * @param resourceId   资源ID
     * @param resourceName 资源名称
     * @return true-已收藏, false-已取消
     */
    boolean toggle(Long studentId, String resourceType, Long resourceId, String resourceName);

    /**
     * 检查是否已收藏
     *
     * @param studentId    学生ID
     * @param resourceType 资源类型
     * @param resourceId   资源ID
     * @return 是否已收藏
     */
    boolean isFavorited(Long studentId, String resourceType, Long resourceId);

    /**
     * 获取学生收藏的资源ID列表
     *
     * @param studentId    学生ID
     * @param resourceType 资源类型
     * @return 资源ID列表
     */
    List<Long> getFavoriteResourceIds(Long studentId, String resourceType);
}
