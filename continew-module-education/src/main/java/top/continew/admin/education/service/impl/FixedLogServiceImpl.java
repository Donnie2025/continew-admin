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
import top.continew.admin.education.mapper.FixedLogMapper;
import top.continew.admin.education.model.entity.FixedLogDO;
import top.continew.admin.education.service.FixedLogService;

import java.time.LocalDateTime;

/**
 * 固定课操作记录业务实现
 *
 * @author Charles7c
 * @since 2024/12/28 19:30
 */
@Service
@RequiredArgsConstructor
public class FixedLogServiceImpl implements FixedLogService {

    private final FixedLogMapper fixedLogMapper;

    @Override
    public void logBooking(Long fixedId,
                           Long studentId,
                           String studentName,
                           Long teacherId,
                           String teacherName,
                           Long opUserId,
                           String opUserName) {
        FixedLogDO log = new FixedLogDO();
        log.setFixedId(fixedId);
        log.setStudentId(studentId);
        log.setStudentName(studentName);
        log.setTeacherId(teacherId);
        log.setTeacherName(teacherName);
        log.setOpType(1); // 1-预约
        log.setOpDesc(String.format("学生 %s 预约了教师 %s 的固定课", studentName, teacherName));
        log.setOpTime(LocalDateTime.now());
        log.setOpUser(opUserId);
        log.setOpUserName(opUserName != null ? opUserName : "系统");
        log.setCreateUser(opUserId);
        log.setUpdateUser(opUserId);

        fixedLogMapper.insert(log);
    }

    @Override
    public void logCancel(Long fixedId,
                          Long studentId,
                          String studentName,
                          Long teacherId,
                          String teacherName,
                          Long opUserId,
                          String opUserName) {
        FixedLogDO log = new FixedLogDO();
        log.setFixedId(fixedId);
        log.setStudentId(studentId);
        log.setStudentName(studentName);
        log.setTeacherId(teacherId);
        log.setTeacherName(teacherName);
        log.setOpType(2); // 2-取消
        log.setOpDesc(String.format("学生 %s 取消了教师 %s 的固定课预约", studentName, teacherName));
        log.setOpTime(LocalDateTime.now());
        log.setOpUser(opUserId);
        log.setOpUserName(opUserName != null ? opUserName : "系统");
        log.setCreateUser(opUserId);
        log.setUpdateUser(opUserId);

        fixedLogMapper.insert(log);
    }
}
