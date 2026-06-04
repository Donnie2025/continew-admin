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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.FixedBookingMapper;
import top.continew.admin.education.mapper.FixedMapper;
import top.continew.admin.education.model.entity.FixedBookingDO;
import top.continew.admin.education.model.entity.FixedDO;
import top.continew.admin.education.model.query.FixedQuery;
import top.continew.admin.education.model.req.FixedBatchReq;
import top.continew.admin.education.model.req.FixedReq;
import top.continew.admin.education.model.resp.FixedResp;
import top.continew.admin.education.model.resp.TeacherDetailResp;
import top.continew.admin.education.service.FixedBookingService;
import top.continew.admin.education.service.FixedService;
import top.continew.admin.education.service.TeacherService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 固定课业务实现
 *
 * @author Charles7c
 * @since 2024/12/28 18:30
 */
@Service
@RequiredArgsConstructor
public class FixedServiceImpl extends BaseServiceImpl<FixedMapper, FixedDO, FixedResp, FixedResp, FixedQuery, FixedReq> implements FixedService {

    private final FixedBookingService fixedBookingService;
    private final FixedBookingMapper fixedBookingMapper;
    private final TeacherService teacherService;

    @Override
    public Long create(FixedReq req) {
        // 参数校验
        if (req == null) {
            throw new RuntimeException("请求参数不能为空");
        }
        if (req.getTeacherId() == null) {
            throw new RuntimeException("教师ID不能为空");
        }

        // 检查时间冲突
        if (isTimeConflict(req.getTeacherId(), req.getWeekDay(), req.getStartTime(), null)) {
            throw new RuntimeException("该教师在此时间段已有固定课安排");
        }

        // 获取教师信息
        TeacherDetailResp teacher = teacherService.get(req.getTeacherId());
        if (teacher == null) {
            throw new RuntimeException("教师信息不存在");
        }

        // 创建固定课
        FixedDO fixed = BeanUtil.copyProperties(req, FixedDO.class);
        fixed.setTeacherName(teacher.getName() != null ? teacher.getName() : "");
        fixed.setStatus(1);

        baseMapper.insert(fixed);
        return fixed.getId();
    }

    @Override
    public List<FixedResp> listByTeacherId(Long teacherId) {
        if (teacherId == null) {
            throw new RuntimeException("教师ID不能为空");
        }

        List<FixedDO> fixedList = ((FixedMapper)baseMapper).selectByTeacherId(teacherId);
        if (fixedList.isEmpty()) {
            return List.of();
        }

        // 批量加载：一次性获取该教师的所有预约数据，避免 N+1 查询
        List<FixedBookingDO> allBookings = fixedBookingMapper.selectByTeacherId(teacherId);
        Map<Long, List<FixedBookingDO>> bookingMap = allBookings.stream()
            .collect(Collectors.groupingBy(FixedBookingDO::getFixedId));

        // 一次性获取教师信息（头像、标签）
        String teacherAvatar = null;
        String teacherTags = null;
        try {
            top.continew.admin.education.model.entity.TeacherDO teacher = teacherService.getById(teacherId);
            if (teacher != null) {
                teacherAvatar = teacher.getAvatar();
                teacherTags = teacher.getTags();
            }
        } catch (Exception ignored) {
        }

        final String finalAvatar = teacherAvatar;
        final String finalTags = teacherTags;

        return fixedList.stream().map(fixedDO -> {
            FixedResp resp = new FixedResp();
            resp.setId(fixedDO.getId());
            resp.setTeacherId(fixedDO.getTeacherId());
            resp.setTeacherName(fixedDO.getTeacherName());
            resp.setTeacherAvatar(finalAvatar);
            resp.setTeacherTags(finalTags);
            resp.setWeekDay(fixedDO.getWeekDay());
            resp.setStartTime(fixedDO.getStartTime());
            resp.setDurationMinutes(fixedDO.getDurationMinutes());
            resp.setMaxStudents(fixedDO.getMaxStudents());
            resp.setStatus(fixedDO.getStatus());
            resp.setCreateTime(fixedDO.getCreateTime());
            resp.setUpdateTime(fixedDO.getUpdateTime());

            List<FixedBookingDO> bookings = bookingMap.getOrDefault(fixedDO.getId(), List.of());
            resp.setBookedCount(bookings.size());
            resp.setStudentNames(bookings.stream()
                .map(b -> b.getStudentName() != null ? b.getStudentName() : "未知学生")
                .collect(Collectors.toList()));
            resp.setStudentPhones(bookings.stream()
                .map(b -> b.getStudentPhone() != null ? b.getStudentPhone() : "")
                .collect(Collectors.toList()));
            return resp;
        }).collect(Collectors.toList());
    }

