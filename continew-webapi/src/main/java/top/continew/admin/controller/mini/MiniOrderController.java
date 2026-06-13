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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.common.satoken.StpMiniUtil;
import top.continew.admin.common.context.UserContextHolder;
import top.continew.admin.common.context.UserContext;
import top.continew.admin.education.model.req.OrderReq;
import top.continew.admin.education.model.resp.OrderDetailResp;
import top.continew.admin.education.service.OrderService;
import top.continew.starter.web.model.R;

import java.util.Collections;

/**
 * 小程序订单 API
 *
 * @author don
 * @since 2025/11/23
 */
@Tag(name = "小程序订单 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mini/order")
public class MiniOrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单", description = "用户购买会员卡时创建订单")
    @PostMapping("/create")
    public R<OrderDetailResp> createOrder(@Validated @RequestBody OrderReq req) {
        // 验证小程序用户登录
        StpMiniUtil.checkLogin();

        // 设置用户上下文
        Long userId = StpMiniUtil.getLoginIdAsLong();
        UserContext userContext = new UserContext(Collections.emptySet(), Collections.emptySet(), -1);
        userContext.setId(userId);
        UserContextHolder.setContext(userContext);

        OrderDetailResp order = orderService.createOrder(req);
        return R.ok(order);
    }

    @Operation(summary = "取消订单", description = "取消待支付的订单")
    @PostMapping("/cancel/{orderNo}")
    public R<Void> cancelOrder(@PathVariable String orderNo) {
        // 验证小程序用户登录
        StpMiniUtil.checkLogin();

        // 设置用户上下文
        Long userId = StpMiniUtil.getLoginIdAsLong();
        UserContext userContext = new UserContext(Collections.emptySet(), Collections.emptySet(), -1);
        userContext.setId(userId);
        UserContextHolder.setContext(userContext);

        orderService.cancelOrder(orderNo);
        return R.ok();
    }
}
