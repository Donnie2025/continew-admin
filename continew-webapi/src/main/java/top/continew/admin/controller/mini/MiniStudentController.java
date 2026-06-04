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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.common.satoken.StpMiniUtil;
import top.continew.admin.education.model.resp.StudentStatsResp;
import top.continew.admin.education.service.StudentService;
import top.continew.starter.web.model.R;

/**
 * 小程序学生 API
 *
 * @author don
 * @since 2026/05
 */
@Slf4j
@Tag(name = "小程序学生 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mini/student")
public class MiniStudentController {

    private final StudentService studentService;

    @Operation(summary = "获取我的学习统计", description = "返回剩余/待上/已完成课程数 + 固定课数量 + 余额预警")
    @GetMapping("/stats")
    public R<StudentStatsResp> getMyStats() {
        try {
            Long stuId = resolveCurrentStudentId();
            StudentStatsResp resp = studentService.getStats(stuId);
            return R.ok(resp);
        } catch (Exception e) {
            log.error("获取学习统计失败: {}", e.getMessage());
            return R.fail("500", e.getMessage());
        }
    }

    /**
     * 从当前请求的 Authorization 头解析出学生ID（走 StpMiniUtil 的小程序 StpLogic）
     */
    private Long resolveCurrentStudentId() {
        String authHeader = SaManager.getSaTokenContext().getRequest().getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("用户未登录，请先登录");
        }
        String token = authHeader.substring(7);
        Object loginId = StpMiniUtil.getStpLogic().getLoginIdByToken(token);
        if (loginId == null) {
            throw new RuntimeException("用户未登录，请先登录");
        }
        return Long.valueOf(loginId.toString());
    }
}