    @Override
    public List<FixedResp> listByWeekDay(Integer weekDay) {
        if (weekDay == null || weekDay < 1 || weekDay > 7) {
            throw new RuntimeException("星期几参数无效");
        }

        List<FixedDO> fixedList = ((FixedMapper)baseMapper).selectByWeekDay(weekDay);
        return fixedList.stream().map(this::toResp).collect(Collectors.toList());
    }

    @Override
    public boolean isTimeConflict(Long teacherId, Integer weekDay, String startTime, Long excludeId) {
        if (teacherId == null) {
            throw new RuntimeException("教师ID不能为空");
        }
        if (weekDay == null) {
            throw new RuntimeException("星期几不能为空");
        }
        if (StrUtil.isBlank(startTime)) {
            throw new RuntimeException("开始时间不能为空");
        }

        int count = ((FixedMapper)baseMapper).countTimeConflict(teacherId, weekDay, startTime, excludeId);
        return count > 0;
    }

    @Override
    @Transactional
    public Long createFixed(FixedReq req) {
        return create(req);
    }

    @Override
    @Transactional
    public void deleteFixed(Long id) {
        if (id == null) {
            throw new RuntimeException("固定课ID不能为空");
        }

        // 检查固定课是否存在
        FixedDO fixed = baseMapper.selectById(id);
        if (fixed == null) {
            throw new RuntimeException("固定课不存在");
        }

        // 检查是否有未完成的预约
        boolean hasActiveBooking = fixedBookingService.hasActiveBooking(id);
        if (hasActiveBooking) {
            throw new RuntimeException("该固定课存在有效预约，无法删除");
        }

        // 软删除固定课
        baseMapper.deleteById(id);
    }

    @Override
    public List<FixedResp> listForStudent(Long studentId, Long teacherId) {
        if (studentId == null) {
            throw new RuntimeException("学生ID不能为空");
        }
        if (teacherId == null) {
            throw new RuntimeException("教师ID不能为空");
        }

        // 1. 获取指定教师的启用固定课
        List<FixedDO> allFixed = baseMapper.selectList(new LambdaQueryWrapper<FixedDO>()
            .eq(FixedDO::getTeacherId, teacherId)
            .eq(FixedDO::getStatus, 1)
            .orderBy(true, true, FixedDO::getWeekDay, FixedDO::getStartTime));

        if (allFixed.isEmpty()) {
            return List.of();
        }

        // 2. 批量加载该教师所有预约数据（1次查询）
        List<FixedBookingDO> allBookings = fixedBookingMapper.selectByTeacherId(teacherId);
        Map<Long, List<FixedBookingDO>> bookingMap = allBookings.stream()
            .collect(Collectors.groupingBy(FixedBookingDO::getFixedId));

        // 3. 获取该学生已预约的固定课ID列表（1次查询）
        List<Long> bookedFixedIds = fixedBookingService.listByStudentId(studentId);

        // 4. 一次性获取教师扩展信息（1次查询）
        String teacherAvatar = null;
        String teacherTags = null;
        try {
            top.continew.admin.education.model.entity.TeacherDO teacher = teacherService.getById(teacherId);
            if (teacher != null) {
                teacherAvatar = teacher.getAvatar();
                teacherTags = teacher.getTags();
            }
        } catch (Exception ignored) {
        }
        final String finalAvatar = teacherAvatar;
        final String finalTags = teacherTags;

        // 5. 内存映射，无额外DB查询
        return allFixed.stream().map(fixedDO -> {
            FixedResp resp = new FixedResp();
            resp.setId(fixedDO.getId());
            resp.setTeacherId(fixedDO.getTeacherId());
            resp.setTeacherName(fixedDO.getTeacherName());
            resp.setTeacherAvatar(finalAvatar);
            resp.setTeacherTags(finalTags);
            resp.setWeekDay(fixedDO.getWeekDay());
            resp.setStartTime(fixedDO.getStartTime());
            resp.setDurationMinutes(fixedDO.getDurationMinutes());
            resp.setMaxStudents(fixedDO.getMaxStudents());
            resp.setStatus(fixedDO.getStatus());
            resp.setCreateTime(fixedDO.getCreateTime());
            resp.setUpdateTime(fixedDO.getUpdateTime());

            List<FixedBookingDO> bookings = bookingMap.getOrDefault(fixedDO.getId(), List.of());
            resp.setBookedCount(bookings.size());
            resp.setStudentNames(bookings.stream()
                .map(b -> b.getStudentName() != null ? b.getStudentName() : "未知学生")
                .collect(Collectors.toList()));
            resp.setStudentPhones(bookings.stream()
                .map(b -> b.getStudentPhone() != null ? b.getStudentPhone() : "")
                .collect(Collectors.toList()));

            resp.setIsBooked(resp.getBookedCount() > 0);
            resp.setIsMyBooking(bookedFixedIds.contains(fixedDO.getId()));

            return resp;
        }).collect(Collectors.toList());
    }

