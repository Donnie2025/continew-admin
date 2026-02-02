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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.education.model.entity.BookingDO;
import top.continew.admin.education.model.req.BatchBookingReq;
import top.continew.admin.education.service.BookingService;
import top.continew.admin.education.model.resp.MyBookingResp;

import java.util.List;

/**
 * 小程序预约管理 API
 *
 * @author continew-org
 * @since 2024-12-29
 */
@Tag(name = "小程序预约管理 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mini/booking")
public class MiniBookingController {

    private final BookingService bookingService;

    @Operation(summary = "创建预约", description = "批量创建课程预约记录（事务性操作）")
    @PostMapping("/create")
    public List<Long> createBooking(@Valid @RequestBody BatchBookingReq batchReq) {
        return bookingService.createBatchBookingWithTransaction(batchReq);
    }

    @Operation(summary = "获取我的预约列表", description = "获取当前学生的所有预约记录")
    @GetMapping("/my")
    public List<MyBookingResp> getMyBookings() {
        return bookingService.getMyBookings();
    }

    @Operation(summary = "创建预约（RESTful风格）", description = "创建课程预约记录（事务性操作）")
    @PostMapping("/bookings")
    public Long createBookingRestful(@Valid @RequestBody BookingDO booking) {
        return bookingService.createBookingWithTransaction(booking);
    }

    @Operation(summary = "获取学生预约列表", description = "根据学生ID获取预约列表")
    @GetMapping("/students/{studentId}/bookings")
    public List<BookingDO> getStudentBookings(@Parameter(description = "学生ID") @PathVariable Long studentId) {
        return bookingService.listByStudentId(studentId);
    }

    @Operation(summary = "更新预约", description = "更新预约信息")
    @PutMapping("/bookings/{id}")
    public void updateBooking(@Parameter(description = "预约ID") @PathVariable Long id,
                              @Valid @RequestBody BookingDO booking) {
        booking.setId(id);
        bookingService.update(booking, id);
    }

    @Operation(summary = "删除预约", description = "删除预约记录")
    @DeleteMapping("/bookings/{id}")
    public void deleteBooking(@Parameter(description = "预约ID") @PathVariable Long id) {
        bookingService.delete(List.of(id));
    }

    @Operation(summary = "根据教材和学生获取预约", description = "根据教材ID和学生ID获取相关预约")
    @GetMapping("/materials/{materialId}/students/{studentId}/bookings")
    public List<BookingDO> getBookingsByMaterial(@Parameter(description = "教材ID") @PathVariable Long materialId,
                                                 @Parameter(description = "学生ID") @PathVariable Long studentId) {
        return bookingService.listByStudentIdAndMaterialId(studentId, materialId);
    }

    @Operation(summary = "获取学生最后一次预约记录", description = "获取当前登录学生的最后一次预约记录，用于智能选择下一节课")
    @GetMapping("/last-booking")
    public BookingDO getLastBooking() {
        return bookingService.getLastBookingByCurrentStudent();
    }

    @Operation(summary = "取消预约", description = "取消预约记录，需要满足时间条件（距离开课2小时以上）")
    @PostMapping("/cancel/{id}")
    public void cancelBooking(@Parameter(description = "预约ID") @PathVariable Long id) {
        bookingService.cancelBooking(id);
    }

    @Operation(summary = "教师取消预约", description = "教师取消预约记录，不受时间限制")
    @PostMapping("/cancel-by-teacher/{id}")
    public void cancelBookingByTeacher(@Parameter(description = "预约ID") @PathVariable Long id) {
        bookingService.cancelBookingByTeacher(id);
    }

    @Operation(summary = "教师通过时间段取消预约", description = "教师通过时间段ID和学生ID取消预约，不受时间限制")
    @PostMapping("/cancel-by-slot/{slotId}/student/{studentId}")
    public void cancelBookingBySlotAndStudent(@Parameter(description = "时间段ID") @PathVariable Long slotId,
                                              @Parameter(description = "学生ID") @PathVariable Long studentId) {
        bookingService.cancelBookingBySlotAndStudent(slotId, studentId);
    }

}
