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
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.SlotMapper;
import top.continew.admin.education.model.entity.SlotDO;
import top.continew.admin.education.model.query.SlotQuery;
import top.continew.admin.education.model.req.BatchSlotReq;
import top.continew.admin.education.model.req.SlotReq;
import top.continew.admin.education.model.resp.SlotDetailResp;
import top.continew.admin.education.model.resp.SlotResp;
import top.continew.starter.extension.crud.model.query.SortQuery;
import top.continew.admin.education.service.SlotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 课程管理业务实现
 *
 * @author don
 * @since 2025/04/25 23:24
 */
@Service
@RequiredArgsConstructor
public class SlotServiceImpl extends BaseServiceImpl<SlotMapper, SlotDO, SlotResp, SlotDetailResp, SlotQuery, SlotReq> implements SlotService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private static final Logger log = LoggerFactory.getLogger(SlotServiceImpl.class);

    /**
     * 批量创建课程时间
     *
     * @param req 批量课程时间请求
     * @return 课程时间列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<SlotResp> batchCreateSlot(BatchSlotReq req) {
        // 创建结果列表
        List<SlotResp> result = new ArrayList<>();
        List<Long> createdIds = new ArrayList<>();

        log.info("开始批量创建课程时间，请求参数: {}", req);

        // 检查日期列表是否为空
        if (req.getDates() == null || req.getDates().isEmpty()) {
            log.error("日期列表为空，无法创建课程时间");
            return result;
        }

        log.info("处理的日期列表: {}", req.getDates());
        log.info("处理的时间列表: {}", req.getTimes());

        int skippedCount = 0; // 统计跳过的记录数

        // 遍历所有日期
        for (String dateStr : req.getDates()) {
            // 解析日期并计算是周几
            java.time.LocalDate localDate = java.time.LocalDate.parse(dateStr);
            int dayOfWeek = localDate.getDayOfWeek().getValue(); // 1=周一, 7=周日

            // 将YYYY-MM-DD格式转换为YYYYMMDD格式
            String formattedDate = dateStr.replace("-", "");
            log.debug("处理日期: {} -> {}, 星期: {}", dateStr, formattedDate, dayOfWeek);

            // 遍历所有时间段
            for (String time : req.getTimes()) {
                // 先检查是否已存在相同老师、日期、时间且状态为1的课时记录
                SlotDO existingSlot = this.baseMapper.checkExistingSlot(req.getTeacherId(), formattedDate, time);

                if (existingSlot != null) {
                    // 已存在记录，跳过创建
                    log.info("跳过创建课时：老师[{}]在日期[{}]的时间[{}]已存在记录，ID为[{}]", req
                        .getTeacherId(), formattedDate, time, existingSlot.getId());
                    skippedCount++;
                    continue;
                }

                // 创建课程时间请求
                SlotReq slotReq = new SlotReq();
                slotReq.setTeacherId(req.getTeacherId());
                slotReq.setTeacherName(req.getTeacherName());
                slotReq.setStartDate(formattedDate);
                slotReq.setStartTime(time);
                slotReq.setIsOnline(req.getOnline());
                slotReq.setWeekday(dayOfWeek); // 设置星期几

                // 设置可选字段
                if (req.getDuration() != null) {
                    slotReq.setDuration(req.getDuration());
                } else {
                    slotReq.setDuration(25);
                }

                if (req.getInstitutionId() != null) {
                    slotReq.setInstitutionId(req.getInstitutionId());
                }

                // 调用单个创建接口
                log.debug("创建单个课程时间，参数: {}", slotReq);
                Long id = super.create(slotReq);
                log.debug("创建单个课程时间成功，ID: {}", id);
                createdIds.add(id);
            }
        }

        // 查询创建的所有课程时间
        if (!createdIds.isEmpty()) {
            SlotQuery query = new SlotQuery();
            query.setIds(createdIds);
            result = this.list(query, new SortQuery());
            log.info("批量创建课程时间完成，共创建{}个时间段，跳过{}个重复记录", result.size(), skippedCount);
        } else {
            if (skippedCount > 0) {
                log.warn("批量创建课程时间完成，未创建任何记录，全部{}个记录都是重复的", skippedCount);
            } else {
                log.warn("批量创建课程时间失败，未创建任何记录");
            }
        }

        return result;
    }

    /**
     * 重写创建方法，增加重复检查逻辑
     *
     * @param req 创建请求
     * @return 创建的ID
     */
    @Override
    public Long create(SlotReq req) {
        // 先检查是否已存在相同老师、日期、时间且状态为1的课时记录
        SlotDO existingSlot = this.baseMapper.checkExistingSlot(req.getTeacherId(), req.getStartDate(), req
            .getStartTime());

        if (existingSlot != null) {
            // 已存在记录，返回已存在记录的ID而不是创建新记录
            log.info("跳过创建课时：老师[{}]在日期[{}]的时间[{}]已存在记录，ID为[{}]", req.getTeacherId(), req.getStartDate(), req
                .getStartTime(), existingSlot.getId());
            return existingSlot.getId();
        }

        // 不存在记录，调用父类方法创建新记录
        return super.create(req);
    }
}