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
import top.continew.admin.education.service.SalaryJobService;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

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

        // 查询符合条件的未结算薪资记录
        List<SalaryDO> pendingSalaries = salaryMapper.lambdaQuery()
            .eq(SalaryDO::getStatus, 0)
            .le(SalaryDO::getStartDate, dateThreshold)
            .list();

        int count = 0;
        if (!pendingSalaries.isEmpty()) {
            log.info("找到{}条需要自动结算的薪资记录", pendingSalaries.size());

            // 更新薪资状态为已结算
            for (SalaryDO salary : pendingSalaries) {
                boolean updated = salaryMapper.lambdaUpdate()
                    .eq(SalaryDO::getId, salary.getId())
                    .set(SalaryDO::getStatus, 1)
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
     * 初始化本周教师薪资数据
     * 为所有符合条件的老师（status为1且group_name不为classin）创建本周的薪资记录
     * 
     * @return 新创建的薪资记录数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int initializeWeeklySalaryData() {
        // 获取本周的起始日期和结束日期（周一到周日）
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        log.info("初始化{}至{}期间的教师薪资数据", startOfWeek, endOfWeek);

        // 查询所有符合条件的教师（status为1且group_name不为classin）
        List<TeacherDO> teachers = teacherMapper.lambdaQuery()
            .eq(TeacherDO::getStatus, 1)
            .and(wrapper -> wrapper.ne(TeacherDO::getGroupName, "Classin")
                .or()
                .isNull(TeacherDO::getGroupName)
                .or()
                .eq(TeacherDO::getGroupName, ""))
            .list();

        if (teachers.isEmpty()) {
            log.info("没有找到符合条件的教师");
            return 0;
        }

        log.info("找到{}位符合条件的教师", teachers.size());

        int createdCount = 0;
        int updateCount = 0;
        List<SalaryDO> salariesToInsert = new ArrayList<>();
        List<SalaryDO> salariesToUpdate = new ArrayList<>();

        // 为每个教师检查是否已有本周的薪资记录，如果没有则创建一条
        for (TeacherDO teacher : teachers) {
            // 检查该教师在本周是否已有薪资记录
            List<SalaryDO> salaryDOList = salaryMapper.lambdaQuery()
                    .eq(SalaryDO::getTeacherId, teacher.getId())
                    .ge(SalaryDO::getStartDate, startOfWeek)
                    .le(SalaryDO::getEndDate, endOfWeek)
                    .list();
            long existingCount = salaryDOList.size();

            if (existingCount == 0) {
                // 创建新的薪资记录
                SalaryDO salary = new SalaryDO();
                salary.setTeacherId(teacher.getId());
                salary.setTeacherName(teacher.getName());
                salary.setStartDate(startOfWeek);
                salary.setEndDate(endOfWeek);
                salary.setCourseCount(0);
                salary.setCourseAmount(BigDecimal.ZERO);
                salary.setDeductionAmount(BigDecimal.ZERO);
                salary.setTipAmount(BigDecimal.ZERO);
                salary.setFinalAmount(BigDecimal.ZERO);
                salary.setStatus(0); // 未结算
                salary.setRate(teacher.getRate());
                salary.setGroupName(teacher.getGroupName());

                // 设置系统默认用户ID为创建者(ID=1通常是系统管理员)
                salary.setCreateUser(1L);

                salariesToInsert.add(salary);
                log.info("为教师[{}]创建本周薪资记录", teacher.getName());
            } else {
                SalaryDO salaryDO = salaryDOList.get(0);
                Integer courseCountInt = salaryDO.getCourseCount();
                if (courseCountInt==0){
                    continue;
                }
                BigDecimal teacherRate = BigDecimal.valueOf(teacher.getRate());
                BigDecimal courseCount = BigDecimal.valueOf(courseCountInt);
                BigDecimal courseAmt = teacherRate.multiply(courseCount);
                BigDecimal finalAmt = courseAmt.subtract(salaryDO.getDeductionAmount()).subtract(salaryDO.getTipAmount());

                salaryDO.setCourseAmount(courseAmt);
                salaryDO.setFinalAmount(finalAmt);
                salariesToUpdate.add(salaryDO);
                log.info("教师[{}] 更新本周薪资记录成功", teacher.getName());
            }
        }

        // 批量插入新创建的薪资记录
        if (!salariesToInsert.isEmpty()) {
            for (SalaryDO salary : salariesToInsert) {
                salaryMapper.insert(salary);
                createdCount++;
            }
        }

        // 批量修改
        if (!salariesToUpdate.isEmpty()) {
            for (SalaryDO salary : salariesToUpdate) {
                salaryMapper.updateById(salary);
                updateCount++;
            }
        }

        log.info("成功创建{}条薪资记录", createdCount);
        log.info("成功修改{}条薪资记录", updateCount);
        return createdCount+updateCount;
    }
}