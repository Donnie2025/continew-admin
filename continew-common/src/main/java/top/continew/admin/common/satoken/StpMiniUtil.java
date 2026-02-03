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

package top.continew.admin.common.satoken;

import cn.dev33.satoken.stp.StpLogic;

/**
 * 小程序用户 Sa-Token 工具类
 *
 * @author Charles7c
 * @since 2025/1/4 15:30
 */
public class StpMiniUtil {

    /**
     * 小程序用户专用的 StpLogic 实例
     */
    public static final StpLogic stpLogic;

    static {
        stpLogic = new StpLogic("mini");
        // 设置与默认Sa-Token相同的token配置，确保前端能正确传递token
        try {
            stpLogic.getConfig().setTokenName("Authorization");
            stpLogic.getConfig().setTokenPrefix("Bearer");
        } catch (Exception e) {
            // 如果设置失败，记录错误但不影响初始化
            System.err.println("设置StpMiniUtil token配置失败: " + e.getMessage());
        }
    }

    /**
     * 获取小程序用户专用的 StpLogic 实例
     */
    public static StpLogic getStpLogic() {
        return stpLogic;
    }

    // ==================== 登录相关 ====================

    /**
     * 小程序用户登录
     */
    public static void login(Object loginId) {
        stpLogic.login(loginId);
    }

    /**
     * 小程序用户登录（带参数）
     */
    public static void login(Object loginId, String device) {
        stpLogic.login(loginId, device);
    }

    /**
     * 小程序用户登出
     */
    public static void logout() {
        stpLogic.logout();
    }

    /**
     * 检查小程序用户是否登录
     */
    public static void checkLogin() {
        stpLogic.checkLogin();
    }

    /**
     * 获取小程序用户登录ID
     */
    public static Object getLoginId() {
        return stpLogic.getLoginId();
    }

    /**
     * 获取小程序用户Token
     */
    public static String getTokenValue() {
        return stpLogic.getTokenValue();
    }

    /**
     * 获取小程序用户会话
     */
    public static cn.dev33.satoken.session.SaSession getSession() {
        return stpLogic.getSession();
    }
}