    /**
     * 转换为响应对象
     */
    private FixedResp toResp(FixedDO fixedDO) {
        if (fixedDO == null) {
            return null;
        }

        FixedResp resp = new FixedResp();
        resp.setId(fixedDO.getId());
        resp.setTeacherId(fixedDO.getTeacherId());
        resp.setTeacherName(fixedDO.getTeacherName());
        // 补充教师扩展信息（头像、简介、标签）
        if (fixedDO.getTeacherId() != null) {
            try {
                top.continew.admin.education.model.entity.TeacherDO teacher = teacherService.getById(fixedDO
                    .getTeacherId());
                if (teacher != null) {
                    resp.setTeacherAvatar(teacher.getAvatar());
                    resp.setTeacherTags(teacher.getTags());
                }
            } catch (Exception ignored) {
            }
        }
        resp.setWeekDay(fixedDO.getWeekDay());
        resp.setStartTime(fixedDO.getStartTime());
        resp.setDurationMinutes(fixedDO.getDurationMinutes());
        resp.setMaxStudents(fixedDO.getMaxStudents());
        resp.setStatus(fixedDO.getStatus());
        resp.setCreateTime(fixedDO.getCreateTime());
        resp.setUpdateTime(fixedDO.getUpdateTime());

        // 查询已预约学生数
        int bookedCount = fixedBookingService.countByFixedId(fixedDO.getId());
        resp.setBookedCount(bookedCount);

        // 查询已预约学生姓名列表
        List<String> studentNames = fixedBookingService.getStudentNamesByFixedId(fixedDO.getId());
        resp.setStudentNames(studentNames);

        // 查询已预约学生手机号列表
        List<String> studentPhones = fixedBookingService.getStudentPhonesByFixedId(fixedDO.getId());
        resp.setStudentPhones(studentPhones);

        return resp;
    }

    @Override
    public boolean isBatchTimeConflict(Long teacherId, List<Integer> weekDays, List<String> startTimes) {
        // 检查所有星期和时间的组合是否存在冲突
        for (Integer weekDay : weekDays) {
            for (String startTime : startTimes) {
                if (isTimeConflict(teacherId, weekDay, startTime, null)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchCreate(FixedBatchReq req) {
        // 参数校验
        if (req.getTeacherId() == null) {
            throw new RuntimeException("教师ID不能为空");
        }
        if (req.getWeekDays() == null || req.getWeekDays().isEmpty()) {
            throw new RuntimeException("星期列表不能为空");
        }
        if (req.getStartTimes() == null || req.getStartTimes().isEmpty()) {
            throw new RuntimeException("开始时间列表不能为空");
        }

        // 获取教师信息
        TeacherDetailResp teacher = teacherService.get(req.getTeacherId());
        if (teacher == null) {
            throw new RuntimeException("教师信息不存在");
        }

        // 批量创建固定课，跳过已存在的课程
        int createdCount = 0;
        for (Integer weekDay : req.getWeekDays()) {
            for (String startTime : req.getStartTimes()) {
                // 检查该时间段是否已有固定课
                if (isTimeConflict(req.getTeacherId(), weekDay, startTime, null)) {
                    // 已有课程，跳过
                    continue;
                }

                // 创建新的固定课
                FixedDO fixed = new FixedDO();
                fixed.setTeacherId(req.getTeacherId());
                fixed.setTeacherName(teacher.getName() != null ? teacher.getName() : "");
                fixed.setWeekDay(weekDay);
                fixed.setStartTime(startTime);
                fixed.setDurationMinutes(req.getDurationMinutes());
                fixed.setMaxStudents(req.getMaxStudents());
                fixed.setStatus(1);

                baseMapper.insert(fixed);
                createdCount++;
            }
        }

        return createdCount;
    }
}
