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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.education.service.FavoriteService;
import top.continew.starter.web.model.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序收藏 API
 *
 * @author donnie
 * @since 2025/06/10
 */
@Tag(name = "小程序收藏 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mini/favorite")
public class MiniFavoriteController {

    private static final Logger log = LoggerFactory.getLogger(MiniFavoriteController.class);

    private final FavoriteService favoriteService;

    @PostMapping("/toggle")
    @Operation(summary = "切换收藏状态", description = "如果已收藏则取消，未收藏则添加")
    public R<Map<String, Object>> toggle(@RequestBody Map<String, Object> request) {
        try {
            Long studentId = Long.valueOf(request.get("studentId").toString());
            String resourceType = request.get("resourceType").toString();
            Long resourceId = Long.valueOf(request.get("resourceId").toString());
            String resourceName = request.getOrDefault("resourceName", "").toString();

            boolean isFavorited = favoriteService.toggle(studentId, resourceType, resourceId, resourceName);

            Map<String, Object> result = new HashMap<>();
            result.put("isFavorited", isFavorited);
            result.put("message", isFavorited ? "收藏成功" : "取消收藏");

            return R.ok(result);

        } catch (Exception e) {
            log.error("切换收藏状态失败", e);
            return R.fail("500", "操作失败");
        }
    }

    @PostMapping("/add")
    @Operation(summary = "添加收藏", description = "收藏教师或教材")
    public R<String> add(@RequestBody Map<String, Object> request) {
        try {
            Long studentId = Long.valueOf(request.get("studentId").toString());
            String resourceType = request.get("resourceType").toString();
            Long resourceId = Long.valueOf(request.get("resourceId").toString());
            String resourceName = request.getOrDefault("resourceName", "").toString();

            boolean success = favoriteService.favorite(studentId, resourceType, resourceId, resourceName);

            if (success) {
                return R.ok("收藏成功");
            } else {
                return R.fail("500", "收藏失败");
            }

        } catch (Exception e) {
            log.error("添加收藏失败", e);
            return R.fail("500", "收藏失败");
        }
    }

    @PostMapping("/remove")
    @Operation(summary = "取消收藏", description = "取消收藏教师或教材")
    public R<String> remove(@RequestBody Map<String, Object> request) {
        try {
            Long studentId = Long.valueOf(request.get("studentId").toString());
            String resourceType = request.get("resourceType").toString();
            Long resourceId = Long.valueOf(request.get("resourceId").toString());

            boolean success = favoriteService.unfavorite(studentId, resourceType, resourceId);

            if (success) {
                return R.ok("取消收藏成功");
            } else {
                return R.fail("500", "取消收藏失败");
            }

        } catch (Exception e) {
            log.error("取消收藏失败", e);
            return R.fail("500", "取消收藏失败");
        }
    }

    @GetMapping("/check")
    @Operation(summary = "检查收藏状态", description = "检查是否已收藏某个资源")
    public R<Map<String, Object>> check(@RequestParam Long studentId,
                                        @RequestParam String resourceType,
                                        @RequestParam Long resourceId) {
        try {
            boolean isFavorited = favoriteService.isFavorited(studentId, resourceType, resourceId);

            Map<String, Object> result = new HashMap<>();
            result.put("isFavorited", isFavorited);

            return R.ok(result);

        } catch (Exception e) {
            log.error("检查收藏状态失败", e);
            return R.fail("500", "查询失败");
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取收藏列表", description = "获取学生收藏的资源ID列表")
    public R<List<Long>> list(@RequestParam Long studentId, @RequestParam String resourceType) {
        try {
            List<Long> favoriteIds = favoriteService.getFavoriteResourceIds(studentId, resourceType);
            return R.ok(favoriteIds);

        } catch (Exception e) {
            log.error("获取收藏列表失败", e);
            return R.fail("500", "查询失败");
        }
    }

    @PostMapping("/batch/check")
    @Operation(summary = "批量检查收藏状态", description = "批量检查多个资源的收藏状态")
    public R<Map<Long, Boolean>> batchCheck(@RequestBody Map<String, Object> request) {
        try {
            Long studentId = Long.valueOf(request.get("studentId").toString());
            String resourceType = request.get("resourceType").toString();
            @SuppressWarnings("unchecked") List<Long> resourceIds = (List<Long>)request.get("resourceIds");

            Map<Long, Boolean> result = new HashMap<>();
            for (Long resourceId : resourceIds) {
                boolean isFavorited = favoriteService.isFavorited(studentId, resourceType, resourceId);
                result.put(resourceId, isFavorited);
            }

            return R.ok(result);

        } catch (Exception e) {
            log.error("批量检查收藏状态失败", e);
            return R.fail("500", "查询失败");
        }
    }
}
