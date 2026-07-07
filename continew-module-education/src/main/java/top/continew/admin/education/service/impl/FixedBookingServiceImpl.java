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
import top.continew.admin.education.mapper.FixedBookingMapper;
import top.continew.admin.education.mapper.FixedMapper;
import top.continew.admin.education.model.entity.FixedBookingDO;
import top.continew.admin.education.model.entity.FixedDO;
import top.continew.admin.education.model.entity.StudentDO;
import top.continew.admin.education.model.req.FixedBookingReq;
import top.continew.admin.education.model.resp.FixedResp;
import top.continew.admin.education.service.FixedBookingService;
import top.continew.admin.education.service.StudentService;
import top.continew.admin.education.service.FixedLogService;
import top.continew.admin.common.context.UserContextHolder;
import top.continew.admin.education.enums.RecordStatusEnum;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.bean.BeanUtil;
import top.continew.admin.education.service.TeacherService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 固定课预约业务实现
 *
 * @author Charles7c
 * @since 2024/12/28 19:00
 */
@Service
@RequiredArgsConstructor
public class FixedBookingServiceImpl implements FixedBookingService {

    private final FixedBookingMapper fixedBookingMapper;
    private final FixedMapper fixedMapper;
    private final StudentService studentService;
    private final FixedLogService fixedLogService;
    private final TeacherService teacherService;

    @Override
    @Transactional
    public Long bookFixed(FixedBookingReq req) {
        if (req == null || req.getFixedId() == null || req.getStudentId() == null) {
            throw new RuntimeException("预约参数不能为空");
        }

        // 检查固定课是否存在
        FixedDO fixed = fixedMapper.selectById(req.getFixedId());
        if (fixed == null) {
            throw new RuntimeException("固定课不存在");
        }

        // 检查是否已预约
        if (isAlreadyBooked(req.getFixedId(), req.getStudentId())) {
            throw new RuntimeException("已预约该固定课，请勿重复预约");
        }

        // 检查预约数量是否超限
        int bookedCount = countByFixedId(req.getFixedId());
        if (bookedCount >= fixed.getMaxStudents()) {
            throw new RuntimeException("预约人数已满，无法预约");
        }

        // 获取学生信息
        StudentDO student = studentService.getById(req.getStudentId());
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 获取当前操作用户ID，如果没有则使用系统默认值
        Long updateUserId;
        try {
            updateUserId = UserContextHolder.getUserId();
        } catch (Exception e) {
            // 小程序环境下可能没有用户上下文，使用系统默认值
            updateUserId = 1L;
        }

        // 创建预约记录
        FixedBookingDO booking = new FixedBookingDO();
        booking.setFixedId(req.getFixedId());
        booking.setStudentId(req.getStudentId());
        booking.setStudentName(student.getName() != null ? student.getName() : student.getNickname());
        booking.setStudentPhone(student.getPhone());
        booking.setTeacherId(fixed.getTeacherId());
        booking.setTeacherName(fixed.getTeacherName());
        booking.setWeekDay(fixed.getWeekDay());
        booking.setStartTime(fixed.getStartTime());
        booking.setDurationMinutes(fixed.getDurationMinutes());
        booking.setStatus(1);
        booking.setCreateUser(updateUserId);
        booking.setUpdateUser(updateUserId);

        fixedBookingMapper.insert(booking);

        // 记录预约操作日志
        fixedLogService.logBooking(req.getFixedId(), req.getStudentId(), student.getName() != null
            ? student.getName()
            : student.getNickname(), fixed.getTeacherId(), fixed.getTeacherName(), updateUserId, "系统");

        return booking.getId();
    }

    @Override
    @Transactional
    public void cancelBooking(Long fixedId, Long studentId) {
        if (fixedId == null || studentId == null) {
            throw new RuntimeException("取消预约参数不能为空");
        }

        // 查找预约记录
        List<FixedBookingDO> bookings = fixedBookingMapper.selectByFixedId(fixedId)
            .stream()
            .filter(booking -> ObjectUtil.equals(booking.getStudentId(), studentId))
            .collect(Collectors.toList());

        if (bookings.isEmpty()) {
            throw new RuntimeException("未找到预约记录");
        }

        // 获取当前操作用户ID
        Long updateUserId;
        try {
            updateUserId = UserContextHolder.getUserId();
        } catch (Exception e) {
            // 小程序环境下可能没有用户上下文，使用系统默认值
            updateUserId = 1L;
        }

        // 软删除预约记录
        FixedBookingDO booking = bookings.get(0);
        booking.setStatus(RecordStatusEnum.DISABLED.getValue());
        booking.setUpdateUser(updateUserId);
        fixedBookingMapper.updateById(booking);

        // 记录取消预约操作日志
        fixedLogService.logCancel(fixedId, studentId, booking.getStudentName(), booking.getTeacherId(), booking
            .getTeacherName(), updateUserId, "系统");
    }

    @Override
    public List<Long> listByStudentId(Long studentId) {
        if (studentId == null) {
            return List.of();
        }

        return fixedBookingMapper.selectByStudentId(studentId)
            .stream()
            .map(FixedBookingDO::getFixedId)
            .collect(Collectors.toList());
    }

