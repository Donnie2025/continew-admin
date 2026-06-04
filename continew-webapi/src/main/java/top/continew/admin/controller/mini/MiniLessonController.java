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
import top.continew.admin.education.mapper.LessonMapper;
import top.continew.admin.education.mapper.MaterialMapper;
import top.continew.admin.education.model.entity.LessonDO;
import top.continew.admin.education.model.entity.MaterialDO;
import top.continew.admin.education.model.resp.MiniLessonBookingResp;
import top.continew.starter.web.model.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 小程序课节 API
 *
 * @author don
 * @since 2025/04/18
 */
@Slf4j
@Tag(name = "小程序课节 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mini/lesson")
public class MiniLessonController {

    private final LessonMapper lessonMapper;
    private final MaterialMapper materialMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Operation(summary = "获取我的预约列表", description = "获取当前登录教师名下的课节预约（分页），按时间排列")
    @GetMapping("/bookings")
    public R<?> getBookings(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) String status) {
        try {
            // 从 Authorization 头获取当前登录教师 ID
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
            log.info("查询教师[{}]的课节预约列表 page={} size={} status={}", teacherId, page, size, status);

            // 查询该教师名下所有课节（仅展示 status=1 启用的课节）
            List<LessonDO> lessons = lessonMapper.selectList(Wrappers.lambdaQuery(LessonDO.class)
                .eq(LessonDO::getTeacherId, teacherId)
                .eq(LessonDO::getStatus, 1));

            LocalDateTime now = LocalDateTime.now();

            // 排序：upcoming（startTime >= now）升序在前，completed 降序在后
            lessons.sort((a, b) -> {
                boolean aUpcoming = a.getStartTime() != null && !a.getStartTime().isBefore(now);
                boolean bUpcoming = b.getStartTime() != null && !b.getStartTime().isBefore(now);
                if (aUpcoming != bUpcoming) {
                    return aUpcoming ? -1 : 1;
                }
                if (a.getStartTime() == null && b.getStartTime() == null)
                    return 0;
                if (a.getStartTime() == null)
                    return 1;
                if (b.getStartTime() == null)
                    return -1;
                return aUpcoming
                    ? a.getStartTime().compareTo(b.getStartTime())
                    : b.getStartTime().compareTo(a.getStartTime());
            });

            // 预查询所有相关教材，构建 materialId -> textbookName 映射
            Map<Long, String> textbookNameMap = buildTextbookNameMap(lessons);

            // 映射全部记录，计算 status
            List<MiniLessonBookingResp> allMapped = lessons.stream().map(lesson -> {
                MiniLessonBookingResp resp = new MiniLessonBookingResp();
                resp.setId(lesson.getId());
                resp.setName(lesson.getName());
                resp.setMaterialName(lesson.getMaterialName());
                resp.setTextbookName(textbookNameMap.get(lesson.getMaterialId()));
                resp.setRemark(lesson.getRemark());

                if (lesson.getStartTime() != null) {
                    resp.setDate(lesson.getStartTime().format(DATE_FORMATTER));
                    resp.setStartTime(lesson.getStartTime().format(TIME_FORMATTER));

                    long durationMinutes = lesson.getDuration() != null ? lesson.getDuration() : 0L;
                    LocalDateTime endDateTime = lesson.getStartTime().plusMinutes(durationMinutes);
                    resp.setEndTime(endDateTime.format(TIME_FORMATTER));

                    resp.setStatus(endDateTime.isBefore(now) ? "completed" : "upcoming");
                }
                return resp;
            }).collect(Collectors.toList());

            // 统计总数（不受 status 过滤影响）
            long upcomingCount = allMapped.stream().filter(r -> "upcoming".equals(r.getStatus())).count();
            long completedCount = allMapped.stream().filter(r -> "completed".equals(r.getStatus())).count();

            // 按 status 过滤
            List<MiniLessonBookingResp> filtered = allMapped;
            if (status != null && !status.isEmpty()) {
                filtered = allMapped.stream().filter(r -> status.equals(r.getStatus())).collect(Collectors.toList());
            }

            // 分页
            long total = filtered.size();
            int fromIndex = (page - 1) * size;
            List<MiniLessonBookingResp> pageList;
            if (fromIndex >= filtered.size()) {
                pageList = Collections.emptyList();
            } else {
                pageList = filtered.subList(fromIndex, Math.min(fromIndex + size, filtered.size()));
            }
            boolean hasMore = (long)fromIndex + size < total;

            log.info("返回 {} 条，总计 {} 条，hasMore={}", pageList.size(), total, hasMore);

            Map<String, Object> result = new HashMap<>();
            result.put("list", pageList);
            result.put("total", total);
            result.put("hasMore", hasMore);
            result.put("upcomingCount", upcomingCount);
            result.put("completedCount", completedCount);

            return R.ok(result);
        } catch (Exception e) {
            log.error("获取预约列表失败", e);
            return R.fail("500", e.getMessage());
        }
    }

    /**
     * 构建 materialId -> textbookName（BOOK层级）的映射
     */
    private Map<Long, String> buildTextbookNameMap(List<LessonDO> lessons) {
        List<Long> materialIds = lessons.stream()
            .map(LessonDO::getMaterialId)
            .filter(id -> id != null)
            .distinct()
            .collect(Collectors.toList());
        if (materialIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 批量加载所有涉及的节点（含父节点），使用迭代向上追溯到 BOOK 层
        Map<Long, MaterialDO> nodeCache = new HashMap<>();
        List<Long> toLoad = new java.util.ArrayList<>(materialIds);
        // 最多向上追溯 5 层（LESSON→UNIT→LEVEL→BOOK→CATEGORY）
        for (int i = 0; i < 5 && !toLoad.isEmpty(); i++) {
            List<MaterialDO> batch = materialMapper.selectBatchIds(toLoad);
            batch.forEach(m -> nodeCache.put(m.getId(), m));
            toLoad = batch.stream()
                .filter(m -> m.getPid() != null && m.getPid() != 0 && !nodeCache.containsKey(m.getPid()))
                .map(MaterialDO::getPid)
                .distinct()
                .collect(Collectors.toList());
        }

        Map<Long, String> result = new HashMap<>();
        for (Long materialId : materialIds) {
            String bookName = findBookAncestor(materialId, nodeCache);
            if (bookName != null) {
                result.put(materialId, bookName);
            }
        }
        return result;
    }

    /**
     * 从缓存中向上追溯，找到 BOOK 层级节点名称
     */
    private String findBookAncestor(Long nodeId, Map<Long, MaterialDO> cache) {
        MaterialDO node = cache.get(nodeId);
        while (node != null) {
            if ("BOOK".equals(node.getType())) {
                return node.getName();
            }
            Long pid = node.getPid();
            if (pid == null || pid == 0)
                break;
            node = cache.get(pid);
        }
        return null;
    }
}
