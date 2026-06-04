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

package top.continew.admin.controller.wechat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.education.model.req.MpMenuReq;
import top.continew.admin.education.model.resp.MpMenuResp;
import top.continew.admin.education.service.MpMenuService;

/**
 * 微信公众号菜单管理 API
 *
 * @author don
 * @since 2025/05/02
 */
@Tag(name = "微信公众号菜单管理 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/wechat/mpMenu")
public class MpMenuController {

    private final MpMenuService mpMenuService;

    @Operation(summary = "查询当前菜单", description = "从微信服务器获取当前已发布的公众号菜单")
    @GetMapping
    public MpMenuResp getMenu() {
        return mpMenuService.getMenu();
    }

    @Operation(summary = "创建/更新菜单", description = "覆盖式保存公众号自定义菜单，保存后立即生效")
    @PostMapping
    public void saveMenu(@Valid @RequestBody MpMenuReq req) {
        mpMenuService.saveMenu(req);
    }

    @Operation(summary = "删除菜单", description = "删除当前公众号所有自定义菜单")
    @DeleteMapping
    public void deleteMenu() {
        mpMenuService.deleteMenu();
    }
}
