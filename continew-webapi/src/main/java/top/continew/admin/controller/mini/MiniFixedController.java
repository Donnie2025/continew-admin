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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.continew.starter.web.model.R;
import top.continew.admin.education.model.req.FixedReq;
import top.continew.admin.education.model.req.FixedBookingReq;
import top.continew.admin.education.model.resp.FixedResp;
import top.continew.admin.education.service.FixedService;
import top.continew.admin.education.service.FixedBookingService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 小程序固定课管理 API
 *
 * @author Charles7c
 * @since 2024/12/28 20:30
 */
@Tag(name = "小程序固定课管理 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mini/fixed")
@Validated
public class MiniFixedController {

    private final FixedService fixedService;
    private final FixedBookingService fixedBookingService;

    @Operation(summary = "教师创建固定课", description = "教师在小程序中创建新的固定课")
    @PostMapping
    public R<Long> createFixed(@Valid @RequestBody FixedReq req) {
        return R.ok(fixedService.createFixed(req));
    }

    @Operation(summary = "教师查询自己的固定课", description = "教师查询自己创建的所有固定课")
    @GetMapping("/teacher/{teacherId}")
    public R<List<FixedResp>> listByTeacherId(@Parameter(description = "教师ID", example = "1") @PathVariable @NotNull Long teacherId) {
        return R.ok(fixedService.listByTeacherId(teacherId));
    }

    @Operation(summary = "教师删除固定课", description = "教师删除自己创建的固定课")
    @DeleteMapping("/{id}")
    public R<String> deleteFixed(@Parameter(description = "固定课ID", example = "1") @PathVariable @NotNull Long id) {
        fixedService.deleteFixed(id);
        return R.ok();
    }

    @Operation(summary = "学生查询可预约的固定课", description = "学生查询指定星期几的所有可预约固定课")
    @GetMapping("/available/{weekDay}")
    public R<List<FixedResp>> listAvailableByWeekDay(@Parameter(description = "星期几：1-周一，2-周二，3-周三，4-周四，5-周五，6-周六，7-周日", example = "1") @PathVariable @NotNull Integer weekDay) {
        return R.ok(fixedService.listByWeekDay(weekDay));
    }

    @Operation(summary = "学生预约固定课", description = "学生预约指定的固定课")
    @PostMapping("/booking")
    public R<Long> bookFixed(@Valid @RequestBody FixedBookingReq req) {
        return R.ok(fixedBookingService.bookFixed(req));
    }

    @Operation(summary = "学生取消预约", description = "学生取消已预约的固定课")
    @DeleteMapping("/booking")
    public R<String> cancelBooking(@Parameter(description = "固定课ID", example = "1") @RequestParam @NotNull Long fixedId,
                                   @Parameter(description = "学生ID", example = "1") @RequestParam @NotNull Long studentId) {
        fixedBookingService.cancelBooking(fixedId, studentId);
        return R.ok();
    }

    @Operation(summary = "学生查询自己的预约", description = "学生查询自己预约的所有固定课")
    @GetMapping("/booking/student/{studentId}")
    public R<List<Long>> listBookingsByStudentId(@Parameter(description = "学生ID", example = "1") @PathVariable @NotNull Long studentId) {
        return R.ok(fixedBookingService.listByStudentId(studentId));
    }

    @Operation(summary = "检查预约状态", description = "检查学生是否已预约指定固定课")
    @GetMapping("/booking/check")
    public R<Boolean> checkBookingStatus(@Parameter(description = "固定课ID", example = "1") @RequestParam @NotNull Long fixedId,
                                         @Parameter(description = "学生ID", example = "1") @RequestParam @NotNull Long studentId) {
        boolean isBooked = fixedBookingService.isAlreadyBooked(fixedId, studentId);
        return R.ok(isBooked);
    }

    @Operation(summary = "学生查询指定教师的固定课（含预约状态）", description = "学生查询指定教师的可用固定课，包含预约状态")
    @GetMapping("/student/{studentId}")
    public R<List<FixedResp>> listForStudent(@Parameter(description = "学生ID", example = "1") @PathVariable @NotNull Long studentId,
                                             @Parameter(description = "教师ID", example = "1") @RequestParam @NotNull Long teacherId) {
        return R.ok(fixedService.listForStudent(studentId, teacherId));
    }
}
