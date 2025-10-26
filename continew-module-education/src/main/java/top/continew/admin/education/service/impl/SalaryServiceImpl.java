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

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.SalaryMapper;
import top.continew.admin.education.mapper.TeacherMapper;
import top.continew.admin.education.model.entity.SalaryDO;
import top.continew.admin.education.model.entity.TeacherDO;
import top.continew.admin.education.model.query.SalaryQuery;
import top.continew.admin.education.model.req.SalaryBatchImportReq;
import top.continew.admin.education.model.req.SalaryReq;
import top.continew.admin.education.model.resp.SalaryBatchImportResp;
import top.continew.admin.education.model.resp.SalaryDetailResp;
import top.continew.admin.education.model.resp.SalaryResp;
import top.continew.admin.education.service.SalaryService;
import top.continew.admin.education.util.SalaryCalculationUtil;
import top.continew.starter.core.exception.BusinessException;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 薪资业务实现
 *
 * @author don
 * @since 2025/05/13 22:43
 */
@Slf4j
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

        // 小费将在 afterCreate 中根据课程总金额自动计算
        // 这里不再设置默认值

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

        // 自动计算小费金额
        BigDecimal tipAmount = SalaryCalculationUtil.calculateTipAmount(courseAmount, entity.getTeacherName(), entity
            .getGroupName());
        entity.setTipAmount(tipAmount);

        // 重新计算最终支付金额
        BigDecimal finalAmount = courseAmount.subtract(deductionAmount).add(tipAmount);
        entity.setFinalAmount(finalAmount);

        // 设置默认状态：生效且未结算
        entity.setStatus(1); // 生效
        entity.setIsSettled(0); // 未结算

        // 更新实体以保存最终支付金额
        baseMapper.updateById(entity);

        super.afterCreate(req, entity);
    }

    @Override
    protected void beforeUpdate(SalaryReq req, Long id) {
        // 获取教师信息
        TeacherDO teacher = teacherMapper.selectById(req.getTeacherId());
        if (teacher != null) {
            // 设置教师单价
            if (req.getCourseCount() != null && teacher.getRate() != null) {
                req.setRate(teacher.getRate());
                // 重新计算课程总金额
                BigDecimal courseAmount = BigDecimal.valueOf(teacher.getRate())
                    .multiply(BigDecimal.valueOf(req.getCourseCount()));
                req.setCourseAmount(courseAmount);
            }

            // 设置教师姓名和所属组
            req.setTeacherName(teacher.getName());
            if (req.getGroupName() == null) {
                req.setGroupName(teacher.getGroupName());
            }
        }

        // 确保扣款金额不为空
        if (req.getDeductionAmount() == null) {
            req.setDeductionAmount(BigDecimal.ZERO);
        }

        // 小费将在 afterUpdate 中根据课程总金额自动重新计算

        super.beforeUpdate(req, id);
    }

    @Override
    protected void afterUpdate(SalaryReq req, SalaryDO entity) {
        // 重新计算最终支付金额
        BigDecimal courseAmount = entity.getCourseAmount() != null ? entity.getCourseAmount() : BigDecimal.ZERO;
        BigDecimal deductionAmount = entity.getDeductionAmount() != null
            ? entity.getDeductionAmount()
            : BigDecimal.ZERO;

        // 自动计算小费金额
        BigDecimal tipAmount = SalaryCalculationUtil.calculateTipAmount(courseAmount, entity.getTeacherName(), entity
            .getGroupName());
        entity.setTipAmount(tipAmount);

        BigDecimal finalAmount = courseAmount.subtract(deductionAmount).add(tipAmount);

        entity.setFinalAmount(finalAmount);
        baseMapper.updateById(entity);

        super.afterUpdate(req, entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SalaryBatchImportResp batchImport(SalaryBatchImportReq req) {
        // 获取日期范围（默认为本周）
        LocalDate startDate = req.getStartDate();
        LocalDate endDate = req.getEndDate();
        if (startDate == null || endDate == null) {
            LocalDate today = LocalDate.now();
            startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            endDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        }

        log.info("开始批量导入薪资数据，日期范围：{} 至 {}", startDate, endDate);

        // 解析导入数据
        String[] lines = req.getImportData().split("\n");
        List<SalaryBatchImportResp.ImportFailureDetail> failures = new ArrayList<>();
        int successCount = 0;

        // 查询所有教师，构建名称到教师的映射
        List<TeacherDO> allTeachers = teacherMapper.selectList(null);
        Map<String, TeacherDO> teacherMap = allTeachers.stream()
            .collect(Collectors.toMap(TeacherDO::getName, t -> t, (t1, t2) -> t1));

        // 查询本周期所有薪资记录
        LocalDate finalStartDate = startDate;
        LocalDate finalEndDate = endDate;
        Map<Long, SalaryDO> existingSalaryMap = baseMapper.lambdaQuery()
            .ge(SalaryDO::getStartDate, startDate)
            .le(SalaryDO::getEndDate, endDate)
            .list()
            .stream()
            .collect(Collectors.toMap(SalaryDO::getTeacherId, s -> s, (s1, s2) -> s1));
        // 逐行处理
        for (String line : lines) {
            if (StrUtil.isBlank(line)) {
                continue;
            }

            try {
                // 解析每行数据（格式：教师名\t课程数量）
                String[] parts = line.trim().split("\t");
                if (parts.length < 2) {
                    failures.add(SalaryBatchImportResp.ImportFailureDetail.builder()
                        .teacherName(line.trim())
                        .reason("数据格式错误，应为：教师名[Tab]课程数量")
                        .build());
                    continue;
                }

                String teacherName = parts[0].trim();
                Integer courseCount;
                try {
                    courseCount = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    failures.add(SalaryBatchImportResp.ImportFailureDetail.builder()
                        .teacherName(teacherName)
                        .reason("课程数量格式错误：" + parts[1])
                        .build());
                    continue;
                }

                // 查找教师
                TeacherDO teacher = teacherMap.get(teacherName);
                if (teacher == null) {
                    failures.add(SalaryBatchImportResp.ImportFailureDetail.builder()
                        .teacherName(teacherName)
                        .courseCount(courseCount)
                        .reason("系统中未找到该教师")
                        .build());
                    continue;
                }

                // 检查教师单价
                if (teacher.getRate() == null) {
                    failures.add(SalaryBatchImportResp.ImportFailureDetail.builder()
                        .teacherName(teacherName)
                        .courseCount(courseCount)
                        .reason("教师单价未设置")
                        .build());
                    continue;
                }

                // 查找或创建薪资记录
                SalaryDO salary = existingSalaryMap.get(teacher.getId());
                if (salary == null) {
                    // 创建新的薪资记录
                    salary = createNewSalary(teacher, finalStartDate, finalEndDate, courseCount);
                    baseMapper.insert(salary);
                    existingSalaryMap.put(teacher.getId(), salary);
                } else {
                    // 检查是否已结算
                    if (salary.getIsSettled() != null && salary.getIsSettled() == 1) {
                        failures.add(SalaryBatchImportResp.ImportFailureDetail.builder()
                            .teacherName(teacherName)
                            .courseCount(courseCount)
                            .reason("该教师本周期薪资已结算，无法修改")
                            .build());
                        continue;
                    }
                    // 更新现有薪资记录
                    updateSalaryCourseCount(salary, teacher, courseCount);
                    baseMapper.updateById(salary);
                }

                successCount++;
                log.debug("成功导入教师[{}]的课程数据，课程数：{}", teacherName, courseCount);

            } catch (Exception e) {
                log.error("导入数据出错：{}", line, e);
                failures.add(SalaryBatchImportResp.ImportFailureDetail.builder()
                    .teacherName(line.substring(0, Math.min(line.length(), 50)))
                    .reason("处理异常：" + e.getMessage())
                    .build());
            }
        }

        log.info("批量导入完成，成功：{}，失败：{}", successCount, failures.size());

        return SalaryBatchImportResp.builder()
            .successCount(successCount)
            .failureCount(failures.size())
            .failures(failures)
            .build();
    }

    /**
     * 创建新的薪资记录
     */
    private SalaryDO createNewSalary(TeacherDO teacher, LocalDate startDate, LocalDate endDate, Integer courseCount) {
        SalaryDO salary = new SalaryDO();
        salary.setTeacherId(teacher.getId());
        salary.setTeacherName(teacher.getName());
        salary.setStartDate(startDate);
        salary.setEndDate(endDate);
        salary.setCourseCount(courseCount);
        salary.setRate(teacher.getRate());
        salary.setGroupName(teacher.getGroupName());

        // 计算金额
        BigDecimal courseAmount = BigDecimal.valueOf(teacher.getRate()).multiply(BigDecimal.valueOf(courseCount));
        salary.setCourseAmount(courseAmount);
        salary.setDeductionAmount(BigDecimal.ZERO);

        // 自动计算小费金额
        BigDecimal tipAmount = SalaryCalculationUtil.calculateTipAmount(courseAmount, teacher.getName(), teacher
            .getGroupName());
        salary.setTipAmount(tipAmount);

        // 计算最终金额
        BigDecimal finalAmount = courseAmount.add(tipAmount);
        salary.setFinalAmount(finalAmount);

        salary.setStatus(1); // 生效
        salary.setIsSettled(0); // 未结算

        return salary;
    }

    /**
     * 更新薪资记录的课程数量
     */
    private void updateSalaryCourseCount(SalaryDO salary, TeacherDO teacher, Integer courseCount) {
        salary.setCourseCount(courseCount);

        // 重新计算金额
        BigDecimal courseAmount = BigDecimal.valueOf(teacher.getRate()).multiply(BigDecimal.valueOf(courseCount));
        salary.setCourseAmount(courseAmount);

        BigDecimal deductionAmount = salary.getDeductionAmount() != null
            ? salary.getDeductionAmount()
            : BigDecimal.ZERO;

        // 自动重新计算小费金额
        BigDecimal tipAmount = SalaryCalculationUtil.calculateTipAmount(courseAmount, teacher.getName(), teacher
            .getGroupName());
        salary.setTipAmount(tipAmount);

        BigDecimal finalAmount = courseAmount.subtract(deductionAmount).add(tipAmount);
        salary.setFinalAmount(finalAmount);
    }
}