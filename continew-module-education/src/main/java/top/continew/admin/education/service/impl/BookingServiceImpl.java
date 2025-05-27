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
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.BookingMapper;
import top.continew.admin.education.model.entity.BookingDO;
import top.continew.admin.education.model.query.BookingQuery;
import top.continew.admin.education.model.req.BookingReq;
import top.continew.admin.education.model.resp.BookingDetailResp;
import top.continew.admin.education.model.resp.BookingResp;
import top.continew.admin.education.service.BookingService;
import top.continew.admin.education.service.StudentService;
import top.continew.admin.education.service.StuCardService;
import top.continew.admin.education.service.SlotService;
import top.continew.admin.education.model.resp.StudentDetailResp;
import top.continew.admin.education.model.resp.StuCardDetailResp;
import top.continew.admin.education.model.resp.SlotDetailResp;
import java.time.LocalDateTime;

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
    private StuCardService stuCardService;
    
    @Autowired
    private SlotService slotService;

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
        Long cardId = req.getCardId();
        Long slotId = req.getSlotId();

        try {
            log.info("预约参数: studentId={}, cardId={}, materialId={}, slotId={}, createUser={}", studentId, cardId, req
                .getMaterialId(), slotId, req.getCreateUser());

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

        } catch (Exception e) {
            log.error("处理预约参数出错", e);
        }
    }
}