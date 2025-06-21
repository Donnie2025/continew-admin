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

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.education.client.ClassinUserClient;
import top.continew.admin.education.constant.ClassinConstants;
import top.continew.admin.education.mapper.ClassinUserMapper;
import top.continew.admin.education.model.entity.ClassinUserDO;
import top.continew.admin.education.model.query.ClassinUserQuery;
import top.continew.admin.education.model.req.ClassinUserReq;
import top.continew.admin.education.model.resp.ClassinUserDetailResp;
import top.continew.admin.education.model.resp.ClassinUserResp;
import top.continew.admin.education.service.ClassinUserService;
import top.continew.starter.extension.crud.service.BaseServiceImpl;

/**
 * ClassIn用户Service实现类
 *
 * @author donnie
 * @since 2025/04/12 20:49
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassinUserServiceImpl extends BaseServiceImpl<ClassinUserMapper, ClassinUserDO, ClassinUserResp, ClassinUserDetailResp, ClassinUserQuery, ClassinUserReq> implements ClassinUserService {

    @Autowired
    private ClassinUserClient classinUserClient;

    private final ClassinUserMapper classinUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ClassinUserReq req) {
        // 参数校验
        validateCreateParams(req);
        // 调用ClassIn注册接口
        String classinUid = classinUserClient.registerClassin(req);
        req.setClassinUid(classinUid);
        return super.create(req);
    }

    /**
     * 校验创建参数
     */
    private void validateCreateParams(ClassinUserReq req) {
        if (StrUtil.isBlank(req.getTelephone()) && StrUtil.isBlank(req.getEmail())) {
            throw new IllegalArgumentException("手机号和邮箱必须填写一个");
        }
        if (StrUtil.isBlank(req.getPassword())) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (req.getPassword().length() < 6 || req.getPassword().length() > 20) {
            throw new IllegalArgumentException("密码长度必须在6-20位之间");
        }
        if (StrUtil.isNotBlank(req.getNickname()) && req.getNickname().length() > 24) {
            throw new IllegalArgumentException("昵称长度不能超过24个字符");
        }
    }

    @Override
    public ClassinUserDO getByMemberIdAndUserType(Long memberId, String userType) {
        if (memberId == null) {
            return null;
        }
        LambdaQueryWrapper<ClassinUserDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ClassinUserDO::getMemberId, memberId)
                    .eq(ClassinUserDO::getUserType, userType)
                    .eq(ClassinUserDO::getStatus, 1);
        return classinUserMapper.selectOne(queryWrapper);
    }
}