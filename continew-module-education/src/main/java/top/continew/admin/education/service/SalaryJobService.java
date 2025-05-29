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

package top.continew.admin.education.service;

/**
 * 薪资任务服务接口
 *
 * @author Young
 * @since 2024/5/14 23:10
 */
public interface SalaryJobService {

    /**
     * 自动结算过期薪资
     * 将指定天数前的未结算薪资记录标记为已结算
     *
     * @param days 天数，如30表示30天前
     * @return 成功结算的记录数量
     */
    int autoSettleExpiredSalaries(int days);

    /**
     * 生成月度薪资报表
     * 统计当月各教师的薪资情况并生成报表数据
     */
    void generateMonthlyReport();

    /**
     * 初始化本周教师薪资数据
     * 为所有符合条件的老师（status为1且group_name不为classin）创建本周的薪资记录
     * 
     * @return 新创建的薪资记录数量
     */
    int initializeWeeklySalaryData();
}