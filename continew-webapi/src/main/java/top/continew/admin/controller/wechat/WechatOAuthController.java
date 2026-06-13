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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.common.satoken.StpMiniUtil;
import top.continew.admin.education.config.WechatMpConfig;
import top.continew.admin.education.mapper.StudentMapper;
import top.continew.admin.education.model.entity.StudentDO;
import top.continew.starter.web.model.R;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 微信OAuth授权控制器
 *
 * @author don
 * @since 2026/06/05
 */
@Slf4j
@Tag(name = "微信OAuth授权API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wechat/oauth")
public class WechatOAuthController {

    private final WxMpService wxMpService;
    private final WechatMpConfig wechatMpConfig;
    private final StudentMapper studentMapper;

    /**
     * 获取授权链接
     *
     * 前端调用此接口获取授权URL，然后重定向到该URL引导用户授权
     */
    @Operation(summary = "获取授权链接", description = "获取微信OAuth2授权链接，用于获取用户openid")
    @GetMapping("/authorize-url")
    public R<String> getAuthorizeUrl(@Parameter(description = "授权后重定向地址") @RequestParam(required = false) String redirectUrl) {

        log.info("获取授权链接，redirectUrl: {}", redirectUrl);

        try {
            // 如果没有指定redirectUrl，使用配置的回调地址
            String callbackUrl = wechatMpConfig.getOauthRedirectUrl();

            // 如果指定了redirectUrl，将其作为参数传递
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                callbackUrl = callbackUrl + "?redirect=" + URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8);
            }

            // 构建授权链接
            // scope: snsapi_base - 静默授权，只能获取openid
            // scope: snsapi_userinfo - 需要用户确认，可以获取用户信息
            String authorizeUrl = wxMpService.getOAuth2Service()
                .buildAuthorizationUrl(callbackUrl, "snsapi_base", null);

