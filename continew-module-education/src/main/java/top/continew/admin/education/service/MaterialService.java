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
import top.continew.admin.education.model.resp.MaterialDetailResp;
import top.continew.admin.education.model.resp.MaterialResp;
import top.continew.admin.education.model.resp.MaterialStatisticsResp;
import top.continew.starter.extension.crud.service.BaseService;

import java.util.List;

/**
 * 教材业务接口
 *
 * @author don
 * @since 2025/12/29 21:22
 */
public interface MaterialService extends BaseService<MaterialResp, MaterialDetailResp, MaterialQuery, MaterialReq> {

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
}