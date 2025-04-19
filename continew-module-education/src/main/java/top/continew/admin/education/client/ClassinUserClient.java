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

package top.continew.admin.education.client;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import top.continew.admin.education.config.ClassinProperties;
import top.continew.admin.education.model.req.ClassinUserReq;
import top.continew.starter.core.exception.BusinessException;

/**
 * ClassIn用户客户端
 *
 * @author donnie
 * @since 2025/04/12 20:49
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClassinUserClient {

    private final ClassinProperties properties;

    @PostConstruct
    public void init() {
        log.info("开始初始化ClassIn客户端配置...");
        log.info("当前配置信息: url={}, register={}, addSchoolStudent={}, addTeacher={}, appId={}", properties
            .getUrl(), properties.getRegister(), properties.getAddSchoolStudent(), properties
                .getAddTeacher(), properties.getAppId());

        // 校验必要的配置参数
        if (StrUtil.isBlank(properties.getUrl())) {
            throw new IllegalStateException("ClassIn API URL不能为空，请检查配置文件中的classin.api.url配置项");
        }
        if (StrUtil.isBlank(properties.getAppId())) {
            throw new IllegalStateException("ClassIn AppID不能为空，请检查配置文件中的classin.api.appId配置项");
        }
        if (StrUtil.isBlank(properties.getAppSecret())) {
            throw new IllegalStateException("ClassIn AppSecret不能为空，请检查配置文件中的classin.api.appSecret配置项");
        }
        if (StrUtil.isBlank(properties.getRegister())) {
            throw new IllegalStateException("ClassIn注册接口路径不能为空，请检查配置文件中的classin.api.register配置项");
        }
        if (StrUtil.isBlank(properties.getAddSchoolStudent())) {
            throw new IllegalStateException("ClassIn添加学生接口路径不能为空，请检查配置文件中的classin.api.addSchoolStudent配置项");
        }
        if (StrUtil.isBlank(properties.getAddTeacher())) {
            throw new IllegalStateException("ClassIn添加教师接口路径不能为空，请检查配置文件中的classin.api.addTeacher配置项");
        }

        log.info("ClassIn客户端初始化完成");
    }

    /**
     * 构建公共请求参数
     */
    private JSONObject buildCommonParams() {
        long timeStamp = System.currentTimeMillis() / 1000;
        String safeKey = DigestUtils.md5Hex(properties.getAppSecret() + timeStamp);

        JSONObject params = new JSONObject();
        params.set("SID", properties.getAppId());
        params.set("safeKey", safeKey);
        params.set("timeStamp", timeStamp);
        return params;
    }

    /**
     * 校验注册参数
     */
    private void validateRegisterParams(ClassinUserReq req) {
        // 手机号和邮箱二选一，且需要按照格式要求处理
        if (StrUtil.isNotBlank(req.getTelephone())) {
            // 处理手机号格式
            String telephone = req.getTelephone();
            // 如果是中国大陆手机号，直接使用
            if (telephone.startsWith("1") && telephone.length() == 11) {
                // 符合要求
            } else {
                // 其他国家手机号，需要添加国家代码前缀
                // 这里需要根据实际需求处理其他国家的手机号格式
                throw new BusinessException("暂不支持非中国大陆手机号注册");
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
     * 调用ClassIn注册接口
     */
    public String registerClassin(ClassinUserReq req) {
        try {
            // 校验注册参数
            validateRegisterParams(req);

            // 构建请求参数
            JSONObject params = buildCommonParams();
            // 手机号和邮箱二选一，且需要按照格式要求处理
            if (StrUtil.isNotBlank(req.getTelephone())) {
                params.set("telephone", req.getTelephone());
            } else if (StrUtil.isNotBlank(req.getEmail())) {
                params.set("email", req.getEmail());
            }
            // 设置密码（必填）
            params.set("password", req.getPassword());
            // 设置是否加入为机构成员
            // 0：不加为机构成员；1：加为机构学生；2：加为机构老师
            params.set("addToSchoolMember", 0);
            // 设置昵称（选填，最长24位字符）
            if (StrUtil.isNotBlank(req.getNickname())) {
                if (req.getNickname().length() > 24) {
                    params.set("nickname", req.getNickname().substring(0, 24));
                } else {
                    params.set("nickname", req.getNickname());
                }
            }
            // 调用ClassIn注册接口
            String apiUrl = properties.getUrl() + properties.getRegister();
            log.debug("调用ClassIn注册接口: url={}, params={}", apiUrl, params);

            HttpResponse response = HttpRequest.post(apiUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .form(params)  // 使用form方式提交参数
                .timeout(10000)
                .execute();

            // 解析响应
            String responseBody = response.body();
            log.debug("ClassIn注册接口响应: {}", responseBody);

            if (StrUtil.isBlank(responseBody)) {
                throw new BusinessException("注册失败：接口响应为空");
            }

            JSONObject result = JSONUtil.parseObj(responseBody);
            JSONObject errorInfo = result.getJSONObject("error_info");
            int code = errorInfo.getInt("errno", -1);

            // 处理错误码135的情况（手机号已注册）
            if (code == 135 || code == 461) {  // 461是邮箱已注册的错误码
                log.info("用户已在ClassIn注册，直接获取用户ID");
                return result.getStr("data");
            }

            // 处理其他错误情况
            if (code != 1) {
                String errorMsg = errorInfo.getStr("error");
                log.error("ClassIn注册接口调用失败: code={}, error={}, params={}", code, errorMsg, params);
                throw new BusinessException(String.format("注册失败（错误码：%d）：%s", code, errorMsg));
            }
            return result.getStr("data");
        } catch (Exception e) {
            log.error("调用ClassIn注册接口异常: {}", e.getMessage());
            if (e instanceof BusinessException) {
                throw (BusinessException)e;
            }
            throw new BusinessException("注册失败：" + e.getMessage());
        }
    }

    public String addStudent(ClassinUserReq req) {
        try {
            // 构建请求参数
            JSONObject params = buildCommonParams();
            params.set("password", req.getPassword());
            params.set("nickname", req.getNickname());

            // 调用ClassIn添加学生接口
            String apiUrl = properties.getUrl() + properties.getAddSchoolStudent();
            log.debug("调用ClassIn添加学生接口: url={}, params={}", apiUrl, params);

            HttpResponse response = HttpRequest.post(apiUrl)
                .header("Content-Type", "application/json")
                .body(params.toString())
                .timeout(10000)
                .execute();

            // 解析响应
            String responseBody = response.body();
            log.debug("ClassIn添加学生接口响应: {}", responseBody);

            if (StrUtil.isBlank(responseBody)) {
                throw new BusinessException("添加学生失败：接口响应为空");
            }

            JSONObject result = JSONUtil.parseObj(responseBody);
            if (result.getInt("code") != 1) {
                String errorMsg = result.getStr("error");
                log.error("ClassIn添加学生接口调用失败: code={}, error={}, params={}", result.getInt("code"), errorMsg, params);
                throw new BusinessException("添加学生失败：" + errorMsg);
            }

            return result.getJSONObject("data").getStr("data");
        } catch (Exception e) {
            log.error("调用ClassIn添加学生接口异常: {}", e.getMessage());
            if (e instanceof BusinessException) {
                throw (BusinessException)e;
            }
            throw new BusinessException("添加学生失败：" + e.getMessage());
        }
    }

    public String addTeacher(ClassinUserReq req) {
        try {
            // 构建请求参数
            JSONObject params = buildCommonParams();
            params.set("password", req.getPassword());
            params.set("nickname", req.getNickname());
            if (StrUtil.isNotBlank(req.getEmail())) {
                params.set("email", req.getEmail());
            }

            // 调用ClassIn添加教师接口
            String apiUrl = properties.getUrl() + properties.getAddTeacher();
            log.debug("调用ClassIn添加教师接口: url={}, params={}", apiUrl, params);

            HttpResponse response = HttpRequest.post(apiUrl)
                .header("Content-Type", "application/json")
                .body(params.toString())
                .timeout(10000)
                .execute();

            // 解析响应
            String responseBody = response.body();
            log.debug("ClassIn添加教师接口响应: {}", responseBody);

            if (StrUtil.isBlank(responseBody)) {
                throw new BusinessException("添加教师失败：接口响应为空");
            }

            JSONObject result = JSONUtil.parseObj(responseBody);
            if (result.getInt("code") != 1) {
                String errorMsg = result.getStr("error");
                log.error("ClassIn添加教师接口调用失败: code={}, error={}, params={}", result.getInt("code"), errorMsg, params);
                throw new BusinessException("添加教师失败：" + errorMsg);
            }
            return result.getJSONObject("data").getStr("data");
        } catch (Exception e) {
            log.error("调用ClassIn添加教师接口异常: {}", e.getMessage());
            if (e instanceof BusinessException) {
                throw (BusinessException)e;
            }
            throw new BusinessException("添加教师失败：" + e.getMessage());
        }
    }
}