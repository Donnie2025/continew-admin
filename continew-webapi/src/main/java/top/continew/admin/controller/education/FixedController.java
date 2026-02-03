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

package top.continew.admin.controller.education;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.education.model.query.FixedQuery;
import top.continew.admin.education.model.req.FixedBatchReq;
import top.continew.admin.education.model.req.FixedBookingReq;
import top.continew.admin.education.model.req.FixedReq;
import top.continew.admin.education.model.resp.FixedResp;
import top.continew.admin.education.service.FixedBookingService;
import top.continew.admin.education.service.FixedService;
import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.starter.extension.crud.enums.Api;
import top.continew.starter.web.model.R;

import java.util.List;
import java.util.Map;

/**
 * 固定课管理 API
 *
 * @author Charles7c
 * @since 2024/12/28 20:00
 */
@Tag(name = "固定课管理 API")
@Validated
@RestController
@RequiredArgsConstructor
@CrudRequestMapping(value = "/education/fixed", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class FixedController extends BaseController<FixedService, FixedResp, FixedResp, FixedQuery, FixedReq> {

    private final FixedBookingService fixedBookingService;

    /**
     * 根据教师ID查询固定课列表
     *
     * @param teacherId 教师ID
     * @return 固定课列表
     */
    @Operation(summary = "根据教师ID查询固定课列表", description = "根据教师ID查询其创建的所有固定课")
    @GetMapping("/teacher/{teacherId}")
    public R<List<FixedResp>> listByTeacherId(@Parameter(description = "教师ID", example = "1") @PathVariable @NotNull Long teacherId) {
        return R.ok(baseService.listByTeacherId(teacherId));
    }

    /**
     * 根据星期几查询固定课列表
     *
     * @param weekDay 星期几
     * @return 固定课列表
     */
    @Operation(summary = "根据星期几查询固定课列表", description = "查询指定星期几的所有固定课")
    @GetMapping("/weekday/{weekDay}")
    public R<List<FixedResp>> listByWeekDay(@Parameter(description = "星期几：1-周一，2-周二，3-周三，4-周四，5-周五，6-周六，7-周日", example = "1") @PathVariable @NotNull Integer weekDay) {
        return R.ok(baseService.listByWeekDay(weekDay));
    }

    /**
     * 检查时间冲突
     *
     * @param teacherId 教师ID
     * @param weekDay   星期几
     * @param startTime 开始时间
     * @param excludeId 排除的固定课ID
     * @return 是否冲突
     */
    @Operation(summary = "检查时间冲突", description = "检查教师在指定时间是否已有固定课安排")
    @GetMapping("/conflict")
    public R<Boolean> checkTimeConflict(@Parameter(description = "教师ID", example = "1") @RequestParam @NotNull Long teacherId,
                                        @Parameter(description = "星期几：1-周一，2-周二，3-周三，4-周四，5-周五，6-周六，7-周日", example = "1") @RequestParam @NotNull Integer weekDay,
                                        @Parameter(description = "开始时间（HH:MM格式）", example = "19:00") @RequestParam @NotNull String startTime,
                                        @Parameter(description = "排除的固定课ID（用于更新时排除自己）", example = "1") @RequestParam(required = false) Long excludeId) {
        boolean conflict = baseService.isTimeConflict(teacherId, weekDay, startTime, excludeId);
        return R.ok(conflict);
    }

    /**
     * 批量检查时间冲突
     *
     * @param req 批量冲突检查请求
     * @return 是否存在冲突
     */
    @Operation(summary = "批量检查时间冲突", description = "检查教师在多个时间段是否存在冲突")
    @PostMapping("/conflict/batch")
    public R<Boolean> checkBatchTimeConflict(@RequestBody @Validated FixedBatchReq req) {
        boolean conflict = baseService.isBatchTimeConflict(req.getTeacherId(), req.getWeekDays(), req.getStartTimes());
        return R.ok(conflict);
    }

    /**
     * 批量创建固定课
     *
     * @param req 批量创建请求
     * @return 创建的固定课数量
     */
    @Operation(summary = "批量创建固定课", description = "批量创建固定课")
    @PostMapping("/batch")
    public R<Integer> batchCreate(@RequestBody @Validated FixedBatchReq req) {
        int count = baseService.batchCreate(req);
        return R.ok(count);
    }

    /**
     * 获取固定课的预约学生列表
     *
     * @param id 固定课ID
     * @return 预约学生列表
     */
    @Operation(summary = "获取固定课的预约学生列表", description = "获取指定固定课的所有预约学生信息")
    @GetMapping("/{id}/bookings")
    public R<List<Map<String, Object>>> getFixedBookings(@Parameter(description = "固定课ID", example = "1") @PathVariable @NotNull Long id) {
        return R.ok(fixedBookingService.getBookingDetailsByFixedId(id));
    }

    /**
     * 添加学生预约
     *
     * @param req 预约请求
     * @return 预约ID
     */
    @Operation(summary = "添加学生预约", description = "为固定课添加学生预约")
    @PostMapping("/booking")
    public R<Long> addFixedBooking(@RequestBody @Validated FixedBookingReq req) {
        return R.ok(fixedBookingService.bookFixed(req));
    }

    /**
     * 取消学生预约
     *
     * @param id 预约ID
     * @return 操作结果
     */
    @Operation(summary = "取消学生预约", description = "取消指定学生的固定课预约")
    @DeleteMapping("/booking/{id}")
    public R<Void> deleteFixedBooking(@Parameter(description = "预约ID", example = "1") @PathVariable @NotNull Long id) {
        fixedBookingService.deleteById(id);
        return R.ok();
    }
}
