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

package top.continew.admin.controller.schedule;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.common.log.SnailJobLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.continew.admin.education.service.SalaryJobService;

/**
 * 薪资处理任务
 *
 * @author Young
 * @since 2024/5/14 23:00
 */
@Component
@RequiredArgsConstructor
public class SalaryProcessingJob {

    private final SalaryJobService salaryJobService;

    /**
     * 自动结算未结算的薪资
     * 将状态为0（未结算）且开始日期在30天前的薪资记录自动标记为已结算
     */
    @JobExecutor(name = "AutoSettleSalary")
    public void autoSettleSalary() {
        try {
            SnailJobLog.REMOTE.info("定时任务 [自动结算薪资] 开始执行");

            // 调用education模块的业务逻辑处理
            int count = salaryJobService.autoSettleExpiredSalaries(30);

            SnailJobLog.REMOTE.info("定时任务 [自动结算薪资] 执行完成，成功结算{}条记录", count);
        } catch (Exception e) {
            SnailJobLog.REMOTE.error("自动结算薪资任务执行失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 薪资报表统计
     * 统计各教师的薪资情况并生成报表数据
     */
    @JobExecutor(name = "GenerateSalaryReport")
    public void generateSalaryReport() {
        try {
            SnailJobLog.REMOTE.info("定时任务 [生成薪资报表] 开始执行");

            // 调用education模块的业务逻辑处理
            salaryJobService.generateMonthlyReport();

            SnailJobLog.REMOTE.info("定时任务 [生成薪资报表] 执行完成");
        } catch (Exception e) {
            SnailJobLog.REMOTE.error("生成薪资报表任务执行失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 初始化每周教师薪资数据
     * 为符合条件的教师（status=1且group_name≠classin）初始化本周薪资记录
     */
    @JobExecutor(name = "InitializeWeeklySalary")
    public void initializeWeeklySalary() {
        try {
            SnailJobLog.REMOTE.info("定时任务 [初始化每周薪资数据] 开始执行");

            // 调用education模块的业务逻辑处理
            //            int count = salaryJobService.initializeWeeklySalaryData();
            //
            //            SnailJobLog.REMOTE.info("定时任务 [初始化每周薪资数据] 执行完成，成功创建{}条薪资记录", count);
        } catch (Exception e) {
            SnailJobLog.REMOTE.error("初始化每周薪资数据任务执行失败: {}", e.getMessage(), e);
            throw e;
        }
    }
}