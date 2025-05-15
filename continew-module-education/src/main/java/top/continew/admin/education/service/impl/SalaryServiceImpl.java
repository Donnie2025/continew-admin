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

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.SalaryMapper;
import top.continew.admin.education.mapper.TeacherMapper;
import top.continew.admin.education.model.entity.SalaryDO;
import top.continew.admin.education.model.entity.TeacherDO;
import top.continew.admin.education.model.query.SalaryQuery;
import top.continew.admin.education.model.req.SalaryReq;
import top.continew.admin.education.model.resp.SalaryDetailResp;
import top.continew.admin.education.model.resp.SalaryResp;
import top.continew.admin.education.service.SalaryService;
import top.continew.starter.core.exception.BusinessException;

import java.math.BigDecimal;

/**
 * 薪资业务实现
 *
 * @author don
 * @since 2025/05/13 22:43
 */
@Service
@RequiredArgsConstructor
public class SalaryServiceImpl extends BaseServiceImpl<SalaryMapper, SalaryDO, SalaryResp, SalaryDetailResp, SalaryQuery, SalaryReq> implements SalaryService {

    private final TeacherMapper teacherMapper;

    @Override
    protected void beforeCreate(SalaryReq req) {
        // 根据教师ID获取教师信息
        TeacherDO teacher = teacherMapper.selectById(req.getTeacherId());
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }

        // 获取教师的单价(rate)
        Integer rate = teacher.getRate();
        if (rate == null) {
            throw new BusinessException("教师单价未设置");
        }

        // 获取所属组和教师姓名
        String groupName = teacher.getGroupName();
        String teacherName = teacher.getName();

        // 计算课程总金额：单价 * 课程总数
        BigDecimal courseAmount = BigDecimal.valueOf(rate).multiply(BigDecimal.valueOf(req.getCourseCount()));

        // 设置扣款金额，如果为空则默认为0
        BigDecimal deductionAmount = req.getDeductionAmount();
        if (deductionAmount == null) {
            deductionAmount = BigDecimal.ZERO;
            req.setDeductionAmount(deductionAmount);
        }

        // 设置小费金额，如果为空则默认为0
        BigDecimal tipAmount = req.getTipAmount();
        if (tipAmount == null) {
            tipAmount = BigDecimal.ZERO;
            req.setTipAmount(tipAmount);
        }

        // 设置计算的值到请求对象中
        req.setCourseAmount(courseAmount);
        req.setGroupName(groupName);
        req.setTeacherName(teacherName);

        super.beforeCreate(req);
    }

    @Override
    protected void afterCreate(SalaryReq req, SalaryDO entity) {
        // 设置教师信息
        TeacherDO teacher = teacherMapper.selectById(req.getTeacherId());
        if (teacher != null) {
            // 设置教师单价
            if (teacher.getRate() != null) {
                entity.setRate(teacher.getRate());
            }

            // 确保groupName已设置
            if (entity.getGroupName() == null && teacher.getGroupName() != null) {
                entity.setGroupName(teacher.getGroupName());
            }
            entity.setTeacherName(teacher.getName());
        }

        // 获取当前实体的金额值，确保没有null值
        BigDecimal courseAmount = entity.getCourseAmount() != null ? entity.getCourseAmount() : BigDecimal.ZERO;

        BigDecimal deductionAmount = entity.getDeductionAmount() != null
            ? entity.getDeductionAmount()
            : BigDecimal.ZERO;

        BigDecimal tipAmount = entity.getTipAmount() != null ? entity.getTipAmount() : BigDecimal.ZERO;

        // 重新计算最终支付金额
        BigDecimal finalAmount = courseAmount.subtract(deductionAmount).add(tipAmount);
        entity.setFinalAmount(finalAmount);

        // 默认未结算状态
        entity.setStatus(0);

        // 更新实体以保存最终支付金额
        baseMapper.updateById(entity);

        super.afterCreate(req, entity);
    }
}