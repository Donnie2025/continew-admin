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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.education.mapper.SalaryMapper;
import top.continew.admin.education.mapper.TeacherMapper;
import top.continew.admin.education.model.entity.SalaryDO;
import top.continew.admin.education.model.entity.TeacherDO;
import top.continew.admin.education.model.req.SalaryInitializeReq;
import top.continew.admin.education.service.SalaryJobService;
import top.continew.admin.education.util.SalaryCalculationUtil;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 薪资任务服务实现类
 *
 * @author Young
 * @since 2024/5/14 23:15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryJobServiceImpl implements SalaryJobService {

    private final SalaryMapper salaryMapper;
    private final TeacherMapper teacherMapper;

    /**
     * 自动结算过期薪资
     * 将指定天数前的未结算薪资记录标记为已结算
     *
     * @param days 天数，如30表示30天前
     * @return 成功结算的记录数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int autoSettleExpiredSalaries(int days) {
        // 计算指定天数前的日期
        LocalDate daysAgo = LocalDate.now().minusDays(days);
        String dateThreshold = daysAgo.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // 查询符合条件的未结算薪资记录（status=1生效，isSettled=0未结算）
        List<SalaryDO> pendingSalaries = salaryMapper.lambdaQuery()
            .eq(SalaryDO::getStatus, 1)  // 状态为生效
            .eq(SalaryDO::getIsSettled, 0)  // 未结算
            .le(SalaryDO::getStartDate, dateThreshold)
            .list();

        int count = 0;
        if (!pendingSalaries.isEmpty()) {
            log.info("找到{}条需要自动结算的薪资记录", pendingSalaries.size());

            // 更新薪资状态为已结算
            for (SalaryDO salary : pendingSalaries) {
                boolean updated = salaryMapper.lambdaUpdate()
                    .eq(SalaryDO::getId, salary.getId())
                    .set(SalaryDO::getIsSettled, 1)  // 设置为已结算
                    .update();

                if (updated) {
                    count++;
                    log.info("薪资记录[ID={}]已自动结算", salary.getId());
                }
            }
        } else {
            log.info("没有找到需要自动结算的薪资记录");
        }

        return count;
    }

    /**
     * 生成月度薪资报表
     * 统计当月各教师的薪资情况并生成报表数据
     */
    @Override
    public void generateMonthlyReport() {
        // 获取当月第一天
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        String monthStart = firstDayOfMonth.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // 获取当月最后一天
        LocalDate lastDayOfMonth = firstDayOfMonth.plusMonths(1).minusDays(1);
        String monthEnd = lastDayOfMonth.format(DateTimeFormatter.ISO_LOCAL_DATE);

        log.info("开始统计{}至{}期间的薪资数据", monthStart, monthEnd);

        // TODO: 实现实际的统计逻辑
        // 例如：按教师统计课程数、总金额等

        log.info("薪资报表生成完成");
    }

    /**
     * 初始化教师薪资数据
     * 为所有符合条件的老师（status为1且group_name不为classin）创建指定日期范围的薪资记录
     * 
     * @param req 薪资初始化请求（包含起始日期和结束日期，可选）
     * @return 新创建和更新的薪资记录数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int initializeWeeklySalaryData(SalaryInitializeReq req) {
        // 获取日期范围，如果未提供则使用本周
        LocalDate startOfWeek;
        LocalDate endOfWeek;
        
        if (req != null && req.getStartDate() != null && req.getEndDate() != null) {
            startOfWeek = req.getStartDate();
            endOfWeek = req.getEndDate();
        } else {
            // 默认为本周（周一到周日）
            LocalDate today = LocalDate.now();
            startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        }

        log.info("初始化{}至{}期间的教师薪资数据", startOfWeek, endOfWeek);

        // 1. 查询所有符合条件的教师（status为1且group_name不为Classin）
        List<TeacherDO> teachers = queryEligibleTeachers();
        if (teachers.isEmpty()) {
            log.info("没有找到符合条件的教师");
            return 0;
        }

        log.info("找到{}位符合条件的教师", teachers.size());

        // 2. 批量查询本周所有教师的薪资记录（一次查询，避免N+1问题）
        List<Long> teacherIds = teachers.stream().map(TeacherDO::getId).collect(Collectors.toList());

        Map<Long, SalaryDO> existingSalaryMap = salaryMapper.lambdaQuery()
            .in(SalaryDO::getTeacherId, teacherIds)
            .ge(SalaryDO::getStartDate, startOfWeek)
            .le(SalaryDO::getEndDate, endOfWeek)
            .list()
            .stream()
            .collect(Collectors.toMap(SalaryDO::getTeacherId, salary -> salary, (old, newVal) -> old));

        // 3. 分类处理：新建和更新
        List<SalaryDO> salariesToInsert = new ArrayList<>();
        List<SalaryDO> salariesToUpdate = new ArrayList<>();

        for (TeacherDO teacher : teachers) {
            SalaryDO existingSalary = existingSalaryMap.get(teacher.getId());

            if (existingSalary == null) {
                // 创建新的薪资记录
                SalaryDO newSalary = createNewSalary(teacher, startOfWeek, endOfWeek);
                salariesToInsert.add(newSalary);
                log.debug("为教师[{}]创建本周薪资记录", teacher.getName());
            } else {
                if (existingSalary.getIsSettled()==1){
                    // 已结算的
                    log.debug("教师[{}] 本周已结算，跳过", teacher.getName());
                    continue;
                }
                // 更新现有薪资记录（仅当有课程时）
                if (updateExistingSalary(existingSalary, teacher)) {
                    salariesToUpdate.add(existingSalary);
                    log.debug("教师[{}]本周薪资记录已更新", teacher.getName());
                }
            }
        }

        // 4. 批量执行数据库操作
        int createdCount = batchInsertSalaries(salariesToInsert);
        int updatedCount = batchUpdateSalaries(salariesToUpdate);

        log.info("薪资初始化完成：新建{}条，更新{}条", createdCount, updatedCount);
        return createdCount + updatedCount;
    }

    /**
     * 查询符合条件的教师
     * 条件：status为1且group_name不为"Classin"（包括null和空字符串）
     * 
     * @return 符合条件的教师列表
     */
    private List<TeacherDO> queryEligibleTeachers() {
        return teacherMapper.lambdaQuery()
            .eq(TeacherDO::getStatus, 1).eq(TeacherDO::getIsShow, 1)
            .and(wrapper -> wrapper.ne(TeacherDO::getGroupName, "Classin")
                .or()
                .isNull(TeacherDO::getGroupName)
                .or()
                .eq(TeacherDO::getGroupName, ""))
            .list();
    }

    /**
     * 创建新的薪资记录
     * 
     * @param teacher     教师信息
     * @param startOfWeek 本周开始日期
     * @param endOfWeek   本周结束日期
     * @return 新建的薪资记录
     */
    private SalaryDO createNewSalary(TeacherDO teacher, LocalDate startOfWeek, LocalDate endOfWeek) {
        SalaryDO salary = new SalaryDO();
        salary.setTeacherId(teacher.getId());
        salary.setTeacherName(teacher.getName());
        salary.setStartDate(startOfWeek);
        salary.setEndDate(endOfWeek);
        salary.setCourseCount(0);
        
        // 初始课程金额为0
        BigDecimal courseAmount = BigDecimal.ZERO;
        salary.setCourseAmount(courseAmount);
        salary.setDeductionAmount(BigDecimal.ZERO);
        
        // 自动计算小费金额（课程金额为0时，小费也为0）
        BigDecimal tipAmount = SalaryCalculationUtil.calculateTipAmount(courseAmount, teacher.getName(), teacher.getGroupName());
        salary.setTipAmount(tipAmount);
        
        // 最终金额 = 课程金额 - 扣款金额 + 小费金额
        salary.setFinalAmount(courseAmount.add(tipAmount));
        
        salary.setStatus(1); // 生效
        salary.setIsSettled(0); // 未结算
        salary.setRate(teacher.getRate());
        salary.setGroupName(teacher.getGroupName());
        salary.setCreateUser(1L); // 系统默认用户ID
        return salary;
    }

    /**
     * 更新现有薪资记录
     * 仅当课程数量大于0时才更新金额
     * 
     * @param salary  现有薪资记录
     * @param teacher 教师信息
     * @return 是否需要更新
     */
    private boolean updateExistingSalary(SalaryDO salary, TeacherDO teacher) {
        Integer courseCount = salary.getCourseCount();

        // 如果没有课程，不需要更新
        if (courseCount == null || courseCount == 0) {
            return false;
        }

        // 计算薪资金额
        BigDecimal courseAmount = calculateCourseAmount(teacher.getRate(), courseCount);
        
        // 自动重新计算小费金额
        BigDecimal tipAmount = SalaryCalculationUtil.calculateTipAmount(courseAmount, teacher.getName(), teacher.getGroupName());
        salary.setTipAmount(tipAmount);
        
        BigDecimal finalAmount = calculateFinalAmount(courseAmount, salary.getDeductionAmount(), tipAmount);

        // 更新金额
        salary.setCourseAmount(courseAmount);
        salary.setFinalAmount(finalAmount);

        return true;
    }

    /**
     * 计算课程金额
     * 
     * @param rate        教师费率
     * @param courseCount 课程数量
     * @return 课程金额
     */
    private BigDecimal calculateCourseAmount(Integer rate, Integer courseCount) {
        if (rate == null || courseCount == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(rate).multiply(BigDecimal.valueOf(courseCount));
    }

    /**
     * 计算最终金额
     * 
     * @param courseAmount    课程金额
     * @param deductionAmount 扣款金额
     * @param tipAmount       小费金额
     * @return 最终金额 = 课程金额 - 扣款金额 + 小费金额
     */
    private BigDecimal calculateFinalAmount(BigDecimal courseAmount, BigDecimal deductionAmount, BigDecimal tipAmount) {
        BigDecimal result = courseAmount;
        if (deductionAmount != null) {
            result = result.subtract(deductionAmount);
        }
        if (tipAmount != null) {
            result = result.add(tipAmount);  // 小费是加到最终金额中
        }
        return result;
    }

    /**
     * 批量插入薪资记录
     * 
     * @param salaries 待插入的薪资记录列表
     * @return 实际插入的数量
     */
    private int batchInsertSalaries(List<SalaryDO> salaries) {
        if (salaries.isEmpty()) {
            return 0;
        }

        // 使用循环插入而非saveBatch，以确保每条记录的插入结果可控
        int count = 0;
        for (SalaryDO salary : salaries) {
            if (salaryMapper.insert(salary) > 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * 批量更新薪资记录
     * 
     * @param salaries 待更新的薪资记录列表
     * @return 实际更新的数量
     */
    private int batchUpdateSalaries(List<SalaryDO> salaries) {
        if (salaries.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (SalaryDO salary : salaries) {
            if (salaryMapper.updateById(salary) > 0) {
                count++;
            }
        }
        return count;
    }
}