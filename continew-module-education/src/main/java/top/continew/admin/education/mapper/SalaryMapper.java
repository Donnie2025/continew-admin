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

package top.continew.admin.education.mapper;

import org.apache.ibatis.annotations.Param;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.education.model.entity.SalaryDO;

import java.util.List;
import java.util.Map;

/**
 * 薪资 Mapper
 *
 * @author don
 * @since 2025/05/13 22:43
 */
public interface SalaryMapper extends BaseMapper<SalaryDO> {

    /**
     * 查询约课数量趋势（按结束日期统计）
     *
     * @param weeks 查询最近几周的数据
     * @return 约课数量趋势(name: 结束日期, value: 约课数量)
     */
    List<Map<String, Object>> selectListCourseWeeklyTrend(@Param("weeks") Integer weeks);

    /**
     * 查询本周约课数量
     *
     * @return 本周约课总数
     */
    Long selectThisWeekCourseCount();

    /**
     * 查询上周约课数量
     *
     * @return 上周约课总数
     */
    Long selectLastWeekCourseCount();
}