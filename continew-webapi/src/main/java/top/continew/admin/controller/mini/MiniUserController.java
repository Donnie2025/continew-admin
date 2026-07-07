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

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.common.context.UserContext;
import top.continew.admin.common.context.UserContextHolder;
import top.continew.admin.common.satoken.StpMiniUtil;
import top.continew.admin.education.model.req.StudentUpdateReq;
import top.continew.admin.education.service.StudentService;

import java.util.Collections;

/**
 * 小程序用户信息 API
 *
 * @author don
 * @since 2025/06/14
 */
@Slf4j
@Tag(name = "小程序用户信息 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class MiniUserController {

    private final StudentService studentService;

    @Operation(summary = "更新用户信息", description = "更新学生昵称和头像")
    @PostMapping("/update")
    @SaIgnore
    public void updateUserInfo(@Validated @RequestBody StudentUpdateReq req) {
        // 验证小程序用户登录
        StpMiniUtil.checkLogin();

        // 获取用户ID并设置上下文
        Long studentId = StpMiniUtil.getLoginIdAsLong();
        UserContext userContext = new UserContext(Collections.emptySet(), Collections.emptySet(), -1);
        userContext.setId(studentId);
        UserContextHolder.setContext(userContext);

        log.info("更新用户信息请求: studentId={}, nickname={}", studentId, req.getNickname());
        studentService.updateStudentInfo(studentId, req);
    }
}
