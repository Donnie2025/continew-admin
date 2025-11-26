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

package top.continew.admin.system.service;

import top.continew.admin.system.model.resp.dashboard.DashboardAccessTrendResp;
import top.continew.admin.system.model.resp.dashboard.DashboardChartCommonResp;
import top.continew.admin.system.model.resp.dashboard.DashboardNoticeResp;
import top.continew.admin.system.model.resp.dashboard.DashboardOverviewCommonResp;

import java.io.IOException;
import java.util.List;

/**
 * 仪表盘业务接口
 *
 * @author Charles7c
 * @since 2023/9/8 21:32
 */
public interface DashboardService {

    /**
     * 查询公告列表
     *
     * @return 公告列表
     */
    List<DashboardNoticeResp> listNotice();

    /**
     * 查询 PV 总览
     *
     * @return PV 总览
     */
    DashboardOverviewCommonResp getOverviewPv();

    /**
     * 查询 IP 总览
     *
     * @return IP 总览
     */
    DashboardOverviewCommonResp getOverviewIp();

    /**
     * 查询地域分析信息
     *
     * @return 地域分析信息
     * @throws IOException /
     */
    List<DashboardChartCommonResp> getAnalysisGeo() throws IOException;

    /**
     * 查询访问趋势信息
     *
     * @param days 日期数
     * @return 访问趋势信息
     */
    List<DashboardAccessTrendResp> listAccessTrend(Integer days);

    /**
     * 查询访问时段分析信息
     *
     * @return 访问时段分析信息
     */
    List<DashboardChartCommonResp> getAnalysisTimeslot();

    /**
     * 查询模块分析信息
     *
     * @return 模块分析信息
     */
    List<DashboardChartCommonResp> getAnalysisModule();

    /**
     * 查询终端分析信息
     *
     * @return 终端分析信息
     */
    List<DashboardChartCommonResp> getAnalysisOs();

    /**
     * 查询浏览器分析信息
     *
     * @return 浏览器分析信息
     */
    List<DashboardChartCommonResp> getAnalysisBrowser();

    /**
     * 查询约课数量趋势（按结束日期统计）
     *
     * @param weeks 查询最近几周的数据
     * @return 约课数量趋势
     */
    List<DashboardChartCommonResp> listCourseWeeklyTrend(Integer weeks);

    /**
     * 查询本周约课总览
     *
     * @return 本周约课数量及较上周的变化
     */
    DashboardOverviewCommonResp getOverviewWeeklyCourse();
}
