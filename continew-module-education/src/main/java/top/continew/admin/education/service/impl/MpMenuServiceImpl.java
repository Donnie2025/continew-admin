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

package top.continew.admin.education.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.continew.admin.education.model.req.MpMenuReq;
import top.continew.admin.education.model.resp.MpMenuResp;
import top.continew.admin.education.service.MpMenuService;
import top.continew.starter.core.exception.BadRequestException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 微信公众号菜单业务实现
 *
 * @author don
 * @since 2025/05/02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MpMenuServiceImpl implements MpMenuService {

    private static final String WX_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    private static final String WX_MENU_CREATE = "https://api.weixin.qq.com/cgi-bin/menu/create";
    private static final String WX_MENU_GET = "https://api.weixin.qq.com/cgi-bin/menu/get";
    private static final String WX_MENU_DELETE = "https://api.weixin.qq.com/cgi-bin/menu/delete";
    private static final String REDIS_TOKEN_KEY = "wechat:mp:access_token";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${wechat.mp.app-id:}")
    private String appId;

    @Value("${wechat.mp.app-secret:}")
    private String appSecret;

    @Override
    public MpMenuResp getMenu() {
        String token = getAccessToken();
        String url = WX_MENU_GET + "?access_token=" + token;
        String response = HttpUtil.get(url);
        log.info("获取公众号菜单响应: {}", response);
        JSONObject json = JSONUtil.parseObj(response);
        int errcode = json.getInt("errcode", 0);
        if (errcode == 46003) {
            log.info("公众号暂无自定义菜单，返回空数据");
            return new MpMenuResp();
        }
        checkError(json, "获取菜单失败");
        try {
            return objectMapper.readValue(response, MpMenuResp.class);
        } catch (Exception e) {
            log.error("解析菜单响应失败", e);
            throw new BadRequestException("解析菜单数据失败");
        }
    }

    @Override
    public void saveMenu(MpMenuReq req) {
        String token = getAccessToken();
        String url = WX_MENU_CREATE + "?access_token=" + token;
        try {
            String body = buildMenuJson(req);
            log.info("创建公众号菜单，请求体: {}", body);
            String response = HttpUtil.post(url, body);
            log.info("创建公众号菜单响应: {}", response);
            JSONObject json = JSONUtil.parseObj(response);
            checkError(json, "保存菜单失败");
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("保存公众号菜单异常", e);
            throw new BadRequestException("保存菜单失败：" + e.getMessage());
        }
    }

    @Override
    public void deleteMenu() {
        String token = getAccessToken();
        String url = WX_MENU_DELETE + "?access_token=" + token;
        String response = HttpUtil.get(url);
        log.info("删除公众号菜单响应: {}", response);
        JSONObject json = JSONUtil.parseObj(response);
        checkError(json, "删除菜单失败");
    }

    /**
     * 获取 Access Token（Redis 缓存，提前 5 分钟刷新）
     */
    private String getAccessToken() {
        String cached = redisTemplate.opsForValue().get(REDIS_TOKEN_KEY);
        if (cached != null) {
            return cached;
        }
        return refreshAccessToken();
    }

    /**
     * 从微信服务器刷新 Access Token
     */
    private String refreshAccessToken() {
        if (appId == null || appId.isEmpty()) {
            throw new BadRequestException("微信公众号 AppId 未配置（wechat.mp.app-id）");
        }
        if (appSecret == null || appSecret.isEmpty()) {
            throw new BadRequestException("微信公众号 AppSecret 未配置（wechat.mp.app-secret）");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("grant_type", "client_credential");
        params.put("appid", appId);
        params.put("secret", appSecret);
        String response = HttpUtil.get(WX_TOKEN_URL, params);
        log.info("刷新公众号 AccessToken 响应: {}", response);
        JSONObject json = JSONUtil.parseObj(response);
        if (json.containsKey("errcode")) {
            throw new BadRequestException("获取微信 Access Token 失败: " + json.getStr("errmsg"));
        }
        String token = json.getStr("access_token");
        int expiresIn = json.getInt("expires_in", 7200);
        redisTemplate.opsForValue().set(REDIS_TOKEN_KEY, token, expiresIn - 300, TimeUnit.SECONDS);
        log.info("公众号 AccessToken 已刷新，有效期 {}s", expiresIn - 300);
        return token;
    }

    /**
     * 构建菜单 JSON（过滤空值，字段名转为微信要求的 snake_case）
     */
    private String buildMenuJson(MpMenuReq req) throws Exception {
        List<Map<String, Object>> buttons = new ArrayList<>();
        for (MpMenuReq.Button btn : req.getButton()) {
            buttons.add(toWechatButton(btn));
        }
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("button", buttons);
        return objectMapper.writeValueAsString(root);
    }

    /**
     * 将 Button 转为微信接口所需的 Map，跳过 null 和空字符串，子菜单用 sub_button
     */
    private Map<String, Object> toWechatButton(MpMenuReq.Button btn) {
        Map<String, Object> map = new LinkedHashMap<>();
        putIfNotEmpty(map, "name", btn.getName());
        List<MpMenuReq.Button> subs = btn.getSubButton();
        if (subs != null && !subs.isEmpty()) {
            List<Map<String, Object>> subList = new ArrayList<>();
            for (MpMenuReq.Button sub : subs) {
                subList.add(toWechatButton(sub));
            }
            map.put("sub_button", subList);
        } else {
            putIfNotEmpty(map, "type", btn.getType());
            putIfNotEmpty(map, "key", btn.getKey());
            putIfNotEmpty(map, "url", btn.getUrl());
            putIfNotEmpty(map, "appid", btn.getAppid());
            putIfNotEmpty(map, "pagepath", btn.getPagepath());
            putIfNotEmpty(map, "media_id", btn.getMediaId());
        }
        return map;
    }

    private void putIfNotEmpty(Map<String, Object> map, String key, String value) {
        if (value != null && !value.isEmpty()) {
            map.put(key, value);
        }
    }

    /**
     * 检查微信 API 错误码
     */
    private void checkError(JSONObject json, String message) {
        if (json.containsKey("errcode")) {
            int errcode = json.getInt("errcode", 0);
            if (errcode != 0) {
                String errmsg = json.getStr("errmsg", "unknown error");
                log.error("{}: errcode={}, errmsg={}", message, errcode, errmsg);
                throw new BadRequestException(message + "（" + errcode + ": " + errmsg + "）");
            }
        }
    }

}
