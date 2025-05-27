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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.beans.BeanUtils;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.StudentMapper;
import top.continew.admin.education.model.entity.StudentDO;
import top.continew.admin.education.model.query.StudentQuery;
import top.continew.admin.education.model.req.StudentReq;
import top.continew.admin.education.model.resp.StudentDetailResp;
import top.continew.admin.education.model.resp.StudentResp;
import top.continew.admin.education.service.StudentService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生管理业务实现
 *
 * @author don
 * @since 2025/04/20 01:32
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends BaseServiceImpl<StudentMapper, StudentDO, StudentResp, StudentDetailResp, StudentQuery, StudentReq> implements StudentService {

    @Override
    public List<StudentResp> searchStudents(String keyword) {
        // 构建查询条件
        LambdaQueryWrapper<StudentDO> queryWrapper = Wrappers.lambdaQuery(StudentDO.class)
            .eq(StudentDO::getStatus, 1) // 状态为启用
            .orderByDesc(StudentDO::getCreateTime); // 按创建时间倒序

        // 如果关键字不为空，则添加名字或手机号的模糊查询条件
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper.like(StudentDO::getName, keyword)
                .or()
                .like(StudentDO::getPhone, keyword));
        }

        // 执行查询并转换为响应对象
        List<StudentDO> studentList = this.baseMapper.selectList(queryWrapper);
        return studentList.stream().map(this::convert).collect(Collectors.toList());
    }

    /**
     * 将 StudentDO 转换为 StudentResp
     *
     * @param entity StudentDO 实体
     * @return StudentResp 响应对象
     */
    private StudentResp convert(StudentDO entity) {
        if (entity == null) {
            return null;
        }
        StudentResp resp = new StudentResp();
        BeanUtils.copyProperties(entity, resp);
        return resp;
    }
}