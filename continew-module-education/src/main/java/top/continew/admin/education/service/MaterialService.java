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

import top.continew.admin.education.model.query.MaterialQuery;
import top.continew.admin.education.model.req.MaterialReq;
import top.continew.admin.education.model.req.MaterialSortReq;
import top.continew.admin.education.model.req.SyncCloudFoldersReq;
import top.continew.admin.education.model.req.MaterialLessonImportReq;
import top.continew.admin.education.model.resp.MaterialDetailResp;
import top.continew.admin.education.model.resp.MaterialResp;
import top.continew.admin.education.model.resp.MaterialStatisticsResp;
import top.continew.admin.education.model.resp.MaterialImportResp;
import top.continew.admin.education.model.resp.MaterialLessonWithCompletionResp;
import top.continew.starter.extension.crud.service.BaseService;

import java.util.List;

/**
 * 教材业务接口（包含课节相关功能）
 *
 * @author don
 * @since 2025/12/29 21:22
 */
public interface MaterialService extends BaseService<MaterialResp, MaterialDetailResp, MaterialQuery, MaterialReq> {

    /**
     * 获取全量教材列表（不分页）
     *
     * @return 全量教材列表
     */
    List<MaterialResp> listAll();

    /**
     * 根据分类获取教材列表
     *
     * @param category 分类
     * @return 教材列表
     */
    List<MaterialResp> listByCategory(String category);

    /**
     * 获取教材统计信息
     *
     * @return 统计信息
     */
    MaterialStatisticsResp getStatistics();

    /**
     * 批量更新排序
     *
     * @param sortList 排序列表
     */
    void batchUpdateSort(List<MaterialSortReq> sortList);

    /**
     * 同步云盘文件夹列表到教材表
     *
     * @param req 同步请求
     * @return 新增节点数量
     */
    int syncCloudFolders(SyncCloudFoldersReq req);

    /**
     * 从ClassIn云盘同步数据到数据库（更新cloudName字段）
     *
     * @param ids 教材ID列表
     * @return 更新节点数量
     */
    int syncCloudData(List<Long> ids);

    /**
     * 同步飞书文件夹token
     *
     * @param id        节点ID
     * @param recursive 是否递归同步子节点
     * @return 同步结果统计
     */
    java.util.Map<String, Object> syncFeishu(Long id, boolean recursive);

    // ========== 课节相关方法（原MaterialLessonService） ==========

    /**
     * 从飞书链接导入课节
     *
     * @param req 导入请求参数
     * @return 导入结果
     */
    MaterialImportResp importLessonsFromFeishu(MaterialLessonImportReq req);

    /**
     * 获取教材的课程列表（包含完成状态）
     *
     * @param materialId 教材ID
     * @return 课程列表（包含完成状态）
     */
    List<MaterialLessonWithCompletionResp> listLessonsWithCompletionStatus(Long materialId);
}