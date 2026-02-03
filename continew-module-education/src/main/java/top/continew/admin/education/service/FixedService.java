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

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.education.model.query.FixedQuery;
import top.continew.admin.education.model.req.FixedReq;
import top.continew.admin.education.model.resp.FixedResp;

import java.util.List;

/**
 * 固定课业务接口
 *
 * @author Charles7c
 * @since 2024/12/28 18:30
 */
public interface FixedService extends BaseService<FixedResp, FixedResp, FixedQuery, FixedReq> {

    /**
     * 根据教师ID查询固定课列表
     *
     * @param teacherId 教师ID
     * @return 固定课列表
     */
    List<FixedResp> listByTeacherId(Long teacherId);

    /**
     * 根据星期几查询固定课列表
     *
     * @param weekDay 星期几
     * @return 固定课列表
     */
    List<FixedResp> listByWeekDay(Integer weekDay);

    /**
     * 检查固定课时间冲突
     *
     * @param teacherId 教师ID
     * @param weekDay   星期几
     * @param startTime 开始时间
     * @param excludeId 排除的固定课ID（用于更新时排除自己）
     * @return 是否冲突
     */
    boolean isTimeConflict(Long teacherId, Integer weekDay, String startTime, Long excludeId);

    /**
     * 创建固定课（用于小程序端）
     *
     * @param req 固定课创建请求
     * @return 固定课ID
     */
    Long createFixed(FixedReq req);

    /**
     * 删除固定课（用于小程序端）
     *
     * @param id 固定课ID
     */
    void deleteFixed(Long id);

    /**
     * 学生查询所有固定课（包含预约状态）
     *
     * @param studentId 学生ID
     * @return 固定课列表（包含是否已预约状态）
     */
    List<FixedResp> listForStudent(Long studentId);

    /**
     * 批量检查时间冲突
     *
     * @param teacherId  教师ID
     * @param weekDays   星期列表
     * @param startTimes 开始时间列表
     * @return 是否存在冲突
     */
    boolean isBatchTimeConflict(Long teacherId, List<Integer> weekDays, List<String> startTimes);

    /**
     * 批量创建固定课
     *
     * @param req 批量创建请求
     * @return 创建的固定课数量
     */
    int batchCreate(top.continew.admin.education.model.req.FixedBatchReq req);
}
