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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.education.mapper.TeacherPaymentMapper;
import top.continew.admin.education.model.entity.TeacherPaymentDO;
import top.continew.admin.education.service.TeacherPaymentService;
import cn.hutool.core.util.StrUtil;

/**
 * 教师收款信息业务实现
 *
 * @author donnie
 * @since 2026/06/17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherPaymentServiceImpl implements TeacherPaymentService {

    private final TeacherPaymentMapper teacherPaymentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Long teacherId,
                             String teacherName,
                             String paymentChannel,
                             String accountNumber,
                             String accountName,
                             String qrCode,
                             String bankName,
                             Integer rate) {
        if (teacherId == null || StrUtil.isBlank(paymentChannel)) {
            return;
        }

        // Check if payment info already exists for this teacher
        LambdaQueryWrapper<TeacherPaymentDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeacherPaymentDO::getTeacherId, teacherId);
        TeacherPaymentDO existingPayment = teacherPaymentMapper.selectOne(queryWrapper);

        if (existingPayment != null) {
            // Update existing payment info
            existingPayment.setTeacherName(teacherName);
            existingPayment.setPaymentChannel(paymentChannel);
            existingPayment.setAccountNumber(accountNumber);
            existingPayment.setAccountName(accountName);
            existingPayment.setQrCode(qrCode);
            existingPayment.setBankName(bankName);
            existingPayment.setRate(rate);
            teacherPaymentMapper.updateById(existingPayment);
            log.info("Updated payment info for teacher ID: {}", teacherId);
        } else {
            // Create new payment info
            TeacherPaymentDO payment = new TeacherPaymentDO();
            payment.setTeacherId(teacherId);
            payment.setTeacherName(teacherName);
            payment.setPaymentChannel(paymentChannel);
            payment.setAccountNumber(accountNumber);
            payment.setAccountName(accountName);
            payment.setQrCode(qrCode);
            payment.setBankName(bankName);
            payment.setRate(rate != null ? rate : 0);
            payment.setIsDefault(1);
            payment.setStatus(1);
            teacherPaymentMapper.insert(payment);
            log.info("Created payment info for teacher ID: {}", teacherId);
        }
    }

    @Override
    public TeacherPaymentDO getByTeacherId(Long teacherId) {
        if (teacherId == null) {
            return null;
        }
        LambdaQueryWrapper<TeacherPaymentDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeacherPaymentDO::getTeacherId, teacherId);
        return teacherPaymentMapper.selectOne(queryWrapper);
    }
}
