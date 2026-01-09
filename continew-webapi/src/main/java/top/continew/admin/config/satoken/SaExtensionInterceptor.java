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

package top.continew.admin.config.satoken;

import cn.dev33.satoken.fun.SaParamFunction;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import top.continew.admin.common.satoken.StpMiniUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import top.continew.admin.common.context.UserContext;
import top.continew.admin.common.context.UserContextHolder;

/**
 * Sa-Token 扩展拦截器
 *
 * @author Charles7c
 * @since 2024/10/10 20:25
 */
@Slf4j
public class SaExtensionInterceptor extends SaInterceptor {

    public SaExtensionInterceptor(SaParamFunction<Object> auth) {
        super(auth);
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 判断是否是小程序请求
        String requestPath = request.getRequestURI();
        if (requestPath.startsWith("/api/mini/")) {
            // 小程序请求，检查是否在排除列表中
            if (requestPath.startsWith("/api/mini/auth/") || requestPath.equals("/api/mini/teacher/active")) {
                // 在排除列表中，直接通过
                return true;
            }

            // 不在排除列表中，需要验证token
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                Object loginId = StpMiniUtil.getStpLogic().getLoginIdByToken(token);
                if (loginId != null) {
                    // token有效，设置用户上下文
                    try {
                        UserContext userContext = StpMiniUtil.getStpLogic()
                            .getSession()
                            .getModel(cn.dev33.satoken.session.SaSession.USER, UserContext.class);
                        if (userContext != null) {
                            UserContextHolder.setContext(userContext);
                        }
                        UserContextHolder.getExtraContext();
                    } catch (Exception e) {
                        log.warn("获取小程序用户上下文失败: {}", e.getMessage());
                    }
                    return true;
                } else {
                    // token无效，抛出未登录异常
                    throw new RuntimeException("小程序token无效");
                }
            } else {
                // 没有Authorization头，抛出未登录异常
                throw new RuntimeException("小程序请求缺少Authorization头");
            }
        } else {
            // 普通请求，使用父类的检查逻辑
            boolean flag = super.preHandle(request, response, handler);
            if (flag) {
                if (StpUtil.isLogin()) {
                    UserContextHolder.getContext();
                    UserContextHolder.getExtraContext();
                }
            }
            return flag;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                @Nullable Exception e) throws Exception {
        try {
            super.afterCompletion(request, response, handler, e);
        } finally {
            UserContextHolder.clearContext();
        }
    }
}
