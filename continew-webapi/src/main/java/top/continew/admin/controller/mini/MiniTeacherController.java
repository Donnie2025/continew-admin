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

package top.continew.admin.controller.mini;

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.education.model.query.SlotQuery;
import top.continew.admin.education.model.resp.SlotResp;
import top.continew.admin.education.model.resp.TeacherDetailResp;
import top.continew.admin.education.model.resp.TeacherResp;
import top.continew.admin.education.service.BookingService;
import top.continew.admin.education.service.SlotService;
import top.continew.admin.education.service.TeacherService;
import top.continew.starter.web.model.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 小程序教师 API
 *
 * @author donnie
 * @since 2025/12/28
 */
@Tag(name = "小程序教师 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mini/teacher")
public class MiniTeacherController {

    private static final Logger log = LoggerFactory.getLogger(MiniTeacherController.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final TeacherService teacherService;
    private final SlotService slotService;
    private final BookingService bookingService;

    @SaIgnore
    @GetMapping("/active")
    @Operation(summary = "查询活跃教师列表", description = "查询所有状态为活跃的教师列表，按照排序字段升序排列，可选择按教师姓名进行模糊查询")
    public List<TeacherResp> listActiveTeachers(@RequestParam(required = false) String name) {
        return teacherService.listActiveTeachers(name);
    }

    @SaIgnore
    @GetMapping("/search")
    @Operation(summary = "搜索教师", description = "根据关键字（姓名或手机号）搜索启用状态的教师")
    public List<TeacherResp> search(@RequestParam String keyword) {
        return teacherService.searchTeachers(keyword);
    }

    @SaIgnore
    @GetMapping("/{id}")
    @Operation(summary = "查询教师详情", description = "根据教师ID查询教师详细信息")
    public TeacherDetailResp getTeacher(@PathVariable Long id) {
        return teacherService.get(id);
    }

    @SaIgnore
    @GetMapping("/{teacherId}/available-dates")
    @Operation(summary = "获取教师可约日期统计", description = "获取教师未来指定天数内的可约日期统计信息")
    public R<List<Map<String, Object>>> getAvailableDates(@PathVariable Long teacherId,
                                                          @RequestParam(defaultValue = "14") Integer days) {
        log.info("获取教师可约日期统计, 教师ID: {}, 天数: {}", teacherId, days);

        try {
            List<Map<String, Object>> result = new ArrayList<>();
            LocalDate today = LocalDate.now();

            // 生成未来指定天数的日期
            for (int i = 0; i < days; i++) {
                LocalDate date = today.plusDays(i);
                String dateStr = date.format(DATE_FORMATTER);

                // 查询该日期的可约时间段
                SlotQuery query = new SlotQuery();
                query.setTeacherId(teacherId);
                query.setStartDate(dateStr);
                query.setStatus(1); // 只查询启用状态的时间段

                List<SlotResp> slots = slotService.list(query, null);

                // 过滤掉已被预约的时间段
                List<SlotResp> availableSlots = new ArrayList<>();
                if (slots != null && !slots.isEmpty()) {
                    List<Long> slotIds = slots.stream().map(SlotResp::getId).collect(Collectors.toList());
                    Map<Long, List<String>> bookedSlotsMap = bookingService.findStudentNamesBySlotIds(slotIds);

                    // 只保留未被预约的时间段
                    for (SlotResp slot : slots) {
                        List<String> studentNames = bookedSlotsMap.get(slot.getId());
                        if (studentNames == null || studentNames.isEmpty()) {
                            availableSlots.add(slot);
                        }
                    }
                }

                Map<String, Object> dateInfo = new HashMap<>();
                dateInfo.put("date", date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                dateInfo.put("available", !availableSlots.isEmpty());
                dateInfo.put("availableSlots", availableSlots.size());

                result.add(dateInfo);
            }

            log.info("获取教师可约日期统计完成, 教师ID: {}, 返回{}天数据", teacherId, result.size());
            return R.ok(result);

        } catch (Exception e) {
            log.error("获取教师可约日期统计失败, 教师ID: {}", teacherId, e);
            return R.fail("500", "获取可约日期失败");
        }
    }

    @SaIgnore
    @PostMapping("/batch/available-dates")
    @Operation(summary = "批量获取教师可用日期统计", description = "批量获取多个教师的可用日期统计信息，优化性能")
    public R<Map<Long, Map<String, Object>>> getBatchAvailableDates(@RequestBody List<Long> teacherIds,
                                                                    @RequestParam(defaultValue = "2") Integer days) {
        log.info("批量获取教师可约日期统计, 教师数量: {}, 天数: {}", teacherIds.size(), days);

        try {
            Map<Long, Map<String, Object>> result = new HashMap<>();
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);

            // 为每个教师计算可用日期统计
            for (Long teacherId : teacherIds) {
                Map<String, Object> teacherStats = new HashMap<>();

                // 计算今天的可约数量
                int todaySlots = getAvailableSlotsForDate(teacherId, today);
                // 计算明天的可约数量
                int tomorrowSlots = getAvailableSlotsForDate(teacherId, tomorrow);

                teacherStats.put("today", todaySlots);
                teacherStats.put("tomorrow", tomorrowSlots);

                result.put(teacherId, teacherStats);
            }

            log.info("批量获取教师可约日期统计完成, 处理了{}个教师", teacherIds.size());
            return R.ok(result);

        } catch (Exception e) {
            log.error("批量获取教师可约日期统计失败, 教师IDs: {}", teacherIds, e);
            return R.fail("500", "批量获取可约日期失败");
        }
    }

    /**
     * 获取指定教师在指定日期的可约时间段数量
     */
    private int getAvailableSlotsForDate(Long teacherId, LocalDate date) {
        try {
            String dateStr = date.format(DATE_FORMATTER);

            // 查询该日期的时间段
            SlotQuery query = new SlotQuery();
            query.setTeacherId(teacherId);
            query.setStartDate(dateStr);
            query.setStatus(1); // 只查询启用状态的时间段

            List<SlotResp> slots = slotService.list(query, null);

            if (slots == null || slots.isEmpty()) {
                return 0;
            }

            // 查询已预约的时间段
            List<Long> slotIds = slots.stream().map(SlotResp::getId).collect(Collectors.toList());
            Map<Long, List<String>> bookedSlotsMap = bookingService.findStudentNamesBySlotIds(slotIds);

            // 计算可用的时间段数量
            int availableCount = 0;
            for (SlotResp slot : slots) {
                List<String> studentNames = bookedSlotsMap.get(slot.getId());
                if (studentNames == null || studentNames.isEmpty()) {
                    availableCount++;
                }
            }

            return availableCount;

        } catch (Exception e) {
            log.error("获取教师{}在日期{}的可约时间段数量失败", teacherId, date, e);
            return 0;
        }
    }

    @SaIgnore
    @GetMapping("/{teacherId}/slots")
    @Operation(summary = "获取教师指定日期的时间段", description = "获取教师在指定日期的所有可约时间段")
    public R<List<Map<String, Object>>> getSlots(@PathVariable Long teacherId, @RequestParam String date) {
        log.info("获取教师时间段, 教师ID: {}, 日期: {}", teacherId, date);

        try {
            // 将 YYYY-MM-DD 格式转换为 YYYYMMDD 格式
            String formattedDate = date.replace("-", "");

            // 查询该日期的时间段
            SlotQuery query = new SlotQuery();
            query.setTeacherId(teacherId);
            query.setStartDate(formattedDate);
            query.setStatus(1); // 只查询启用状态的时间段

            List<SlotResp> slots = slotService.list(query, null);
            List<Map<String, Object>> result = new ArrayList<>();

            if (slots != null && !slots.isEmpty()) {
                // 查询预约信息
                List<Long> slotIds = slots.stream().map(SlotResp::getId).collect(Collectors.toList());
                Map<Long, List<String>> bookedSlotsMap = bookingService.findStudentNamesBySlotIds(slotIds);

                // 转换为前端需要的格式
                for (SlotResp slot : slots) {
                    Map<String, Object> slotInfo = new HashMap<>();
                    slotInfo.put("id", slot.getId());
                    slotInfo.put("startTime", slot.getStartTime());
                    slotInfo.put("duration", slot.getDuration());

                    // 检查是否已被预约
                    List<String> studentNames = bookedSlotsMap.get(slot.getId());
                    boolean isBooked = studentNames != null && !studentNames.isEmpty();

                    slotInfo.put("status", isBooked ? "BOOKED" : "AVAILABLE");
                    slotInfo.put("available", !isBooked);

                    if (isBooked) {
                        slotInfo.put("studentNames", studentNames);
                    }

                    result.add(slotInfo);
                }
            }

            log.info("获取教师时间段完成, 教师ID: {}, 日期: {}, 返回{}个时间段", teacherId, date, result.size());
            return R.ok(result);

        } catch (Exception e) {
            log.error("获取教师时间段失败, 教师ID: {}, 日期: {}", teacherId, date, e);
            return R.fail("500", "获取时间段失败");
        }
    }
}
