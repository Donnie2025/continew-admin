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

/**
 * 固定课操作记录业务接口
 *
 * @author Charles7c
 * @since 2024/12/28 19:30
 */
public interface FixedLogService {

    /**
     * 记录预约操作
     *
     * @param fixedId     固定课ID
     * @param studentId   学生ID
     * @param studentName 学生姓名
     * @param teacherId   教师ID
     * @param teacherName 教师姓名
     * @param opUserId    操作用户ID
     * @param opUserName  操作用户姓名
     */
    void logBooking(Long fixedId,
                    Long studentId,
                    String studentName,
                    Long teacherId,
                    String teacherName,
                    Long opUserId,
                    String opUserName);

    /**
     * 记录取消预约操作
     *
     * @param fixedId     固定课ID
     * @param studentId   学生ID
     * @param studentName 学生姓名
     * @param teacherId   教师ID
     * @param teacherName 教师姓名
     * @param opUserId    操作用户ID
     * @param opUserName  操作用户姓名
     */
    void logCancel(Long fixedId,
                   Long studentId,
                   String studentName,
                   Long teacherId,
                   String teacherName,
                   Long opUserId,
                   String opUserName);
}
