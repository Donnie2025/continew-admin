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

import cn.hutool.core.codec.Base64;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.education.mapper.CredentialMapper;
import top.continew.admin.education.mapper.StudentMapper;
import top.continew.admin.education.mapper.TeacherMapper;
import top.continew.admin.education.model.entity.CredentialDO;
import top.continew.admin.education.model.entity.StudentDO;
import top.continew.admin.education.model.entity.TeacherDO;
import top.continew.admin.education.model.req.CredentialSetPasswordReq;
import top.continew.admin.education.model.req.CredentialVerifyPasswordReq;
import top.continew.admin.education.model.resp.CredentialStatusResp;
import top.continew.admin.education.model.resp.CredentialVerifyPasswordResp;
import top.continew.admin.education.service.CredentialService;

import java.time.LocalDateTime;

/**
 * 用户凭证服务实现
 *
 * @author don
 * @since 2025/11/14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CredentialServiceImpl implements CredentialService {

    private final CredentialMapper credentialMapper;
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;

    // 密码错误次数限制
    private static final int MAX_ERROR_COUNT = 5;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setPassword(CredentialSetPasswordReq req) {
        log.info("开始为用户[{}/{}]设置密码", req.getUserType(), req.getUserId());

        try {
            // Base64解码密码
            String decodedPassword = Base64.decodeStr(req.getPassword());

            // 使用BCrypt加密密码
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(decodedPassword);

            LocalDateTime now = LocalDateTime.now();

            // 根据用户类型获取用户信息
            String phone = getUserPhone(req.getUserType(), req.getUserId());
            if (phone == null) {
                throw new RuntimeException("用户不存在");
            }

            // 查询或创建用户凭证
            CredentialDO credential = credentialMapper.selectOne(
                Wrappers.lambdaQuery(CredentialDO.class)
                    .eq(CredentialDO::getUserId, req.getUserId())
                    .eq(CredentialDO::getUserType, req.getUserType())
                    .eq(CredentialDO::getCredentialType, "phone")
            );

            if (credential == null) {
                // 创建新的凭证记录
                credential = new CredentialDO();
                credential.setUserId(req.getUserId());
                credential.setUserType(req.getUserType());
                credential.setPhone(phone);
                credential.setPassword(encodedPassword);
                credential.setCredentialType("phone");
                credential.setIsActive(true);
                credential.setErrorCount(0);
                credential.setIsFrozen(false);
                credential.setPasswordUpdatedTime(now);
                credential.setCreateTime(now);
                credential.setUpdateTime(now);
                
                credentialMapper.insert(credential);
                log.info("为用户[{}/{}]创建新的凭证记录", req.getUserType(), req.getUserId());
            } else {
                // 更新现有凭证的密码
                credential.setPassword(encodedPassword);
                credential.setPasswordUpdatedTime(now);
                credential.setUpdateTime(now);
                // 重置错误计数
                credential.setErrorCount(0);
                credential.setLastErrorTime(null);
                credential.setIsFrozen(false);
                credential.setFreezeUntil(null);
                
                credentialMapper.updateById(credential);
                log.info("更新用户[{}/{}]的凭证密码", req.getUserType(), req.getUserId());
            }

            log.info("用户[{}/{}]密码设置成功", req.getUserType(), req.getUserId());
        } catch (Exception e) {
            log.error("用户[{}/{}]密码设置失败", req.getUserType(), req.getUserId(), e);
            throw new RuntimeException("密码设置失败：" + e.getMessage());
        }
    }

    @Override
    public CredentialVerifyPasswordResp verifyPassword(CredentialVerifyPasswordReq req) {
        log.info("开始验证用户密码，类型：{}，手机号：{}", req.getUserType(), req.getPhone());
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);
        
        // 优先查找教师凭证记录
        CredentialDO credential = credentialMapper.selectOne(
            Wrappers.lambdaQuery(CredentialDO.class)
                .eq(CredentialDO::getPhone, req.getPhone())
                .eq(CredentialDO::getUserType, "teacher")
                .eq(CredentialDO::getCredentialType, "phone")
                .eq(CredentialDO::getIsActive, true)
        );
        
        String actualUserType = "teacher";
        
        // 如果没找到教师凭证，再查找学生凭证
        if (credential == null) {
            log.debug("未找到教师凭证，尝试查找学生凭证，手机号：{}", req.getPhone());
            credential = credentialMapper.selectOne(
                Wrappers.lambdaQuery(CredentialDO.class)
                    .eq(CredentialDO::getPhone, req.getPhone())
                    .eq(CredentialDO::getUserType, "student")
                    .eq(CredentialDO::getCredentialType, "phone")
                    .eq(CredentialDO::getIsActive, true)
            );
            actualUserType = "student";
        }
        
        if (credential == null) {
            log.warn("用户凭证不存在，手机号：{}", req.getPhone());
            return CredentialVerifyPasswordResp.builder()
                .success(false)
                .message("用户不存在或未设置密码")
                .build();
        }
        
        log.info("找到用户凭证，实际用户类型：{}，手机号：{}", actualUserType, req.getPhone());
        
        // 判断逻辑：最后一次错误时间如果在1个小时以内，且错误次数大于等于5，直接阻断
        if (credential.getErrorCount() != null && credential.getErrorCount() >= MAX_ERROR_COUNT && 
            credential.getLastErrorTime() != null && credential.getLastErrorTime().isAfter(oneHourAgo)) {
            
            log.warn("用户账户被阻断，类型：{}，手机号：{}，错误次数：{}，最后错误时间：{}", 
                actualUserType, req.getPhone(), credential.getErrorCount(), credential.getLastErrorTime());
            return CredentialVerifyPasswordResp.builder()
                .success(false)
                .errorCount(credential.getErrorCount())
                .remainingAttempts(0)
                .frozen(true)
                .unfreezeTime(credential.getLastErrorTime().plusHours(1))
                .message("密码错误次数过多，请1小时后再试")
                .build();
        }
        
        // 获取用户信息
        UserInfo userInfo = getUserInfo(actualUserType, credential.getUserId());
        if (userInfo == null) {
            log.warn("用户信息不存在，用户ID：{}", credential.getUserId());
            return CredentialVerifyPasswordResp.builder()
                .success(false)
                .message("用户信息不存在")
                .build();
        }
        
        try {
            // Base64解码密码
            String decodedPassword = Base64.decodeStr(req.getPassword());
            
            // 验证密码
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean passwordMatch = passwordEncoder.matches(decodedPassword, credential.getPassword());
            
            if (passwordMatch) {
                // 密码正确，清空错误次数为0，更新最后登录时间
                credential.setErrorCount(0);
                credential.setLastErrorTime(null);
                credential.setIsFrozen(false);
                credential.setFreezeUntil(null);
                credential.setLastLoginTime(now);
                credential.setUpdateTime(now);
                credentialMapper.updateById(credential);
                
                log.info("用户密码验证成功，类型：{}，手机号：{}", actualUserType, req.getPhone());
                
                return CredentialVerifyPasswordResp.builder()
                    .success(true)
                    .userId(userInfo.getId())
                    .userName(userInfo.getName())
                    .userType(actualUserType)
                    .errorCount(0)
                    .remainingAttempts(MAX_ERROR_COUNT)
                    .frozen(false)
                    .message("验证成功")
                    .build();
            } else {
                // 密码错误的处理逻辑
                int currentErrorCount;
                
                // 判断：最后一次错误时间超过1个小时，不重置错误次数，只更新错误时间
                if (credential.getLastErrorTime() != null && 
                    credential.getLastErrorTime().isBefore(oneHourAgo)) {
                    currentErrorCount = (credential.getErrorCount() != null ? credential.getErrorCount() : 0) + 1; // 不重置，继续累加
                    log.info("超过1小时，继续累加错误次数，类型：{}，手机号：{}，当前错误次数：{}", actualUserType, req.getPhone(), currentErrorCount);
                } else {
                    currentErrorCount = (credential.getErrorCount() != null ? credential.getErrorCount() : 0) + 1;
                }
                
                // 更新错误记录
                credential.setErrorCount(currentErrorCount);
                credential.setLastErrorTime(now);
                credential.setIsFrozen(currentErrorCount >= MAX_ERROR_COUNT);
                credential.setFreezeUntil(currentErrorCount >= MAX_ERROR_COUNT ? now.plusHours(1) : null);
                credential.setUpdateTime(now);
                
                credentialMapper.updateById(credential);
                
                int remainingAttempts = MAX_ERROR_COUNT - currentErrorCount;
                String message;
                
                if (currentErrorCount >= MAX_ERROR_COUNT) {
                    message = "密码错误次数过多，请1小时后再试";
                    remainingAttempts = 0;
                } else {
                    message = "密码错误，剩余尝试次数：" + remainingAttempts;
                }
                
                log.warn("用户密码验证失败，类型：{}，手机号：{}，错误次数：{}，剩余尝试次数：{}", 
                    actualUserType, req.getPhone(), currentErrorCount, remainingAttempts);
                return CredentialVerifyPasswordResp.builder()
                    .success(false)
                    .errorCount(currentErrorCount)
                    .remainingAttempts(remainingAttempts)
                    .frozen(currentErrorCount >= MAX_ERROR_COUNT)
                    .unfreezeTime(currentErrorCount >= MAX_ERROR_COUNT ? now.plusHours(1) : null)
                    .message(message)
                    .build();
            }
        } catch (Exception e) {
            log.error("用户密码验证异常，类型：{}，手机号：{}", actualUserType, req.getPhone(), e);
            return CredentialVerifyPasswordResp.builder()
                .success(false)
                .message("验证失败：" + e.getMessage())
                .build();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetErrorCount(String userType, String phone) {
        log.info("开始重置用户密码错误次数，类型：{}，手机号：{}", userType, phone);
        
        CredentialDO credential = credentialMapper.selectOne(
            Wrappers.lambdaQuery(CredentialDO.class)
                .eq(CredentialDO::getPhone, phone)
                .eq(CredentialDO::getUserType, userType)
                .eq(CredentialDO::getCredentialType, "phone")
        );
        
        if (credential != null) {
            credential.setErrorCount(0);
            credential.setLastErrorTime(null);
            credential.setIsFrozen(false);
            credential.setFreezeUntil(null);
            credential.setUpdateTime(LocalDateTime.now());
            
            credentialMapper.updateById(credential);
            log.info("用户密码错误次数重置成功，类型：{}，手机号：{}", userType, phone);
        } else {
            log.warn("用户凭证不存在，无法重置错误次数，类型：{}，手机号：{}", userType, phone);
            throw new RuntimeException("用户凭证不存在");
        }
    }

    @Override
    public CredentialStatusResp getCredentialStatus(String userType, String phone) {
        log.info("查询用户凭证状态，类型：{}，手机号：{}", userType, phone);
        
        CredentialDO credential = credentialMapper.selectOne(
            Wrappers.lambdaQuery(CredentialDO.class)
                .eq(CredentialDO::getPhone, phone)
                .eq(CredentialDO::getUserType, userType)
                .eq(CredentialDO::getCredentialType, "phone")
        );
        
        if (credential == null) {
            log.warn("用户凭证不存在，类型：{}，手机号：{}", userType, phone);
            throw new RuntimeException("用户凭证不存在");
        }
        
        return CredentialStatusResp.builder()
            .userType(credential.getUserType())
            .phone(credential.getPhone())
            .isActive(credential.getIsActive())
            .errorCount(credential.getErrorCount())
            .lastErrorTime(credential.getLastErrorTime())
            .isFrozen(credential.getIsFrozen())
            .freezeUntil(credential.getFreezeUntil())
            .lastLoginTime(credential.getLastLoginTime())
            .passwordUpdatedTime(credential.getPasswordUpdatedTime())
            .build();
    }

    /**
     * 根据用户类型和ID获取手机号
     */
    private String getUserPhone(String userType, Long userId) {
        switch (userType) {
            case "student":
                StudentDO student = studentMapper.selectById(userId);
                return student != null ? student.getPhone() : null;
            case "teacher":
                TeacherDO teacher = teacherMapper.selectById(userId);
                return teacher != null ? teacher.getPhone() : null;
            default:
                return null;
        }
    }

    /**
     * 根据用户类型和ID获取用户信息
     */
    private UserInfo getUserInfo(String userType, Long userId) {
        switch (userType) {
            case "student":
                StudentDO student = studentMapper.selectById(userId);
                return student != null ? new UserInfo(student.getId(), student.getName()) : null;
            case "teacher":
                TeacherDO teacher = teacherMapper.selectById(userId);
                return teacher != null ? new UserInfo(teacher.getId(), teacher.getName()) : null;
            default:
                return null;
        }
    }

    /**
     * 用户信息内部类
     */
    private static class UserInfo {
        private final Long id;
        private final String name;

        public UserInfo(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
