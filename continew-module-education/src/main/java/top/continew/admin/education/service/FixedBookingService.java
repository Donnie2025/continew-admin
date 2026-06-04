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

import top.continew.admin.education.model.req.FixedBookingReq;

import java.util.List;

/**
 * 固定课预约业务接口
 *
 * @author Charles7c
 * @since 2024/12/28 19:00
 */
public interface FixedBookingService {

    /**
     * 预约固定课
     *
     * @param req 预约请求
     * @return 预约ID
     */
    Long bookFixed(FixedBookingReq req);

    /**
     * 取消预约固定课
     *
     * @param fixedId   固定课ID
     * @param studentId 学生ID
     */
    void cancelBooking(Long fixedId, Long studentId);

    /**
     * 根据学生ID查询预约列表
     *
     * @param studentId 学生ID
     * @return 预约ID列表
     */
    List<Long> listByStudentId(Long studentId);

    /**
     * 根据固定课ID查询预约列表
     *
     * @param fixedId 固定课ID
     * @return 预约ID列表
     */
    List<Long> listByFixedId(Long fixedId);

    /**
     * 根据教师ID查询预约列表
     *
     * @param teacherId 教师ID
     * @return 预约ID列表
     */
    List<Long> listByTeacherId(Long teacherId);

    /**
     * 统计固定课的预约数量
     *
     * @param fixedId 固定课ID
     * @return 预约数量
     */
    int countByFixedId(Long fixedId);

    /**
     * 检查学生是否已预约该固定课
     *
     * @param fixedId   固定课ID
     * @param studentId 学生ID
     * @return 是否已预约
     */
    boolean isAlreadyBooked(Long fixedId, Long studentId);

    /**
     * 检查固定课是否有有效预约
     *
     * @param fixedId 固定课ID
     * @return 是否有有效预约
     */
    boolean hasActiveBooking(Long fixedId);

    /**
     * 根据固定课ID获取学生姓名列表
     * 
     * @param fixedId 固定课ID
     * @return 学生姓名列表
     */
    List<String> getStudentNamesByFixedId(Long fixedId);

    /**
     * 根据固定课ID获取学生手机号列表
     * 
     * @param fixedId 固定课ID
     * @return 学生手机号列表
     */
    List<String> getStudentPhonesByFixedId(Long fixedId);

    /**
     * 根据固定课ID获取预约详情列表
     *
     * @param fixedId 固定课ID
     * @return 预约详情列表
     */
    List<java.util.Map<String, Object>> getBookingDetailsByFixedId(Long fixedId);

    /**
     * 根据预约ID删除预约
     *
     * @param id 预约ID
     */
    void deleteById(Long id);
}
