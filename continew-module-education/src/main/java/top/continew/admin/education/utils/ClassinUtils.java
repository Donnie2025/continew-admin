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

package top.continew.admin.education.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import top.continew.admin.education.model.req.ClassinUserReq;
import top.continew.admin.education.model.resp.classin.ClassinBaseResp;
import top.continew.admin.education.model.resp.classin.ClassinErrorInfo;
import top.continew.starter.core.exception.BusinessException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * ClassIn 工具类
 *
 * @author don
 * @since 2025/06/21
 */
@Slf4j
public class ClassinUtils {

    /**
     * 构建公共请求参数
     *
     * @param appId     应用ID
     * @param appSecret 应用密钥
     * @return 包含公共参数的JSONObject
     */
    public static JSONObject buildCommonParams(String appId, String appSecret) {
        long timeStamp = System.currentTimeMillis() / 1000;
        String safeKey = DigestUtils.md5Hex(appSecret + timeStamp);

        JSONObject params = new JSONObject();
        params.set("SID", appId);
        params.set("safeKey", safeKey);
        params.set("timeStamp", timeStamp);
        return params;
    }

    /**
     * 构建符合ClassIn API v2要求的Header参数
     * 
     * @param appId      应用ID
     * @param appSecret  应用密钥
     * @param bodyParams 请求体参数
     * @return Header参数Map
     */
    public static Map<String, String> buildHeaderParams(String appId, String appSecret, JSONObject bodyParams) {
        // 1. 获取当前时间戳（秒级）
        long timeStamp = System.currentTimeMillis() / 1000;

        // 2. 准备参与签名的参数
        Map<String, Object> signParams = new HashMap<>();

        // 2.1 添加sid和timeStamp
        signParams.put("sid", appId);
        signParams.put("timeStamp", String.valueOf(timeStamp));

        // 2.2 添加body中的参数（排除不参与签名的参数）
        if (bodyParams != null) {
            for (Entry<String, Object> entry : bodyParams.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                // 排除数组和字典类参数
                if (value instanceof List || value instanceof Map || value instanceof JSONObject) {
                    continue;
                }

                // 排除value长度超过1024的参数
                if (value != null && value.toString().length() > 1024) {
                    continue;
                }

                signParams.put(key, value);
            }
        }

        // 3. 计算签名
        String sign = calculateSignV2(signParams, appSecret);

        // 4. 构建Header
        Map<String, String> headers = new HashMap<>();
        headers.put("X-EEO-SIGN", sign);
        headers.put("X-EEO-UID", appId);
        headers.put("X-EEO-TS", String.valueOf(timeStamp));
        headers.put("Content-Type", "application/json");

        return headers;
    }

    /**
     * 计算ClassIn API v2签名
     * 
     * @param params    参与签名的参数
     * @param secretKey 密钥
     * @return 签名值
     */
    private static String calculateSignV2(Map<String, Object> params, String secretKey) {
        // 1. 按参数名ASCII码从小到大排序
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        // 2. 拼接待签名字符串
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key).toString();

            stringBuilder.append(key).append("=").append(value);