            log.info("授权链接生成成功: {}", authorizeUrl);
            return R.ok(authorizeUrl);

        } catch (Exception e) {
            log.error("生成授权链接失败", e);
            return R.fail("500", "生成授权链接失败: " + e.getMessage());
        }
    }

    /**
     * OAuth授权回调
     *
     * 用户授权后，微信会回调此接口，携带code参数
     * 我们用code换取access_token，从中获取openid
     */
    @Operation(summary = "OAuth授权回调", description = "接收微信OAuth2授权回调，获取用户openid")
    @GetMapping("/callback")
    public void callback(@Parameter(description = "授权码") @RequestParam String code,
                         @Parameter(description = "重定向地址") @RequestParam(required = false) String redirect,
                         HttpServletResponse response,
                         HttpSession session) {

        log.info("收到OAuth回调，code: {}, redirect: {}", code, redirect);

        try {
            // 用code换取access_token
            WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
            String openid = accessToken.getOpenId();

            log.info("获取openid成功: {}", openid);

            // 将openid保存到session中
            session.setAttribute("wechat_openid", openid);

            // 如果用户已登录，更新学生表中的openid
            try {
                if (StpMiniUtil.isLogin()) {
                    Long userId = StpMiniUtil.getLoginIdAsLong();
                    StudentDO student = studentMapper.selectById(userId);
                    if (student != null && (student.getOpenid() == null || student.getOpenid().isEmpty())) {
                        student.setOpenid(openid);
                        studentMapper.updateById(student);
                        log.info("已更新学生openid，studentId: {}, openid: {}", userId, openid);
                    }
                }
            } catch (Exception e) {
                log.warn("更新学生openid失败，但不影响授权流程", e);
            }

            // 重定向到指定页面
            String redirectUrl = redirect != null && !redirect.isEmpty() ? redirect : "/pages/card/index"; // 默认跳转到会员卡页面

            response.sendRedirect(redirectUrl);

        } catch (WxErrorException e) {
            log.error("OAuth回调处理失败，错误码: {}, 错误信息: {}", e.getError().getErrorCode(), e.getError().getErrorMsg(), e);
            try {
                response.sendRedirect("/pages/error?msg=" + URLEncoder.encode("授权失败", StandardCharsets.UTF_8));
            } catch (IOException ex) {
                log.error("重定向到错误页面失败", ex);
            }
        } catch (Exception e) {
            log.error("OAuth回调处理失败", e);
            try {
                response.sendRedirect("/pages/error?msg=" + URLEncoder.encode("授权失败", StandardCharsets.UTF_8));
            } catch (IOException ex) {
                log.error("重定向到错误页面失败", ex);
            }
        }
    }

    /**
     * 获取当前用户的openid
     *
     * 前端可以调用此接口检查是否已授权，以及获取openid
     */
    @Operation(summary = "获取当前用户openid", description = "从session中获取当前用户的openid")
    @GetMapping("/openid")
    public R<String> getOpenid(HttpSession session) {
        String openid = (String)session.getAttribute("wechat_openid");

        if (openid == null || openid.isEmpty()) {
            return R.fail("500", "未授权，请先进行微信授权");
        }

        log.info("获取当前用户openid: {}", openid);
        return R.ok(openid);
    }

    /**
     * 清除授权信息（用于测试或退出登录）
     */
    @Operation(summary = "清除授权信息", description = "清除session中的openid")
    @PostMapping("/clear")
    public R<Void> clearAuth(HttpSession session) {
        session.removeAttribute("wechat_openid");
        log.info("已清除授权信息");
        return R.ok();
    }

    /**
     * 绑定微信openid到当前登录学生
     */
    @Operation(summary = "绑定微信openid", description = "将session中的openid绑定到当前登录的学生账户")
    @PostMapping("/bind")
    public R<Void> bindOpenid(HttpSession session) {
        // 验证小程序用户登录
        if (!StpMiniUtil.isLogin()) {
            return R.fail("401", "请先登录");
        }

        // 获取session中的openid
        String openid = (String)session.getAttribute("wechat_openid");
        if (openid == null || openid.isEmpty()) {
            return R.fail("400", "未找到openid，请先进行微信授权");
        }

        // 更新学生表中的openid
        Long userId = StpMiniUtil.getLoginIdAsLong();
        StudentDO student = studentMapper.selectById(userId);
        if (student == null) {
            return R.fail("404", "学生信息不存在");
        }

        student.setOpenid(openid);
        studentMapper.updateById(student);

        log.info("绑定微信openid成功，studentId: {}, openid: {}", userId, openid);
        return R.ok();
    }

    /**
     * 获取微信JS-SDK配置签名
     *
     * 用于前端调用wx.config初始化微信JS-SDK
     */
    @Operation(summary = "获取JS-SDK配置签名", description = "获取微信JS-SDK配置所需的签名信息")
    @GetMapping("/jssdk-signature")
    public R<Object> getJsSdkSignature(@Parameter(description = "当前页面URL") @RequestParam String url) {
        log.info("获取JS-SDK签名，url: {}", url);

        try {
            // 获取微信JS-SDK配置
            me.chanjar.weixin.common.bean.WxJsapiSignature signature = wxMpService.createJsapiSignature(url);

            // 构建返回数据
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("appId", signature.getAppId());
            result.put("timestamp", signature.getTimestamp());
            result.put("nonceStr", signature.getNonceStr());
            result.put("signature", signature.getSignature());
            result.put("url", signature.getUrl());

            log.info("JS-SDK签名生成成功");
            return R.ok(result);

        } catch (WxErrorException e) {
            log.error("生成JS-SDK签名失败，错误码: {}, 错误信息: {}", e.getError().getErrorCode(), e.getError().getErrorMsg(), e);
            return R.fail("500", "生成JS-SDK签名失败: " + e.getError().getErrorMsg());
        } catch (Exception e) {
            log.error("生成JS-SDK签名失败", e);
            return R.fail("500", "生成JS-SDK签名失败: " + e.getMessage());
        }
    }
}
