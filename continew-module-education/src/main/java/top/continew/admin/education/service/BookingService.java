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
import top.continew.admin.education.model.entity.BookingDO;
import top.continew.admin.education.model.req.BatchBookingReq;
import top.continew.admin.education.model.query.BookingQuery;
import top.continew.admin.education.model.req.BookingReq;
import top.continew.admin.education.model.resp.BookingDetailResp;
import top.continew.admin.education.model.resp.BookingResp;
import top.continew.admin.education.model.resp.MyBookingResp;

import java.util.List;
import java.util.Map;

/**
 * 预约业务接口
 *
 * @author don
 * @since 2025/05/23 23:25
 */
public interface BookingService extends BaseService<BookingResp, BookingDetailResp, BookingQuery, BookingReq> {

    /**
     * 根据课时ID列表查询对应的预约信息
     *
     * @param slotIds 课时ID列表
     * @return 课时ID到学生姓名列表的映射
     */
    Map<Long, List<String>> findStudentNamesBySlotIds(List<Long> slotIds);

    /**
     * 根据课时ID列表查询详细的预约信息
     *
     * @param slotIds 课时ID列表
     * @return 课时ID到预约详细信息列表的映射
     */
    Map<Long, List<BookingDO>> findDetailedBookingsBySlotIds(List<Long> slotIds);

    /**
     * 根据学生ID获取预约列表
     *
     * @param studentId 学生ID
     * @return 预约列表
     */
    List<BookingDO> listByStudentId(Long studentId);

    /**
     * 根据学生ID和教材ID获取预约列表
     *
     * @param studentId  学生ID
     * @param materialId 教材ID
     * @return 预约列表
     */
    List<BookingDO> listByStudentIdAndMaterialId(Long studentId, Long materialId);

    /**
     * 添加预约记录
     *
     * @param booking 预约信息
     * @return 记录ID
     */
    Long add(BookingDO booking);

    /**
     * 更新预约记录
     *
     * @param booking 预约信息
     * @param id      记录ID
     */
    void update(BookingDO booking, Long id);

    /**
     * 删除预约记录
     *
     * @param ids 记录ID列表
     */
    void delete(List<Long> ids);

    /**
     * 创建预约记录（事务性操作）
     * 包含：1.创建预约记录 2.创建交易记录 3.创建课程记录 4.扣减学生卡余额
     *
     * @param booking 预约信息
     * @return 预约记录ID
     * @throws RuntimeException 当任何步骤失败时抛出异常并回滚
     */
    Long createBookingWithTransaction(BookingDO booking);

    /**
     * 批量创建预约记录（事务性操作）
     * 根据前端传递的批量预约请求，为每个时间段创建独立的预约记录
     *
     * @param batchReq 批量预约请求
     * @return 创建的预约记录ID列表
     * @throws RuntimeException 当任何步骤失败时抛出异常并回滚
     */
    List<Long> createBatchBookingWithTransaction(BatchBookingReq batchReq);

    /**
     * 获取当前登录学生的最后一次预约记录
     * 用于智能选择下一节课功能
     *
     * @return 最后一次预约记录，如果没有预约记录则返回null
     */
    BookingDO getLastBookingByCurrentStudent();

    /**
     * 获取学生已完成的课件列表
     * 根据预约时间与当前时间对比判断课件是否已完成
     *
     * @param studentId  学生ID
     * @param materialId 教材ID（可选，用于过滤特定教材的课件）
     * @return 已完成的课件ID列表
     */
    List<Long> getCompletedLessonIds(Long studentId, Long materialId);

    /**
     * 获取当前学生的预约列表（用于小程序"我的预约"功能）
     * 
     * @return 我的预约列表
     */
    List<MyBookingResp> getMyBookings();

    /**
     * 取消预约
     * 验证取消条件：1.只能取消未开课状态的课程 2.距离开课时间必须超过2小时
     * 
     * @param bookingId 预约ID
     * @throws RuntimeException 当不满足取消条件时抛出异常
     */
    void cancelBooking(Long bookingId);

    /**
     * 教师取消预约（无时间限制）
     * 
     * @param bookingId 预约ID
     * @throws RuntimeException 当预约记录不存在或状态异常时抛出异常
     */
    void cancelBookingByTeacher(Long bookingId);

    /**
     * 教师通过时间段和学生取消预约（无时间限制）
     * 
     * @param slotId    时间段ID
     * @param studentId 学生ID
     * @throws RuntimeException 当预约记录不存在或状态异常时抛出异常
     */
    void cancelBookingBySlotAndStudent(Long slotId, Long studentId);

    /**
     * 根据时间段ID获取预约列表
     * 
     * @param slotId 时间段ID
     * @return 预约列表
     */
    List<BookingDO> getBySlotId(Long slotId);
}