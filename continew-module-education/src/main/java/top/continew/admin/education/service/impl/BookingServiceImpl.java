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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.continew.admin.education.constant.ClassinConstants;
import top.continew.admin.education.helper.ClassinHelper;
import top.continew.admin.education.mapper.BookingMapper;
import top.continew.admin.education.model.entity.BookingDO;
import top.continew.admin.education.model.entity.ClassinUserDO;
import top.continew.admin.education.model.query.BookingQuery;
import top.continew.admin.education.model.req.BookingReq;
import top.continew.admin.education.model.resp.*;
import top.continew.admin.education.service.*;
import top.continew.starter.extension.crud.service.BaseServiceImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预约业务实现
 *
 * @author don
 * @since 2025/05/23 23:25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl extends BaseServiceImpl<BookingMapper, BookingDO, BookingResp, BookingDetailResp, BookingQuery, BookingReq> implements BookingService {

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private StuCardService stuCardService;

    @Autowired
    private SlotService slotService;

    @Autowired
    private ClassinHelper classinHelper;

    /**
     * 创建预约前处理
     * 根据前端提交的 memberId 和 cardId 自动获取会员姓名和会员卡名称
     *
     * @param req 创建信息
     */
    @Override
    protected void beforeCreate(BookingReq req) {
        // 获取请求参数
        Long studentId = req.getStudentId();
        Long cardId = req.getStuCardId();
        Long slotId = req.getSlotId();

        log.info("预约参数: studentId={}, cardId={}, materialId={}, slotId={}, createUser={}", studentId, cardId, req
            .getMaterialId(), slotId, req.getCreateUser());

        // 检查该学生是否已经预约过该课时
        if (studentId != null && slotId != null) {
            LambdaQueryWrapper<BookingDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BookingDO::getSlotId, slotId).eq(BookingDO::getStudentId, studentId);
            long count = baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new RuntimeException("您已经预约过该课时，不能重复预约");
            }
            log.info("检查重复预约通过");

            // 新增：校验课时预约人数不能超过studentCount
            // 查询该课时已预约人数
            LambdaQueryWrapper<BookingDO> slotCountWrapper = new LambdaQueryWrapper<>();
            slotCountWrapper.eq(BookingDO::getSlotId, slotId);
            long bookedCount = baseMapper.selectCount(slotCountWrapper);
            // 获取课时最大可预约人数
            SlotDetailResp slot = slotService.get(slotId);
            if (slot != null && slot.getStudentCount() != null) {
                int maxCount = slot.getStudentCount();
                if (bookedCount >= maxCount) {
                    throw new RuntimeException("该课时预约人数已满，无法继续预约");
                }
            }
        }

        // 根据 slotId 获取课时信息
        if (slotId != null) {
            SlotDetailResp slot = slotService.get(slotId);
            if (slot != null) {
                req.setStartDate(slot.getStartDate());
                req.setStartTime(slot.getStartTime());
                log.info("设置课时信息: startDate={}, startTime={}", slot.getStartDate(), slot.getStartTime());
            }
        }

        // 根据 studentId 获取学生信息
        if (studentId != null) {
            StudentDetailResp student = studentService.get(studentId);
            if (student != null) {
                req.setStudentName(student.getName());
                log.info("设置学生姓名: {}", student.getName());

                // 设置学生手机号
                req.setPhone(student.getPhone());
                log.info("设置学生手机号: {}", student.getPhone());

                // 设置操作人名字
                req.setOperatorName(student.getName());
                log.info("设置操作人名字: {}", req.getOperatorName());
            }
        }

        // 设置操作时间为当前时间
        req.setOperateTime(LocalDateTime.now());
        log.info("设置操作时间: {}", req.getOperateTime());

        // 根据 cardId 获取会员卡信息
        if (cardId != null) {
            StuCardDetailResp stuCard = stuCardService.get(cardId);
            if (stuCard != null) {
                req.setCardName(stuCard.getCardName());
                log.info("设置会员卡名称: {}", stuCard.getCardName());
            }
        }

        // 如果没有设置创建人，则设置默认值
        if (req.getCreateUser() == null) {
            req.setCreateUser(1L); // 默认系统管理员
        }
    }

    /**
     * 根据课时ID列表查询对应的预约信息
     *
     * @param slotIds 课时ID列表
     * @return 课时ID到学生姓名列表的映射
     */
    @Override
    public Map<Long, List<String>> findStudentNamesBySlotIds(List<Long> slotIds) {
        if (slotIds == null || slotIds.isEmpty()) {
            return new HashMap<>();
        }

        // 查询所有匹配的预约记录
        LambdaQueryWrapper<BookingDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(BookingDO::getSlotId, slotIds);
        List<BookingDO> bookings = baseMapper.selectList(queryWrapper);

        // 按照slotId分组，合并多个学生的名字
        return bookings.stream()
            .filter(booking -> booking.getStudentName() != null && !booking.getStudentName().isEmpty())
            .collect(Collectors.groupingBy(BookingDO::getSlotId, Collectors
                .mapping(BookingDO::getStudentName, Collectors.toList())));
    }

    /**
     * 创建预约后处理
     * 同步预约信息到 ClassIn 系统
     *
     * @param req    创建信息
     * @param entity 创建的预约实体
     */
    @Override
    protected void afterCreate(BookingReq req, BookingDO entity) {
        try {
            log.info("开始同步预约信息到 ClassIn 系统: bookingId={}", entity.getId());

            // 获取课时信息
            SlotDetailResp slot = slotService.get(entity.getSlotId());
            if (slot == null) {
                log.error("课时信息不存在: slotId={}", entity.getSlotId());
                return;
            }

            TeacherDetailResp teacherDetailResp = teacherService.get(slot.getTeacherId());
            if (teacherDetailResp == null) {
                log.error("老师信息不存在: teacherId={}", slot.getTeacherId());
                return;
            }
            ClassinUserDO teacherClassinUser = classinHelper.getClassinUser(teacherDetailResp.getId(), ClassinConstants.USER_TYPE_TEACHER, teacherDetailResp.getName(), teacherDetailResp.getPhone(), teacherDetailResp.getEmail());

            // 获取学生信息
            StudentDetailResp student = studentService.get(entity.getStudentId());
            if (student == null) {
                log.error("学生信息不存在: studentId={}", entity.getStudentId());
                return;
            }
            ClassinUserDO stuClassinUserDO = classinHelper.getClassinUser(student.getId(), ClassinConstants.USER_TYPE_STUDENT, student.getName(), student.getPhone(), student.getEmail());

            // 构建 ClassIn 预约请求参数
            Map<String, Object> classInParams = new HashMap<>();
            classInParams.put("studentId", stuClassinUserDO.getClassinUid());
            classInParams.put("startTime", entity.getStartDate() + " " + entity.getStartTime());
            classInParams.put("duration", slot.getDuration());
            classInParams.put("teacherId", teacherClassinUser.getClassinUid());

            // TODO: 调用 ClassIn API 进行预约同步
            // classInService.createBooking(classInParams);

            log.info("预约信息同步到 ClassIn 系统成功: bookingId={}", entity.getId());
        } catch (Exception e) {
            log.error("同步预约信息到 ClassIn 系统失败: bookingId={}, error={}", entity.getId(), e.getMessage(), e);
            // 这里可以选择是否抛出异常，取决于业务需求
            // throw new RuntimeException("同步预约信息到 ClassIn 系统失败", e);
        }
    }
}