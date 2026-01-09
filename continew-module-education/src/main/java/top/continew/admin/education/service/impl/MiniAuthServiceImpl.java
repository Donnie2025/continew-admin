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

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.continew.admin.common.context.UserContext;
import top.continew.admin.common.context.UserContextHolder;
import top.continew.admin.common.context.UserExtraContext;
import top.continew.admin.common.satoken.StpMiniUtil;
import top.continew.admin.education.model.entity.StudentDO;
import top.continew.admin.education.model.req.MiniBindPhoneReq;
import top.continew.admin.education.model.req.MiniPasswordLoginReq;
import top.continew.admin.education.model.req.MiniSendCodeReq;
import top.continew.admin.education.model.req.MiniWechatLoginReq;
import top.continew.admin.education.model.req.CredentialVerifyPasswordReq;
import top.continew.admin.education.model.resp.CredentialVerifyPasswordResp;
import top.continew.admin.education.model.resp.MiniLoginResp;
import top.continew.admin.education.service.MiniAuthService;
import top.continew.admin.education.service.CredentialService;
import top.continew.admin.education.service.StudentService;
import top.continew.starter.core.exception.BadRequestException;
import top.continew.starter.core.validation.ValidationUtils;
import top.continew.starter.web.util.ServletUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 小程序认证业务实现
 *
 * @author don
 * @since 2025/11/19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MiniAuthServiceImpl implements MiniAuthService {

    private final StudentService studentService;
    private final CredentialService credentialService;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${wechat.miniprogram.app-id:}")
    private String appId;

    @Value("${wechat.miniprogram.app-secret:}")
    private String appSecret;

    private static final String WECHAT_API_URL = "https://api.weixin.qq.com/sns/jscode2session";
    private static final String VERIFY_CODE_PREFIX = "mini:verify:code:";
    private static final long VERIFY_CODE_EXPIRE_TIME = 5;

    @Override
    public MiniLoginResp loginByWechat(MiniWechatLoginReq req, HttpServletRequest request) {
        log.info("开始微信登录，code: {}, userInfo: {}", req.getCode(), req.getUserInfo() != null ? "有用户信息" : "无用户信息");

        // 调用微信API获取用户信息
        WechatUserInfo wechatUserInfo = getWechatUserInfo(req.getCode());

        // 查找或创建学生用户（不处理手机号）
        StudentDO student = findOrCreateStudent(wechatUserInfo, req.getUserInfo(), request);

        // 创建用户上下文（小程序用户无权限和角色，密码永不过期）
        UserContext userContext = new UserContext(Collections.emptySet(), Collections.emptySet(), -1);
        userContext.setId(student.getId());
        userContext.setUsername(student.getOpenid()); // 使用openid作为username
        userContext.setDeptId(null); // 学生无部门
        userContext.setPwdResetTime(null); // 无密码
        userContext.setClientType("miniprogram");
        userContext.setClientId("miniprogram");

        // 生成token（使用Sa-Token）
        SaLoginParameter loginParameter = new SaLoginParameter();
        loginParameter.setDeviceType("miniprogram");
        loginParameter.setExtraData(BeanUtil.beanToMap(new UserExtraContext(request)));
        // 使用小程序专用的StpLogic进行登录，避免与后台管理系统的用户ID冲突
        StpMiniUtil.login(student.getId(), loginParameter.getDeviceType());
        UserContextHolder.setContext(userContext);
        String token = StpMiniUtil.getTokenValue();

        log.info("小程序登录成功: userId={}, openid={}", student.getId(), student.getOpenid());

        // 返回登录响应
        // 优先使用name字段，如果不存在或为默认值，则使用nickname字段
        String displayName = student.getName();
        if (StrUtil.isBlank(displayName) || "微信用户".equals(displayName)) {
            displayName = StrUtil.isNotBlank(student.getNickname()) ? student.getNickname() : "微信用户";
        }

        // 如果头像不存在，使用默认头像
        String avatarUrl = StrUtil.isNotBlank(student.getAvatar())
            ? student.getAvatar()
            : "/assets/images/default.jpeg";

        return MiniLoginResp.builder()
            .token(token)
            .userId(student.getId())
            .userName(displayName)
            .nickname(student.getNickname())
            .avatar(avatarUrl)
            .phone(student.getPhone()) // 返回手机号，用于前端判断是否需要采集
            .userType("student")
            .build();
    }

    @Override
    public MiniLoginResp loginByPassword(MiniPasswordLoginReq req, HttpServletRequest request) {
        log.info("=== 小程序密码登录开始 ===");
        log.info("请求参数: phone={}, password={}", req.getPhone(), req.getPassword());

        try {

            // 1. 根据手机号查找学生
            StudentDO student = studentService.getByPhone(req.getPhone());
            if (student == null) {
                throw new BadRequestException("手机号未注册，请先注册或使用微信登录");
            }

            // 2. 验证密码
            CredentialVerifyPasswordReq verifyReq = new CredentialVerifyPasswordReq();
            verifyReq.setUserType("student");
            verifyReq.setPhone(req.getPhone());
            verifyReq.setPassword(req.getPassword()); // 密码已经是Base64编码的

            CredentialVerifyPasswordResp verifyResp = credentialService.verifyPassword(verifyReq);
            if (!Boolean.TRUE.equals(verifyResp.getSuccess())) {
                throw new BadRequestException(verifyResp.getMessage());
            }

            // 3. 构建用户上下文（小程序用户无权限和角色，密码永不过期）
            UserContext userContext = new UserContext(Collections.emptySet(), Collections.emptySet(), -1);
            userContext.setId(student.getId());
            userContext.setUsername(student.getPhone()); // 使用手机号作为username
            userContext.setDeptId(null); // 学生无部门
            userContext.setPwdResetTime(null); // 无密码重置时间
            userContext.setClientType("miniprogram");
            userContext.setClientId("miniprogram");

            // 4. 使用小程序专用的StpLogic进行登录
            StpMiniUtil.login(student.getId(), "miniprogram");

            // 将用户上下文保存到小程序的session中
            StpMiniUtil.getStpLogic().getSession().set(cn.dev33.satoken.session.SaSession.USER, userContext);

            String token = StpMiniUtil.getTokenValue();

            // 验证token是否有效
            Object verifyLoginId = StpMiniUtil.getStpLogic().getLoginIdByToken(token);
            log.info("小程序密码登录成功: userId={}, phone={}, token={}, verifyLoginId={}", student.getId(), student
                .getPhone(), token, verifyLoginId);

            // 5. 返回登录响应
            String displayName = StrUtil.isNotBlank(student.getName()) ? student.getName() : "用户";
            String avatarUrl = StrUtil.isNotBlank(student.getAvatar())
                ? student.getAvatar()
                : "/assets/images/default.jpeg";

            return MiniLoginResp.builder()
                .token(token)
                .userId(student.getId())
                .userName(displayName)
                .nickname(student.getNickname())
                .avatar(avatarUrl)
                .phone(student.getPhone())
                .userType("student")
                .build();

        } catch (Exception e) {
            log.error("小程序密码登录失败: phone={}, error={}", req.getPhone(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 调用微信API获取用户信息
     *
     * @param code 微信临时登录凭证
     * @return 微信用户信息
     */
    private WechatUserInfo getWechatUserInfo(String code) {
        ValidationUtils.throwIfBlank(appId, "微信小程序AppId未配置");
        ValidationUtils.throwIfBlank(appSecret, "微信小程序AppSecret未配置");

        Map<String, Object> params = new HashMap<>();
        params.put("appid", appId);
        params.put("secret", appSecret);
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");

        try {
            String response = HttpUtil.get(WECHAT_API_URL, params);
            log.info("微信API响应: {}", response);

            JSONObject jsonObject = JSONUtil.parseObj(response);

            // 检查是否有错误
            if (jsonObject.containsKey("errcode")) {
                Integer errcode = jsonObject.getInt("errcode");
                String errmsg = jsonObject.getStr("errmsg");
                log.error("微信API调用失败: errcode={}, errmsg={}", errcode, errmsg);
                throw new RuntimeException("微信登录失败: " + errmsg);
            }

            WechatUserInfo userInfo = new WechatUserInfo();
            userInfo.setOpenid(jsonObject.getStr("openid"));
            userInfo.setSessionKey(jsonObject.getStr("session_key"));
            userInfo.setUnionid(jsonObject.getStr("unionid"));

            return userInfo;
        } catch (Exception e) {
            log.error("调用微信API异常", e);
            throw new RuntimeException("微信登录失败，请重试");
        }
    }

    /**
     * 根据openid查找或创建学生用户
     *
     * @param wechatUserInfo 微信用户信息
     * @param userInfo       前端传递的用户信息
     * @param request        请求对象
     * @return 学生信息
     */
    private StudentDO findOrCreateStudent(WechatUserInfo wechatUserInfo,
                                          MiniWechatLoginReq.UserInfo userInfo,
                                          HttpServletRequest request) {
        String openid = wechatUserInfo.getOpenid();

        // 根据openid查找学生
        StudentDO student = studentService.getByOpenid(openid);

        if (student == null) {
            // 创建新学生
            student = new StudentDO();
            student.setOpenid(openid);
            student.setUnionid(wechatUserInfo.getUnionid());

            // 设置用户信息
            if (userInfo != null) {
                student.setName(StrUtil.isNotBlank(userInfo.getNickName()) ? userInfo.getNickName() : "微信用户");
                student.setNickname(userInfo.getNickName());
                student.setCountry(userInfo.getCountry());
                student.setProvince(userInfo.getProvince());
                student.setCity(userInfo.getCity());
            } else {
                student.setName("微信用户");
                student.setNickname("微信用户");
            }

            // 手机号留空，等待后续采集
            student.setGender(userInfo != null && userInfo.getGender() != null
                ? (userInfo.getGender() == 1 ? "male" : userInfo.getGender() == 2 ? "female" : "male")
                : "male");
            student.setAvatar(StrUtil.isNotBlank(userInfo != null ? userInfo.getAvatarUrl() : null)
                ? userInfo.getAvatarUrl()
                : "");
            student.setCountry(userInfo != null ? userInfo.getCountry() : null);
            student.setProvince(userInfo != null ? userInfo.getProvince() : null);
            student.setCity(userInfo != null ? userInfo.getCity() : null);
            student.setStatus(1); // 启用状态
            student.setRegisterTime(LocalDateTime.now());
            student.setLastLoginTime(LocalDateTime.now());
            student.setLastLoginIp(ServletUtils.getClientIP(request));
            student.setInstitutionId(1L); // 默认机构ID，需要根据实际情况调整
            student.setCreateUser(1L); // 系统创建用户，使用默认管理员ID
            student.setCreateTime(LocalDateTime.now());

            // 保存学生
            boolean saved = studentService.saveStudent(student);
            if (saved) {
                log.info("创建微信学生用户成功: openid={}, name={}", openid, student.getName());
            } else {
                log.error("创建微信学生用户失败: openid={}, name={}", openid, student.getName());
                throw new RuntimeException("创建用户失败");
            }
        } else {
            // 更新学生信息
            boolean needUpdate = false;
            if (userInfo != null) {
                // 更新昵称（如果提供了新的昵称且与当前不同）
                if (StrUtil.isNotBlank(userInfo.getNickName()) && !userInfo.getNickName()
                    .equals(student.getNickname())) {
                    student.setNickname(userInfo.getNickName());
                    // 如果学生姓名为默认值，也更新姓名
                    if ("微信用户".equals(student.getName())) {
                        student.setName(userInfo.getNickName());
                    }
                    needUpdate = true;
                    log.info("更新学生昵称: openid={}, 新昵称={}", openid, userInfo.getNickName());
                }

                // 更新头像
                if (StrUtil.isNotBlank(userInfo.getAvatarUrl()) && !userInfo.getAvatarUrl()
                    .equals(student.getAvatar())) {
                    student.setAvatar(userInfo.getAvatarUrl());
                    needUpdate = true;
                    log.info("更新学生头像: openid={}", openid);
                }

                // 更新性别
                if (userInfo.getGender() != null) {
                    String newGender = userInfo.getGender() == 1
                        ? "male"
                        : userInfo.getGender() == 2 ? "female" : "male";
                    if (!newGender.equals(student.getGender())) {
                        student.setGender(newGender);
                        needUpdate = true;
                        log.info("更新学生性别: openid={}, 新性别={}", openid, newGender);
                    }
                }

                // 更新地理位置信息
                if (StrUtil.isNotBlank(userInfo.getCountry()) && !userInfo.getCountry().equals(student.getCountry())) {
                    student.setCountry(userInfo.getCountry());
                    needUpdate = true;
                }
                if (StrUtil.isNotBlank(userInfo.getProvince()) && !userInfo.getProvince()
                    .equals(student.getProvince())) {
                    student.setProvince(userInfo.getProvince());
                    needUpdate = true;
                }
                if (StrUtil.isNotBlank(userInfo.getCity()) && !userInfo.getCity().equals(student.getCity())) {
                    student.setCity(userInfo.getCity());
                    needUpdate = true;
                }
            }

            // 手机号由专门的采集页面处理，这里不更新

            // 更新最后登录信息
            student.setLastLoginTime(LocalDateTime.now());
            student.setLastLoginIp(ServletUtils.getClientIP(request));
            student.setUpdateUser(1L); // 系统更新用户，使用默认管理员ID
            needUpdate = true;

            if (needUpdate) {
                boolean updated = studentService.updateStudent(student);
                if (updated) {
                    log.info("更新微信学生用户信息成功: openid={}, name={}", openid, student.getName());
                } else {
                    log.error("更新微信学生用户信息失败: openid={}, name={}", openid, student.getName());
                }
            }
        }

        // 无论是新用户还是老用户，都需要更新最后登录信息
        student.setLastLoginTime(LocalDateTime.now());
        student.setLastLoginIp(ServletUtils.getClientIP(request));
        student.setUpdateUser(1L); // 系统更新用户，使用默认管理员ID

        // 如果是老用户，需要更新登录信息
        if (studentService.getByOpenid(openid) != null) {
            boolean updated = studentService.updateStudent(student);
            if (updated) {
                log.info("更新学生最后登录信息成功: openid={}", openid);
            } else {
                log.error("更新学生最后登录信息失败: openid={}", openid);
            }
        }

        return student;
    }

    @Override
    public void sendVerifyCode(MiniSendCodeReq req) {
        String phone = req.getPhone();
        log.info("发送验证码到手机号: {}", phone);

        // 检查该手机号是否已被其他用户使用
        StudentDO existStudent = studentService.getByPhone(phone);
        if (existStudent != null) {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (!existStudent.getId().equals(currentUserId)) {
                throw new BadRequestException("该手机号已被其他用户绑定");
            }
        }

        // 生成6位数验证码
        String code = RandomUtil.randomNumbers(6);

        // 保存验证码到Redis，有效期5分钟
        String key = VERIFY_CODE_PREFIX + phone;
        stringRedisTemplate.opsForValue().set(key, code, VERIFY_CODE_EXPIRE_TIME, TimeUnit.MINUTES);

        log.info("验证码已生成并保存到Redis: phone={}, code={}", phone, code);

        // TODO: 这里应该调用短信服务发送验证码
        // smsService.send(phone, code);

        // 开发环境下，将验证码打印到日志中（生产环境应删除）
        log.warn("[开发环境] 验证码: {}", code);
    }

    @Override
    public void bindPhone(MiniBindPhoneReq req) {
        String phone = req.getPhone();
        String code = req.getCode();

        log.info("绑定手机号: phone={}, code={}", phone, code);

        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();

        // 验证验证码
        String key = VERIFY_CODE_PREFIX + phone;
        String cachedCode = stringRedisTemplate.opsForValue().get(key);

        // 万能验证码453923，用于测试和开发
        boolean isUniversalCode = "453923".equals(code);

        if (!isUniversalCode) {
            if (StrUtil.isBlank(cachedCode)) {
                throw new BadRequestException("验证码已过期，请重新获取");
            }

            if (!cachedCode.equals(code)) {
                throw new BadRequestException("验证码错误");
            }
        }

        // 检查该手机号是否已被其他用户使用
        StudentDO existStudent = studentService.getByPhone(phone);
        if (existStudent != null && !existStudent.getId().equals(userId)) {
            throw new BadRequestException("该手机号已被其他用户绑定");
        }

        // 获取当前学生信息
        StudentDO student = studentService.getById(userId);
        if (student == null) {
            throw new BadRequestException("用户不存在");
        }

        // 更新手机号
        student.setPhone(phone);
        student.setUpdateUser(userId);
        student.setUpdateTime(LocalDateTime.now());

        // 如果用户填写了英文名，则将英文名和年龄拼接存储到name字段
        if (StrUtil.isNotBlank(req.getEnglishName())) {
            StringBuilder nameBuilder = new StringBuilder();
            String englishName = req.getEnglishName().trim();

            // 将英文名首字母大写，其余字母小写
            if (englishName.length() > 0) {
                englishName = englishName.substring(0, 1).toUpperCase() + (englishName.length() > 1
                    ? englishName.substring(1).toLowerCase()
                    : "");
            }
            nameBuilder.append(englishName);

            // 如果同时填写了年龄，则直接拼接年龄（无连接符）
            if (req.getAge() != null && req.getAge() > 0) {
                nameBuilder.append(req.getAge());
            }

            student.setName(nameBuilder.toString());
            log.info("更新学生姓名: userId={}, name={}", userId, nameBuilder.toString());
        }

        // 处理额外信息并存储到remark字段
        StringBuilder remarkBuilder = new StringBuilder();

        if (StrUtil.isNotBlank(req.getEnglishName())) {
            remarkBuilder.append("英文名: ").append(req.getEnglishName().trim());
        }

        if (req.getAge() != null && req.getAge() > 0) {
            if (remarkBuilder.length() > 0) {
                remarkBuilder.append("; ");
            }
            remarkBuilder.append("年龄: ").append(req.getAge());
        }

        if (StrUtil.isNotBlank(req.getEnglishLevel())) {
            if (remarkBuilder.length() > 0) {
                remarkBuilder.append("; ");
            }
            remarkBuilder.append("英语水平: ").append(req.getEnglishLevel().trim());
        }

        // 如果有额外信息，就更新remark字段
        if (remarkBuilder.length() > 0) {
            student.setRemark(remarkBuilder.toString());
            log.info("保存学生额外信息到remark: userId={}, remark={}", userId, remarkBuilder.toString());
        }

        boolean updated = studentService.updateStudent(student);
        if (!updated) {
            log.error("更新学生信息失败: userId={}, phone={}", userId, phone);
            throw new BadRequestException("绑定手机号失败");
        }

        // 删除验证码
        stringRedisTemplate.delete(key);

        log.info("绑定手机号成功: userId={}, phone={}", userId, phone);
    }

    /**
     * 微信用户信息
     */
    @Data
    private static class WechatUserInfo {
        private String openid;
        private String sessionKey;
        private String unionid;
    }
}