    @Override
    public List<Long> listByFixedId(Long fixedId) {
        if (fixedId == null) {
            return List.of();
        }

        return fixedBookingMapper.selectByFixedId(fixedId)
            .stream()
            .map(FixedBookingDO::getId)
            .collect(Collectors.toList());
    }

    @Override
    public List<Long> listByTeacherId(Long teacherId) {
        if (teacherId == null) {
            return List.of();
        }

        return fixedBookingMapper.selectByTeacherId(teacherId)
            .stream()
            .map(FixedBookingDO::getId)
            .collect(Collectors.toList());
    }

    @Override
    public int countByFixedId(Long fixedId) {
        if (fixedId == null) {
            return 0;
        }

        return fixedBookingMapper.countByFixedId(fixedId);
    }

    @Override
    public boolean isAlreadyBooked(Long fixedId, Long studentId) {
        if (fixedId == null || studentId == null) {
            return false;
        }

        return fixedBookingMapper.countByFixedIdAndStudentId(fixedId, studentId) > 0;
    }

    @Override
    public boolean hasActiveBooking(Long fixedId) {
        if (fixedId == null) {
            return false;
        }

        return countByFixedId(fixedId) > 0;
    }

    @Override
    public List<String> getStudentNamesByFixedId(Long fixedId) {
        if (fixedId == null) {
            return List.of();
        }

        return fixedBookingMapper.selectByFixedId(fixedId).stream().map(booking -> {
            // 直接使用FixedBookingDO中的studentName冗余字段
            return booking.getStudentName() != null ? booking.getStudentName() : "未知学生";
        }).collect(Collectors.toList());
    }

    @Override
    public List<String> getStudentPhonesByFixedId(Long fixedId) {
        if (fixedId == null) {
            return List.of();
        }

        return fixedBookingMapper.selectByFixedId(fixedId).stream().map(booking -> {
            // 直接使用FixedBookingDO中的studentPhone冗余字段
            return booking.getStudentPhone() != null ? booking.getStudentPhone() : "";
        }).collect(Collectors.toList());
    }

    @Override
    public List<java.util.Map<String, Object>> getBookingDetailsByFixedId(Long fixedId) {
        if (fixedId == null) {
            return List.of();
        }

        return fixedBookingMapper.selectByFixedId(fixedId).stream().map(booking -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", booking.getId());
            map.put("studentId", booking.getStudentId());
            map.put("studentName", booking.getStudentName());
            map.put("createTime", booking.getCreateTime());
            map.put("status", booking.getStatus());

            // 获取学生手机号
            try {
                StudentDO student = studentService.getById(booking.getStudentId());
                if (student != null) {
                    map.put("studentPhone", student.getPhone());
                }
            } catch (Exception e) {
                // 如果获取失败，不影响其他信息的返回
                map.put("studentPhone", null);
            }

            return map;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            throw new RuntimeException("预约ID不能为空");
        }

        FixedBookingDO booking = fixedBookingMapper.selectById(id);
        if (booking == null) {
            throw new RuntimeException("预约记录不存在");
        }

        // 获取当前操作用户ID
        Long updateUserId;
        try {
            updateUserId = UserContextHolder.getUserId();
        } catch (Exception e) {
            updateUserId = 1L;
        }

        // 软删除预约记录
        booking.setStatus(RecordStatusEnum.DISABLED.getValue());
        booking.setUpdateUser(updateUserId);
        fixedBookingMapper.updateById(booking);

        // 记录取消预约操作日志
        fixedLogService.logCancel(booking.getFixedId(), booking.getStudentId(), booking.getStudentName(), booking
            .getTeacherId(), booking.getTeacherName(), updateUserId, "系统");
    }

    @Override
    public List<FixedResp> listFixedDetailsByStudentId(Long studentId) {
        if (studentId == null) {
            return List.of();
        }

        // 获取学生的所有预约的固定课ID列表
        List<Long> fixedIds = listByStudentId(studentId);
        if (fixedIds.isEmpty()) {
            return List.of();
        }

        // 根据固定课ID列表查询固定课详情
        return fixedIds.stream()
            .map(fixedId -> {
                FixedDO fixed = fixedMapper.selectById(fixedId);
                if (fixed == null) {
                    return null;
                }

                FixedResp resp = BeanUtil.copyProperties(fixed, FixedResp.class);
                resp.setIsBooked(true);
                resp.setIsMyBooking(true);

                // 查询教师信息，补充头像和标签
                if (fixed.getTeacherId() != null) {
                    try {
                        top.continew.admin.education.model.entity.TeacherDO teacher = teacherService.getById(fixed.getTeacherId());
                        if (teacher != null) {
                            // 过滤掉状态为0（失效）的教师
                            if (teacher.getStatus() != null && teacher.getStatus() == 0) {
                                return null;
                            }
                            resp.setTeacherAvatar(teacher.getAvatar());
                            resp.setTeacherTags(teacher.getTags());
                        }
                    } catch (Exception e) {
                        // 如果查询教师信息失败，不影响其他数据返回
                    }
                }

                return resp;
            })
            .filter(ObjectUtil::isNotNull)
            .collect(Collectors.toList());
    }
}
