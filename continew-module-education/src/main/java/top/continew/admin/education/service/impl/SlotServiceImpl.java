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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.SlotMapper;
import top.continew.admin.education.model.entity.SlotDO;
import top.continew.admin.education.model.query.SlotQuery;
import top.continew.admin.education.model.req.BatchSlotReq;
import top.continew.admin.education.model.req.SlotReq;
import top.continew.admin.education.model.resp.SlotDetailResp;
import top.continew.admin.education.model.resp.SlotResp;
import top.continew.starter.extension.crud.model.query.SortQuery;
import top.continew.admin.education.model.entity.BookingDO;
import top.continew.admin.education.service.BookingService;
import top.continew.admin.education.service.SlotService;
import top.continew.admin.education.service.TeacherService;
import top.continew.admin.education.model.entity.TeacherDO;
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

    private final BookingService bookingService;
    private final TeacherService teacherService;

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

                // 设置学生数量
                if (req.getStudentCount() != null) {
                    slotReq.setStudentCount(req.getStudentCount());
                } else {
                    slotReq.setStudentCount(1); // 默认为1
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

    @Override
    public void disableById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("时间段ID不能为空");
        }

        // 构造更新对象，只更新status字段
        SlotDO updateSlot = new SlotDO();
        updateSlot.setId(id);
        updateSlot.setStatus(0); // 设置为禁用状态（软删除）

        int updateResult = baseMapper.updateById(updateSlot);
        if (updateResult == 0) {
            throw new RuntimeException("软删除时间段失败，可能记录不存在: ID=" + id);
        }

        log.info("成功软删除时间段: ID={}", id);
    }

    @Override
    public Map<String, Integer> batchDeleteUnbookedSlots(List<Long> slotIds) {
        Map<String, Integer> result = new HashMap<>();
        int successCount = 0;
        int failedCount = 0;

        log.info("开始批量删除未预约时间段, 数量: {}", slotIds.size());

        for (Long slotId : slotIds) {
            try {
                // 1. 检查时间段是否存在
                SlotDetailResp slot = this.get(slotId);
                if (slot == null) {
                    log.warn("时间段不存在，跳过删除: ID={}", slotId);
                    failedCount++;
                    continue;
                }

                // 2. 检查是否有预约记录
                List<BookingDO> bookings = bookingService.getBySlotId(slotId);
                if (bookings != null && !bookings.isEmpty()) {
                    log.warn("时间段已有预约，跳过删除: ID={}, 预约数量: {}", slotId, bookings.size());
                    failedCount++;
                    continue;
                }

                // 3. 执行软删除
                this.disableById(slotId);
                successCount++;
                log.debug("删除时间段成功: ID={}", slotId);

            } catch (Exception e) {
                log.error("删除时间段失败: ID={}", slotId, e);
                failedCount++;
            }
        }

        result.put("success", successCount);
        result.put("failed", failedCount);

        log.info("批量删除完成: 成功={}, 失败={}", successCount, failedCount);
        return result;
    }

    @Override
    public Map<String, Object> getWeeklyStats(Long teacherId) {
        Map<String, Object> stats = new HashMap<>();

        // 计算本周的开始和结束日期 (周一到周日)
        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.LocalDate startOfWeek = now.with(java.time.DayOfWeek.MONDAY);
        java.time.LocalDate endOfWeek = now.with(java.time.DayOfWeek.SUNDAY);

        String startDateStr = startOfWeek.format(DATE_FORMATTER);
        String endDateStr = endOfWeek.format(DATE_FORMATTER);

        log.info("获取教师本周统计数据: teacherId={}, 时间范围: {} - {}", teacherId, startDateStr, endDateStr);

        try {
            // 1. 获取教师信息
            TeacherDO teacher = teacherService.getById(teacherId);
            if (teacher == null) {
                log.warn("教师不存在: teacherId={}", teacherId);
                stats.put("classesThisWeek", 0);
                stats.put("bookedSlots", 0);
                stats.put("availableSlots", 0);
                stats.put("showSalary", false);
                stats.put("estimatedEarning", 0);
                return stats;
            }

            // 2. 查询本周所有开放的时间段 (status=1)
            SlotQuery query = new SlotQuery();
            query.setTeacherId(teacherId);
            query.setDateRange(startDateStr, endDateStr);
            query.setStatus(1); // 只查询启用状态的时间段

            List<SlotResp> allSlots = this.list(query, null);
            int totalClasses = allSlots != null ? allSlots.size() : 0;

            // 3. 查询有预约的时间段数量
            int bookedSlots = 0;
            if (allSlots != null && !allSlots.isEmpty()) {
                // 提取所有时间段ID
                List<Long> slotIds = allSlots.stream()
                    .map(SlotResp::getId)
                    .collect(java.util.stream.Collectors.toList());

                // 查询有预约记录的时间段
                Map<Long, List<String>> studentNamesMap = bookingService.findStudentNamesBySlotIds(slotIds);

                // 计算有预约的时间段数量
                bookedSlots = (int)studentNamesMap.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                    .count();
            }

            // 4. 计算可用的时间段数量 (总数 - 已预约数)
            int availableSlots = totalClasses - bookedSlots;

            // 5. 检查是否展示工资
            boolean showSalary = teacher.getShowSalary() != null && teacher.getShowSalary() == 1;

            // 6. 计算预估收入 (单价 * 已预约节数)
            int estimatedEarning = 0;
            if (showSalary && teacher.getRate() != null) {
                estimatedEarning = teacher.getRate() * bookedSlots;
            }

            stats.put("classesThisWeek", totalClasses);
            stats.put("bookedSlots", bookedSlots);
            stats.put("availableSlots", availableSlots);
            stats.put("showSalary", showSalary);
            stats.put("estimatedEarning", estimatedEarning);

            log.info("教师本周统计结果: 总课时={}, 已预约={}, 可预约={}, 展示工资={}, 预估收入={}", totalClasses, bookedSlots, availableSlots, showSalary, estimatedEarning);

        } catch (Exception e) {
            log.error("获取教师本周统计数据失败: teacherId={}", teacherId, e);
            // 返回默认值
            stats.put("classesThisWeek", 0);
            stats.put("bookedSlots", 0);
            stats.put("availableSlots", 0);
            stats.put("showSalary", false);
            stats.put("estimatedEarning", 0);
        }

        return stats;
    }
}