            // 不是最后一个参数，添加&
            if (i < keys.size() - 1) {
                stringBuilder.append("&");
            }
        }

        // 3. 拼接密钥
        stringBuilder.append("&key=").append(secretKey);

        // 4. 计算MD5
        String signStr = stringBuilder.toString();
        log.debug("待签名字符串: {}", signStr);

        return DigestUtils.md5Hex(signStr);
    }

    /**
     * 校验注册参数
     *
     * @param req 注册请求
     */
    public static void validateRegisterParams(ClassinUserReq req) {
        // 手机号和邮箱二选一，且需要按照格式要求处理
        if (StrUtil.isNotBlank(req.getTelephone())) {
            // 处理手机号格式
            String telephone = req.getTelephone();
            // 如果是中国大陆手机号，直接使用
            if (telephone.startsWith("1") && telephone.length() == 11) {
                // 符合要求
            } else if (telephone.startsWith("0063")) {
                // 菲律宾手机号（0063开头），符合要求
            } else {
                // 其他国家手机号，需要添加国家代码前缀
                // 这里需要根据实际需求处理其他国家的手机号格式
                throw new BusinessException("暂不支持非中国大陆手机号注册，除菲律宾手机号外");
            }
        } else if (StrUtil.isNotBlank(req.getEmail())) {
            // 符合要求
        } else {
            throw new BusinessException("手机号和邮箱必须填写一个");
        }

        // 设置密码（必填）
        if (StrUtil.isBlank(req.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
        if (req.getPassword().length() < 6 || req.getPassword().length() > 20) {
            throw new BusinessException("密码长度必须在6-20位之间");
        }
    }

    /**
     * 执行 POST 请求并处理通用响应
     *
     * @param apiUrl   接口URL
     * @param params   请求参数
     * @param dataType 返回数据类型
     * @param <T>      泛型
     * @return 响应对象
     */
    public static <T> ClassinBaseResp<T> executePost(String apiUrl, JSONObject params, Class<T> dataType) {
        try {
            log.debug("调用 ClassIn 接口: url={}, params={}", apiUrl, params);
            HttpResponse response = HttpRequest.post(apiUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .form(params)
                .timeout(10000)
                .execute();

            String responseBody = response.body();
            log.debug("ClassIn 接口响应: {}", responseBody);

            if (StrUtil.isBlank(responseBody)) {
                throw new BusinessException("接口响应为空");
            }

            // 由于泛型嵌套，需要手动解析
            JSONObject result = JSONUtil.parseObj(responseBody);
            ClassinBaseResp<T> baseResp = new ClassinBaseResp<>();
            baseResp.setErrorInfo(result.get("error_info", ClassinErrorInfo.class));
            baseResp.setData(result.get("data", dataType));

            if (baseResp.getErrorInfo() == null) {
                throw new BusinessException("接口响应格式错误，无法解析error_info");
            }
            return baseResp;
        } catch (Exception e) {
            log.error("调用ClassIn接口异常: {}", e.getMessage(), e);
            if (e instanceof BusinessException) {
                throw (BusinessException)e;
            }
            throw new BusinessException("调用ClassIn接口失败：" + e.getMessage());
        }
    }

    /**
     * 执行通用的 POST 请求
     *
     * @param apiUrl      接口URL
     * @param params      请求参数
     * @param description 接口描述（用于日志和异常信息）
     * @return 响应JSONObject
     */
    public static JSONObject executePostRequest(String apiUrl, JSONObject params, String description) {
        try {
            log.debug("调用 ClassIn {} 接口: url={}, params={}", description, apiUrl, params);

            HttpResponse response = HttpRequest.post(apiUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .form(params)
                .timeout(10000)
                .execute();

            String responseBody = response.body();
            log.debug("ClassIn {} 接口响应: {}", description, responseBody);

            if (StrUtil.isBlank(responseBody)) {
                throw new BusinessException(description + "失败：接口响应为空");
            }

            JSONObject result = JSONUtil.parseObj(responseBody);

            // 检查是否有 error_info 字段（某些旧接口使用这种格式）
            if (result.containsKey("error_info")) {
                JSONObject errorInfo = result.getJSONObject("error_info");
                Integer errno = errorInfo.getInt("errno");
                String error = errorInfo.getStr("error");
                if (errno != null && errno != 1) {
                    log.error("ClassIn {} 接口调用失败(error_info格式): errno={}, error={}", description, errno, error);
                    throw new BusinessException(String.format("%s失败（错误码：%d）：%s", description, errno, error));
                }
                log.debug("ClassIn {} 接口调用成功: errno={}, error={}", description, errno, error);
                return result;
            }

            // 检查标准code字段
            Integer code = result.getInt("code");
            if (code == null) {
                log.error("ClassIn {} 接口返回格式异常：响应中缺少code字段，响应体: {}", description, responseBody);
                throw new BusinessException(description + "失败：接口返回格式异常（缺少code字段）");
            }
            if (code != 1) {
                String errorMsg = result.getStr("msg");
                log.error("ClassIn {} 接口调用失败: code={}, error={}", description, code, errorMsg);
                throw new BusinessException(String.format("%s失败（错误码：%d）：%s", description, code, errorMsg));
            }

            return result;
        } catch (Exception e) {
            log.error("调用ClassIn {} 接口异常: {}", description, e.getMessage(), e);
            if (e instanceof BusinessException) {
                throw (BusinessException)e;
            }
            throw new BusinessException(description + "失败：" + e.getMessage());
        }
    }

    /**
     * 执行带Header鉴权的POST请求（API v2）
     *
     * @param apiUrl      接口URL
     * @param headers     请求头
     * @param bodyParams  请求体参数
     * @param description 接口描述（用于日志和异常信息）
     * @return 响应JSONObject
     */
    public static JSONObject executePostRequestV2(String apiUrl,
                                                  Map<String, String> headers,
                                                  JSONObject bodyParams,
                                                  String description) {
        return executePostRequestV2(apiUrl, headers, bodyParams, description, null);
    }

    /**
     * 执行带Header鉴权的POST请求（API v2），可以指定可接受的错误码
     *
     * @param apiUrl               接口URL
     * @param headers              请求头
     * @param bodyParams           请求体参数
     * @param description          接口描述（用于日志和异常信息）
     * @param acceptableErrorCodes 可接受的错误码列表，这些错误码不会导致抛出异常
     * @return 响应JSONObject
     */
    public static JSONObject executePostRequestV2(String apiUrl,
                                                  Map<String, String> headers,
                                                  JSONObject bodyParams,
                                                  String description,
                                                  List<Integer> acceptableErrorCodes) {
        try {
            log.debug("调用 ClassIn {} 接口(V2): url={}, headers={}, params={}", description, apiUrl, headers, bodyParams);

            HttpRequest request = HttpRequest.post(apiUrl).timeout(10000);

            // 添加Headers
            for (Entry<String, String> entry : headers.entrySet()) {
                request.header(entry.getKey(), entry.getValue());
            }

            // 发送请求
            HttpResponse response = request.body(bodyParams.toString()).execute();

            String responseBody = response.body();
            log.debug("ClassIn {} 接口响应: {}", description, responseBody);

            if (StrUtil.isBlank(responseBody)) {
                throw new BusinessException(description + "失败：接口响应为空");
            }

            JSONObject result = JSONUtil.parseObj(responseBody);

            // 检查是否有 error_info 字段（API v2错误格式）
            if (result.containsKey("error_info")) {
                JSONObject errorInfo = result.getJSONObject("error_info");
                Integer errno = errorInfo.getInt("errno");
                String error = errorInfo.getStr("error");
                log.error("ClassIn {} 接口调用失败(API v2格式): errno={}, error={}", description, errno, error);
                throw new BusinessException(String.format("%s失败（错误码：%d）：%s", description, errno, error));
            }

            // 检查标准code字段
            Integer code = result.getInt("code");
            if (code == null) {
                log.error("ClassIn {} 接口返回格式异常：响应中缺少code字段，响应体: {}", description, responseBody);
                throw new BusinessException(description + "失败：接口返回格式异常（缺少code字段）");
            }

            // 检查是否是可接受的错误码
            boolean isAcceptableError = acceptableErrorCodes != null && acceptableErrorCodes.contains(code);

            if (code != 1 && !isAcceptableError) {
                String errorMsg = result.getStr("msg");
                log.error("ClassIn {} 接口调用失败: code={}, error={}", description, code, errorMsg);
                throw new BusinessException(String.format("%s失败（错误码：%d）：%s", description, code, errorMsg));
            }

            // 如果是可接受的错误码，记录一下日志
            if (isAcceptableError) {
                log.info("ClassIn {} 接口返回可接受的错误码: code={}, msg={}", description, code, result.getStr("msg"));
            }

            return result;
        } catch (Exception e) {
            log.error("调用ClassIn {} 接口异常: {}", description, e.getMessage(), e);
            if (e instanceof BusinessException) {
                throw (BusinessException)e;
            }
            throw new BusinessException(description + "失败：" + e.getMessage());
        }
    }
}