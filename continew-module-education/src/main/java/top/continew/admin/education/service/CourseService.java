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

package top.continew.admin.education.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.education.model.query.CourseQuery;
import top.continew.admin.education.model.req.CourseReq;
import top.continew.admin.education.model.resp.CourseDetailResp;
import top.continew.admin.education.model.resp.CourseResp;

import java.util.List;

/**
 * 班级业务接口
 *
 * @author don
 * @since 2025/06/21 23:25
 */
public interface CourseService extends BaseService<CourseResp, CourseDetailResp, CourseQuery, CourseReq> {

    /**
     * 根据班主任ID或手机号获取班级列表（支持按班级名搜索）
     *
     * @param teacherIdentifier 班主任ID或手机号
     * @param name              班级名称（可选，用于搜索）
     * @return 班级列表
     */
    List<CourseResp> listByMainTeacher(String teacherIdentifier, String name);

    /**
     * 根据ID获取班级信息
     *
     * @param id 班级ID
     * @return 班级信息
     */
    CourseResp getById(Long id);
}