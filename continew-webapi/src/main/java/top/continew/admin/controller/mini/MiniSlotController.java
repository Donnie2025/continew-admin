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

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.common.satoken.StpMiniUtil;
import top.continew.admin.education.model.entity.BookingDO;
import top.continew.admin.education.model.query.SlotQuery;
import top.continew.admin.education.model.req.BatchSlotReq;
import top.continew.admin.education.model.resp.SlotDetailResp;
import top.continew.admin.education.model.resp.SlotResp;
import top.continew.admin.education.service.BookingService;
import top.continew.admin.education.service.SlotService;
import top.continew.starter.web.model.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 小程序时间段管理 API
 *
 * @author donnie
 * @since 2025/01/15
 */
@Tag(name = "小程序时间段管理 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mini/slot")
public class MiniSlotController {

    private static final Logger log = LoggerFactory.getLogger(MiniSlotController.class);

    private final SlotService slotService;
    private final BookingService bookingService;

    /**
     * 查询当前登录教师指定日期的时间段（token认证，无需传teacherId）
     *
     * @param date 日期（格式：YYYYMMDD）
     * @return 时间段列表
     */
    @GetMapping("/my-schedule")
    @Operation(summary = "查询我的时间段", description = "根据token获取当前教师指定日期的所有时间段，包含预约信息")
    public R<List<SlotResp>> getMySchedule(@RequestParam("date") String date) {
        try {
            String authHeader = SaManager.getSaTokenContext().getRequest().getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return R.fail("401", "请先登录");
            }
            String token = authHeader.substring(7);
            Object loginId = StpMiniUtil.getStpLogic().getLoginIdByToken(token);
            if (loginId == null) {
                return R.fail("401", "登录已过期，请重新登录");
            }
            Long teacherId = Long.valueOf(loginId.toString());
            log.info("查询我的时间段, 教师ID: {}, 日期: {}", teacherId, date);

            SlotQuery query = new SlotQuery();
            query.setTeacherId(teacherId);
            query.setStartDate(date);
            query.setStatus(1);

            List<SlotResp> result = slotService.list(query, null);
            if (result == null)
                result = new ArrayList<>();

            if (!result.isEmpty()) {
                List<Long> slotIds = result.stream().map(SlotResp::getId).collect(Collectors.toList());
                Map<Long, List<BookingDO>> detailedBookingsMap = bookingService.findDetailedBookingsBySlotIds(slotIds);
                Map<Long, List<String>> studentNamesMap = bookingService.findStudentNamesBySlotIds(slotIds);
                result.forEach(slot -> {
                    List<String> names = studentNamesMap.get(slot.getId());
                    if (names != null && !names.isEmpty())
                        slot.setStudentNameList(names);
                    List<BookingDO> bookings = detailedBookingsMap.get(slot.getId());
                    if (bookings != null && !bookings.isEmpty()) {
                        slot.setBookingDetails(bookings.stream()
                            .map(this::convertToBookingDetailInfo)
                            .collect(Collectors.toList()));
                    }
                });
            }
            result.sort((a, b) -> {
                if (a.getStartTime() == null)
                    return 1;
                if (b.getStartTime() == null)
                    return -1;
                return a.getStartTime().compareTo(b.getStartTime());
            });
            return R.ok(result);
        } catch (Exception e) {
            log.error("查询我的时间段失败", e);
            return R.fail("500", e.getMessage());
        }
    }

    /**
     * 根据日期和教师ID查询时间段
     *
     * @param teacherId 教师ID
     * @param date      日期（格式：YYYYMMDD）
     * @return 时间段列表
     */
    @SaIgnore
    @GetMapping("/by-date")
    @Operation(summary = "根据日期和教师ID查询时间段", description = "查询指定日期该教师的所有时间段，包含预约信息")
    public R<List<SlotResp>> listSlotsByDate(@RequestParam("teacherId") Long teacherId,
                                             @RequestParam("date") String date) {
        log.info("查询教师时间段, 教师ID: {}, 日期: {}", teacherId, date);

        try {
            // 构建查询条件
            SlotQuery query = new SlotQuery();
            query.setTeacherId(teacherId);
            query.setStartDate(date);
            query.setStatus(1); // 只查询启用状态的时间段

            // 查询时间段列表
            List<SlotResp> result = slotService.list(query, null);

            // 如果查询结果为空，返回空列表而不是错误
            if (result == null) {
                log.info("未找到时间段数据，返回空列表");
                return R.ok(new ArrayList<>());
            }

            log.info("查询到{}条时间段记录", result.size());

            // 如果查询结果不为空，关联查询预约信息
            if (!result.isEmpty()) {
                // 提取所有时间段ID
                List<Long> slotIds = result.stream().map(SlotResp::getId).collect(Collectors.toList());

                // 查询所有相关的详细预约信息
                Map<Long, List<BookingDO>> detailedBookingsMap = bookingService.findDetailedBookingsBySlotIds(slotIds);

                // 查询学生姓名（保持向后兼容）
                Map<Long, List<String>> studentNamesMap = bookingService.findStudentNamesBySlotIds(slotIds);

                // 设置学生姓名和详细预约信息
                result.forEach(slot -> {
                    List<String> studentNames = studentNamesMap.get(slot.getId());
                    if (studentNames != null && !studentNames.isEmpty()) {
                        // 设置学生名字列表（向后兼容）
                        slot.setStudentNameList(studentNames);
                    }

                    // 设置详细预约信息
                    List<BookingDO> bookings = detailedBookingsMap.get(slot.getId());
                    if (bookings != null && !bookings.isEmpty()) {
                        List<SlotResp.BookingDetailInfo> bookingDetails = bookings.stream()
                            .map(booking -> convertToBookingDetailInfo(booking))
                            .collect(Collectors.toList());
                        slot.setBookingDetails(bookingDetails);
                    }
                });

                log.info("已关联查询预约信息，共{}条时间段，{}条有预约", result.size(), detailedBookingsMap.size());
            }

            // 按开始时间排序
            if (result != null) {
                result.sort((a, b) -> {
                    if (a.getStartTime() == null)
                        return 1;
                    if (b.getStartTime() == null)
                        return -1;
                    return a.getStartTime().compareTo(b.getStartTime());
                });
            }

            log.info("查询完成，返回{}条时间段", result != null ? result.size() : 0);
            return R.ok(result);

        } catch (Exception e) {
            log.error("查询教师时间段失败, 教师ID: {}, 日期: {}", teacherId, date, e);
            return R.fail("500", "查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据日期范围和教师ID查询可用时间段
     *
     * @param teacherId 教师ID
     * @param startDate 开始日期（格式：YYYYMMDD）
     * @param endDate   结束日期（格式：YYYYMMDD）
     * @return 时间段列表
     */
    @SaIgnore
    @GetMapping("/available")
    @Operation(summary = "根据日期范围和教师ID查询可用时间段", description = "查询指定日期范围内该教师的所有可用时间段")
    public R<List<SlotResp>> listAvailableSlots(@RequestParam("teacherId") Long teacherId,
                                                @RequestParam("startDate") String startDate,
                                                @RequestParam("endDate") String endDate) {
        log.info("查询可用时间段, 教师ID: {}, 开始日期: {}, 结束日期: {}", teacherId, startDate, endDate);

        try {
            // 构建查询条件
            SlotQuery query = new SlotQuery();
            query.setTeacherId(teacherId);
            query.setDateRange(startDate, endDate);
            query.setStatus(1); // 状态为1表示可用

            List<SlotResp> result = slotService.list(query, null);

            // 如果查询结果不为空，关联查询预约信息
            if (result != null && !result.isEmpty()) {
                // 提取所有时间段ID
                List<Long> slotIds = result.stream().map(SlotResp::getId).collect(Collectors.toList());

                // 查询所有相关的预约信息
                Map<Long, List<String>> studentNamesMap = bookingService.findStudentNamesBySlotIds(slotIds);

                // 设置学生姓名
                result.forEach(slot -> {
                    List<String> studentNames = studentNamesMap.get(slot.getId());
                    if (studentNames != null && !studentNames.isEmpty()) {
                        // 设置学生名字列表
                        slot.setStudentNameList(studentNames);
                    }
                });

                log.info("已关联查询预约信息，共{}条时间段，{}条有预约", result.size(), studentNamesMap.size());
            }

            return R.ok(result);

        } catch (Exception e) {
            log.error("查询可用时间段失败, 教师ID: {}, 开始日期: {}, 结束日期: {}", teacherId, startDate, endDate, e);
            return R.fail("500", "查询失败: " + e.getMessage());
        }
    }

    /**
     * 查询时间段详情
     *
     * @param id 时间段ID
     * @return 时间段详情
     */
    @SaIgnore
    @GetMapping("/{id}")
    @Operation(summary = "查询时间段详情", description = "根据时间段ID查询详细信息")
    public R<SlotResp> getSlot(@PathVariable Long id) {
        log.info("查询时间段详情, ID: {}", id);

        try {
            // 构建查询条件
            SlotQuery query = new SlotQuery();
            query.setIds(List.of(id));

            List<SlotResp> result = slotService.list(query, null);

            if (result == null || result.isEmpty()) {
                return R.fail("404", "时间段不存在");
            }

            SlotResp slot = result.get(0);

            // 查询预约信息
            Map<Long, List<String>> studentNamesMap = bookingService.findStudentNamesBySlotIds(List.of(id));
            List<String> studentNames = studentNamesMap.get(id);
            if (studentNames != null && !studentNames.isEmpty()) {
                slot.setStudentNameList(studentNames);
            }

            log.info("查询时间段详情完成, ID: {}", id);
            return R.ok(slot);

        } catch (Exception e) {
            log.error("查询时间段详情失败, ID: {}", id, e);
            return R.fail("500", "查询失败: " + e.getMessage());
        }
    }

    /**
     * 批量创建时间段
     *
     * @param batchSlotReq 批量时间段请求
     * @return 创建结果
     */
    @PostMapping("/batch")
    @Operation(summary = "批量创建时间段", description = "批量创建多个日期和时间的时间段，teacherId自动从 token 获取")
    public R<List<SlotResp>> batchCreateSlot(@RequestBody BatchSlotReq batchSlotReq) {
        try {
            String authHeader = SaManager.getSaTokenContext().getRequest().getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return R.fail("401", "请先登录");
            }
            String token = authHeader.substring(7);
            Object loginId = StpMiniUtil.getStpLogic().getLoginIdByToken(token);
            if (loginId == null) {
                return R.fail("401", "登录已过期，请重新登录");
            }
            Long teacherId = Long.valueOf(loginId.toString());
            batchSlotReq.setTeacherId(teacherId);

            log.info("批量创建时间段, 教师ID: {}, 日期数量: {}, 时间数量: {}, 时长: {}分钟", teacherId, batchSlotReq.getDates() != null
                ? batchSlotReq.getDates().size()
                : 0, batchSlotReq.getTimes() != null ? batchSlotReq.getTimes().size() : 0, batchSlotReq.getDuration());

            List<SlotResp> result = slotService.batchCreateSlot(batchSlotReq);
            log.info("批量创建时间段完成, 成功创建{}个时间段", result.size());
            return R.ok(result);
        } catch (Exception e) {
            log.error("批量创建时间段失败", e);
            return R.fail("500", "创建失败: " + e.getMessage());
        }
    }

    /**
     * 删除时间段
     *
     * @param id 时间段ID
     * @return 删除结果
     */
    @SaIgnore
    @DeleteMapping("/{id}")
    @Operation(summary = "删除时间段", description = "删除指定的时间段")
    public R<Void> deleteSlot(@PathVariable("id") Long id) {
        log.info("删除时间段, ID: {}", id);

        try {
            // 1. 验证时间段是否存在
            SlotDetailResp slot = slotService.get(id);
            if (slot == null) {
                log.warn("时间段不存在, ID: {}", id);
                return R.fail("404", "时间段不存在");
            }

            // 2. 检查是否有预约，如有预约则不能删除
            List<BookingDO> bookings = bookingService.getBySlotId(id);
            if (bookings != null && !bookings.isEmpty()) {
                log.warn("时间段已有预约，不能删除, ID: {}, 预约数量: {}", id, bookings.size());
                return R.fail("400", "该时间段已有预约，不能删除");
            }

            // 3. 执行软删除操作 - 仅更新status状态为0（禁用）
            // 使用部分更新，只修改status字段
            slotService.disableById(id);

            log.info("删除时间段成功, ID: {}", id);
            return R.ok("删除成功");

        } catch (Exception e) {
            log.error("删除时间段失败, ID: {}", id, e);
            return R.fail("500", "删除失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除未预约的时间段
     *
     * @param slotIds 时间段ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除未预约时间段", description = "批量删除指定的未预约时间段，返回成功和失败的统计信息")
    public R<Map<String, Integer>> batchDeleteUnbookedSlots(@RequestBody List<Long> slotIds) {
        try {
            log.info("批量删除时间段请求, IDs: {}", slotIds);

            if (slotIds == null || slotIds.isEmpty()) {
                return R.fail("400", "时间段ID列表不能为空");
            }

            // 调用服务层批量删除
            Map<String, Integer> result = slotService.batchDeleteUnbookedSlots(slotIds);

            log.info("批量删除时间段完成, 成功: {}, 失败: {}", result.get("success"), result.get("failed"));
            return R.ok("批量删除完成", result);

        } catch (Exception e) {
            log.error("批量删除时间段失败", e);
            return R.fail("500", "批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取教师本周统计数据
     *
     * @param teacherId 教师ID
     * @return 本周统计数据
     */
    @SaIgnore
    @GetMapping("/weekly-stats")
    @Operation(summary = "获取教师本周统计数据", description = "获取教师本周的课程统计：总课时、已预约、可预约、预估收入")
    public R<Map<String, Object>> getWeeklyStats(@RequestParam("teacherId") Long teacherId) {
        log.info("获取教师本周统计数据, 教师ID: {}", teacherId);

        try {
            Map<String, Object> stats = slotService.getWeeklyStats(teacherId);

            log.info("获取教师本周统计数据成功, 教师ID: {}, 统计结果: {}", teacherId, stats);
            return R.ok(stats);

        } catch (Exception e) {
            log.error("获取教师本周统计数据失败, 教师ID: {}", teacherId, e);
            return R.fail("500", "获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 将BookingDO转换为BookingDetailInfo
     *
     * @param booking 预约实体
     * @return 预约详细信息
     */
    private SlotResp.BookingDetailInfo convertToBookingDetailInfo(BookingDO booking) {
        SlotResp.BookingDetailInfo detailInfo = new SlotResp.BookingDetailInfo();

        detailInfo.setStudentId(booking.getStudentId());
        detailInfo.setStudentName(booking.getStudentName());
        detailInfo.setStudentPhone(booking.getStudentPhone());
        detailInfo.setMaterialId(booking.getMaterialId());
        detailInfo.setMaterialName(booking.getMaterialName());
        detailInfo.setMaterialCode(booking.getMaterialCode());
        detailInfo.setMaterialLevel(booking.getMaterialLevel());
        detailInfo.setLessonId(booking.getLessonId());
        detailInfo.setLessonName(booking.getLessonName());
        detailInfo.setLessonUrl(booking.getLessonUrl());
        detailInfo.setAccountId(booking.getAccountId());
        detailInfo.setRemark(booking.getRemark());

        // 设置操作人信息（从BaseDO继承的审计字段）
        if (booking.getCreateUser() != null) {
            // 可以根据需要查询用户名，这里先使用用户ID
            detailInfo.setOperatorName("用户ID: " + booking.getCreateUser());
        }

        // 设置操作时间（格式化创建时间）
        if (booking.getCreateTime() != null) {
            detailInfo.setOperateTime(booking.getCreateTime().toString());
        }

        return detailInfo;
    }
}
