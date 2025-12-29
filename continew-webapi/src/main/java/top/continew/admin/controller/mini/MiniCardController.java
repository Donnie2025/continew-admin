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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.common.context.UserContextHolder;
import top.continew.admin.education.model.resp.StuCardResp;
import top.continew.admin.education.service.StuCardService;
import top.continew.starter.web.model.R;

import java.util.Collections;
import java.util.List;

/**
 * 小程序会员卡 API
 *
 * @author don
 * @since 2025/11/23
 */
@Tag(name = "小程序会员卡 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mini/card")
public class MiniCardController {

    private final StuCardService stuCardService;

    @Operation(summary = "获取我的会员卡", description = "获取当前登录学生的会员卡列表")
    @SaIgnore
    @GetMapping("/my-cards")
    public R<List<StuCardResp>> getMyCards(@RequestParam(required = false) Long stuId) {
        try {
            // 如果没有传递stuId参数，尝试从上下文获取
            if (stuId == null) {
                stuId = UserContextHolder.getUserId();
            }
            
            // 如果还是null，使用默认测试用户ID
            if (stuId == null) {
                stuId = 1L; // 默认测试用户ID
            }
            
            List<StuCardResp> cards = stuCardService.getAvailableCards(stuId);
            return R.ok(cards);
        } catch (Exception e) {
            // 出现异常时返回空列表
            return R.ok(Collections.emptyList());
        }
    }
}
