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
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.common.satoken.StpMiniUtil;
import top.continew.admin.education.model.req.StuCardBindReq;
import top.continew.admin.education.model.resp.CardDetailResp;
import top.continew.admin.education.model.resp.CardResp;
import top.continew.admin.education.model.resp.StuCardResp;
import top.continew.admin.education.model.resp.AccountResp;
import top.continew.admin.education.service.CardService;
import top.continew.admin.education.service.StuCardService;
import top.continew.admin.education.service.AccountService;
import top.continew.starter.web.model.R;

import java.util.List;

/**
 * 小程序会员卡 API
 *
 * @author don
 * @since 2025/11/23
 */
@Slf4j
@Tag(name = "小程序会员卡 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mini/card")
public class MiniCardController {

    private final StuCardService stuCardService;
    private final CardService cardService;
    private final AccountService accountService;
    private final top.continew.admin.education.service.OrderService orderService;

    @Operation(summary = "获取我的会员卡", description = "获取当前登录学生的会员卡列表")
    @GetMapping("/my-cards")
    public R<List<StuCardResp>> getMyCards(@RequestParam(required = false) Long stuId) {
        try {
            // 如果没有传递stuId参数，从小程序登录上下文获取
            if (stuId == null) {
                // 从Authorization头手动获取token并验证
                String authHeader = cn.dev33.satoken.SaManager.getSaTokenContext()
                    .getRequest()
                    .getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    Object loginId = StpMiniUtil.getStpLogic().getLoginIdByToken(token);
                    if (loginId != null) {
                        stuId = Long.valueOf(loginId.toString());
                    } else {
                        throw new RuntimeException("用户未登录，请先登录后再查看会员卡");
                    }
                } else {
                    throw new RuntimeException("用户未登录，请先登录后再查看会员卡");
                }
            }

            List<StuCardResp> cards = stuCardService.getAvailableCards(stuId);
            return R.ok(cards);
        } catch (Exception e) {
            // 出现异常时返回错误信息，不返回空列表
            return R.fail("500", e.getMessage());
        }
    }

    @Operation(summary = "获取在售会员卡列表", description = "获取所有可用的会员卡，需要登录")
    @GetMapping("/sale-cards")
    public R<List<CardResp>> getSaleCards() {
        // 需要登录状态，由Sa-Token拦截器验证
        return R.ok(cardService.listActiveCards());
    }

    @Operation(summary = "获取会员卡详情", description = "获取指定会员卡的详细信息，需要登录")
    @GetMapping("/sale-cards/{id}")
    public R<CardDetailResp> getSaleCardDetail(@PathVariable Long id) {
        // 需要登录状态，由Sa-Token拦截器验证
        return R.ok(cardService.get(id));
    }

    @Operation(summary = "购买记录", description = "获取当前学生近1年的订单记录（所有状态）")
    @GetMapping("/purchase-history")
    public R<List<top.continew.admin.education.model.resp.OrderResp>> getPurchaseHistory(@RequestParam(defaultValue = "10") int limit) {
        try {
            String authHeader = cn.dev33.satoken.SaManager.getSaTokenContext().getRequest().getHeader("Authorization");
            Long stuId = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                Object loginId = StpMiniUtil.getStpLogic().getLoginIdByToken(authHeader.substring(7));
                if (loginId != null)
                    stuId = Long.valueOf(loginId.toString());
            }
            if (stuId == null)
                return R.fail("500", "用户未登录");

            // 调用 OrderService 获取订单记录
            List<top.continew.admin.education.model.resp.OrderResp> orders = orderService
                .getStudentOrders(stuId, limit);
            return R.ok(orders);
        } catch (Exception e) {
            return R.fail("500", e.getMessage());
        }
    }

    @Operation(summary = "获取我的账户列表", description = "获取当前学生的所有账户信息")
    @GetMapping("/my-accounts")
    public R<List<AccountResp>> getMyAccounts() {
        try {
            String authHeader = cn.dev33.satoken.SaManager.getSaTokenContext().getRequest().getHeader("Authorization");
            Long stuId = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                Object loginId = StpMiniUtil.getStpLogic().getLoginIdByToken(authHeader.substring(7));
                if (loginId != null) {
                    stuId = Long.valueOf(loginId.toString());
                }
            }
            if (stuId == null) {
                return R.fail("500", "用户未登录");
            }
            log.info("=============== 当前登录学生ID: {} ===============", stuId);
            List<AccountResp> accounts = accountService.getStudentAccounts(stuId);
            log.info("=============== 查询到账户数量: {} ===============", accounts.size());
            return R.ok(accounts);
        } catch (Exception e) {
            return R.fail("500", e.getMessage());
        }
    }

    @Operation(summary = "购买会员卡", description = "小程序用户购买会员卡，需要登录")
    @PostMapping("/purchase")
    public R<StuCardResp> purchaseCard(@RequestBody StuCardBindReq req) {
        try {
            // 从小程序登录上下文获取学生ID，覆盖请求中的stuId
            String authHeader = cn.dev33.satoken.SaManager.getSaTokenContext().getRequest().getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                Object loginId = StpMiniUtil.getStpLogic().getLoginIdByToken(token);
                if (loginId != null) {
                    req.setStuId(Long.valueOf(loginId.toString()));
                } else {
                    throw new RuntimeException("用户未登录，请先登录后再购买会员卡");
                }
            } else {
                throw new RuntimeException("用户未登录，请先登录后再购买会员卡");
            }

            StuCardResp result = stuCardService.bindCard(req);
            return R.ok(result);
        } catch (Exception e) {
            return R.fail("500", e.getMessage());
        }
    }
}
