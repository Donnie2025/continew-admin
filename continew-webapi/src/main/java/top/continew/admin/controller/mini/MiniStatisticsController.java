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

package top.continew.admin.controller.mini;

import cn.dev33.satoken.SaManager;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.common.satoken.StpMiniUtil;
import top.continew.admin.education.mapper.BookingMapper;
import top.continew.admin.education.mapper.SalaryMapper;
import top.continew.admin.education.model.entity.BookingDO;
import top.continew.admin.education.model.entity.SalaryDO;
import top.continew.starter.web.model.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Tag(name = "小程序统计接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mini/statistics")
public class MiniStatisticsController {

    private final SalaryMapper salaryMapper;
    private final BookingMapper bookingMapper;

    private static final DateTimeFormatter SLOT_DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Operation(summary = "获取收益统计", description = "按周/月/年统计教师收益和课节数")
    @GetMapping("/earnings")
    public R<?> getEarnings(@RequestParam(defaultValue = "week") String period,
                            @RequestParam(defaultValue = "0") int weekOffset) {
        try {
            String authHeader = SaManager.getSaTokenContext().getRequest().getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return R.fail("401", "请先登录");
            }
            String token = authHeader.substring(7);
            Object loginId = StpMiniUtil.getStpLogic().getLoginIdByToken(token);
            if (loginId == null) {
                return R.fail("401", "登录已过期，请重新登录");
            }
            Long teacherId = Long.valueOf(loginId.toString());

            LocalDate today = LocalDate.now();
            LocalDate startDate, endDate, prevStartDate, prevEndDate;

            switch (period) {
                case "month":
                    startDate = today.withDayOfMonth(1);
                    endDate = today.withDayOfMonth(today.lengthOfMonth());
                    prevStartDate = startDate.minusMonths(1);
                    prevEndDate = startDate.minusDays(1);
                    break;
                case "year":
                    startDate = today.withDayOfYear(1);
                    endDate = today.withDayOfYear(today.lengthOfYear());
                    prevStartDate = startDate.minusYears(1);
                    prevEndDate = startDate.minusDays(1);
                    break;
                default:
                    startDate = today.with(DayOfWeek.MONDAY).plusWeeks(weekOffset);
                    endDate = today.with(DayOfWeek.SUNDAY).plusWeeks(weekOffset);
                    prevStartDate = startDate.minusWeeks(1);
                    prevEndDate = startDate.minusDays(1);
            }

            // 查询 edu_booking 课节数
            String startStr = startDate.format(SLOT_DATE_FMT);
            String endStr = endDate.format(SLOT_DATE_FMT);
            List<BookingDO> bookings = bookingMapper.selectList(Wrappers.lambdaQuery(BookingDO.class)
                .eq(BookingDO::getTeacherId, teacherId)
                .ge(BookingDO::getSlotDate, startStr)
                .le(BookingDO::getSlotDate, endStr)
                .eq(BookingDO::getStatus, 1));

            // 查询 edu_salary 收益（与查询时间段有交集的薪资记录）
            List<SalaryDO> salaries = salaryMapper.selectList(Wrappers.lambdaQuery(SalaryDO.class)
                .eq(SalaryDO::getTeacherId, teacherId)
                .le(SalaryDO::getStartDate, endDate)
                .ge(SalaryDO::getEndDate, startDate)
                .eq(SalaryDO::getStatus, 1));

            List<SalaryDO> prevSalaries = salaryMapper.selectList(Wrappers.lambdaQuery(SalaryDO.class)
                .eq(SalaryDO::getTeacherId, teacherId)
                .le(SalaryDO::getStartDate, prevEndDate)
                .ge(SalaryDO::getEndDate, prevStartDate)
                .eq(SalaryDO::getStatus, 1));

            BigDecimal totalEarnings = salaries.stream()
                .map(SalaryDO::getFinalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal prevEarnings = prevSalaries.stream()
                .map(SalaryDO::getFinalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            double earningsChange = 0.0;
            if (prevEarnings.compareTo(BigDecimal.ZERO) != 0) {
                earningsChange = totalEarnings.subtract(prevEarnings)
                    .divide(prevEarnings, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(1, RoundingMode.HALF_UP)
                    .doubleValue();
            }

            // 唯一学生数
            long uniqueStudents = bookings.stream().map(BookingDO::getStudentId).distinct().count();

            // edu_salary.course_count 汇总
            int totalCourseCount = salaries.stream()
                .map(SalaryDO::getCourseCount)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

            // 构建图表 breakdown（周视图用 booking.slotDate，月/年视图用 salary.course_count）
            List<Map<String, Object>> breakdown = buildBreakdown(period, salaries, bookings, startDate);

            Map<String, Object> result = new HashMap<>();
            result.put("totalEarnings", totalEarnings);
            result.put("earningsChange", earningsChange);
            result.put("totalClasses", bookings.size());
            result.put("totalCourseCount", totalCourseCount);
            result.put("uniqueStudents", uniqueStudents);
            result.put("breakdown", breakdown);

            log.info("教师[{}]统计 period={} totalEarnings={} totalClasses={} totalCourseCount={}", teacherId, period, totalEarnings, bookings
                .size(), totalCourseCount);
            return R.ok(result);
        } catch (Exception e) {
            log.error("获取收益统计失败", e);
            return R.fail("500", e.getMessage());
        }
    }

    private List<Map<String, Object>> buildBreakdown(String period,
                                                     List<SalaryDO> salaries,
                                                     List<BookingDO> bookings,
                                                     LocalDate startDate) {
        List<Map<String, Object>> breakdown = new ArrayList<>();

        if ("week".equals(period)) {
            // 周视图：按 salary.endDate 放置 course_count，标签显示实际日期（英文）
            DayOfWeek[] days = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY};
            LocalDate monday = startDate.with(DayOfWeek.MONDAY);
            Map<String, Integer> byDay = new LinkedHashMap<>();
            for (DayOfWeek day : days) {
                byDay.put(monday.with(day).format(SLOT_DATE_FMT), 0);
            }
            for (SalaryDO s : salaries) {
                if (s.getEndDate() != null) {
                    String key = s.getEndDate().format(SLOT_DATE_FMT);
                    if (byDay.containsKey(key)) {
                        int cnt = s.getCourseCount() == null ? 0 : s.getCourseCount();
                        byDay.merge(key, cnt, Integer::sum);
                    }
                }
            }
            DateTimeFormatter labelFmt = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);
            for (DayOfWeek day : days) {
                LocalDate date = monday.with(day);
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("label", date.format(labelFmt));
                entry.put("classes", byDay.getOrDefault(date.format(SLOT_DATE_FMT), 0));
                breakdown.add(entry);
            }

        } else if ("month".equals(period)) {
            // 月视图：按 salary.endDate 分组（按日期排序），累加 course_count，标签显示英文 endDate
            Map<LocalDate, Integer> byEndDate = new LinkedHashMap<>();
            // 先按 endDate 排序收集所有 key
            salaries.stream()
                .filter(s -> s.getEndDate() != null)
                .map(SalaryDO::getEndDate)
                .distinct()
                .sorted()
                .forEach(d -> byEndDate.put(d, 0));
            for (SalaryDO s : salaries) {
                if (s.getEndDate() == null)
                    continue;
                int cnt = s.getCourseCount() == null ? 0 : s.getCourseCount();
                byEndDate.merge(s.getEndDate(), cnt, Integer::sum);
            }
            DateTimeFormatter labelFmt = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);
            for (Map.Entry<LocalDate, Integer> e : byEndDate.entrySet()) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("label", e.getKey().format(labelFmt));
                entry.put("classes", e.getValue());
                breakdown.add(entry);
            }

        } else {
            // 按 salary.startDate 所在月份，累加 course_count
            Map<Integer, Integer> byMonth = new LinkedHashMap<>();
            for (int m = 1; m <= 12; m++) {
                byMonth.put(m, 0);
            }
            for (SalaryDO s : salaries) {
                if (s.getEndDate() != null) {
                    int month = s.getEndDate().getMonthValue();
                    int cnt = s.getCourseCount() == null ? 0 : s.getCourseCount();
                    byMonth.merge(month, cnt, (a, b) -> a + b);
                }
            }
            String[] monthLabels = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            for (int m = 1; m <= 12; m++) {
                int cls = byMonth.getOrDefault(m, 0);
                if (cls > 0) {
                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("label", monthLabels[m - 1]);
                    entry.put("classes", cls);
                    breakdown.add(entry);
                }
            }
        }

        return breakdown;
    }
}
