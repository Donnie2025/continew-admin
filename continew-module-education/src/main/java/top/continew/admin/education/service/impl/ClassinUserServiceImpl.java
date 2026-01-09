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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.continew.admin.education.client.ClassinClient;
import top.continew.admin.education.mapper.ClassinUserMapper;
import top.continew.admin.education.model.entity.ClassinUserDO;
import top.continew.admin.education.model.query.ClassinUserQuery;
import top.continew.admin.education.model.req.ClassinUserReq;
import top.continew.admin.education.model.resp.ClassinUserDetailResp;
import top.continew.admin.education.model.resp.ClassinUserResp;
import top.continew.admin.education.service.ClassinUserService;
import top.continew.starter.core.validation.CheckUtils;
import top.continew.starter.extension.crud.service.BaseServiceImpl;

/**
 * Classin用户业务实现
 *
 * @author donnie
 * @since 2025/04/12 20:49
 */
@Service
@RequiredArgsConstructor
public class ClassinUserServiceImpl extends BaseServiceImpl<ClassinUserMapper, ClassinUserDO, ClassinUserResp, ClassinUserDetailResp, ClassinUserQuery, ClassinUserReq> implements ClassinUserService {

    private final ClassinClient classinClient;

    @Override
    public Long create(ClassinUserReq req) {
        // 通过 registerStudentIfAbsent 方法来处理学生注册，此方法主要用于非学生角色的直接创建
        CheckUtils.throwIfNull(req.getClassinInstitutionId(), "机构ID不能为空");

        // 如果已经有ClassIn UID，说明已经在ClassIn注册过，直接使用
        if (req.getClassinUid() == null || req.getClassinUid().isEmpty()) {
            String classinUid = classinClient.registerClassin(req, req.getClassinInstitutionId());
            req.setClassinUid(classinUid);
        }

        return super.create(req);
    }

    @Override
    public ClassinUserDO getByMemberIdAndUserType(Long memberId, String userType) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<ClassinUserDO>()
            .eq(ClassinUserDO::getMemberId, memberId)
            .eq(ClassinUserDO::getUserType, userType));
    }

    @Override
    public ClassinUserDO getByMemberIdAndUserTypeAndInstitution(Long memberId, String userType, Long institutionId) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<ClassinUserDO>()
            .eq(ClassinUserDO::getMemberId, memberId)
            .eq(ClassinUserDO::getUserType, userType)
            .eq(ClassinUserDO::getClassinInstitutionId, institutionId));
    }
}