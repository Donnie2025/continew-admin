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
import java.util.Map;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.education.model.query.SlotQuery;
import top.continew.admin.education.model.req.BatchSlotReq;
import top.continew.admin.education.model.req.SlotReq;
import top.continew.admin.education.model.resp.SlotDetailResp;
import top.continew.admin.education.model.resp.SlotResp;

/**
 * 课程时间服务接口
 *
 * @author don
 * @since 2025/04/25 23:24
 */
public interface SlotService extends BaseService<SlotResp, SlotDetailResp, SlotQuery, SlotReq> {
    /**
     * 批量创建课程时间
     *
     * @param req 批量课程时间请求
     * @return 课程时间列表
     */
    List<SlotResp> batchCreateSlot(BatchSlotReq req);

    /**
     * 软删除时间段（更新状态为禁用）
     *
     * @param id 时间段ID
     */
    void disableById(Long id);

    /**
     * 批量删除未预约的时间段
     * 
     * @param slotIds 时间段ID列表
     * @return 删除结果统计 (success: 成功数量, failed: 失败数量)
     */
    Map<String, Integer> batchDeleteUnbookedSlots(List<Long> slotIds);

    /**
     * 获取教师本周统计数据
     *
     * @param teacherId 教师ID
     * @return 统计数据
     */
    Map<String, Object> getWeeklyStats(Long teacherId);
}