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

import top.continew.admin.education.model.entity.ClassinUserDO;
import top.continew.admin.education.model.query.ClassinUserQuery;
import top.continew.admin.education.model.req.ClassinUserReq;
import top.continew.admin.education.model.resp.ClassinUserDetailResp;
import top.continew.admin.education.model.resp.ClassinUserResp;
import top.continew.starter.extension.crud.service.BaseService;

/**
 * ClassIn用户Service接口
 */
public interface ClassinUserService extends BaseService<ClassinUserResp, ClassinUserDetailResp, ClassinUserQuery, ClassinUserReq> {

    ClassinUserDO getByStudentId(Long studentId);
}