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
import top.continew.admin.education.model.entity.StudentDO;
import top.continew.admin.education.model.query.StudentQuery;
import top.continew.admin.education.model.req.StudentBatchImportReq;
import top.continew.admin.education.model.req.StudentReq;
import top.continew.admin.education.model.resp.StudentBatchImportResp;
import top.continew.admin.education.model.resp.StudentDetailResp;
import top.continew.admin.education.model.resp.StudentResp;

import java.util.List;

/**
 * 学生管理业务接口
 *
 * @author don
 * @since 2025/04/20 01:32
 */
public interface StudentService extends BaseService<StudentResp, StudentDetailResp, StudentQuery, StudentReq> {

    /**
     * 搜索学生
     *
     * @param keyword 关键字（姓名或手机号）
     * @return 学生列表
     */
    List<StudentResp> searchStudents(String keyword);

    /**
     * 批量导入学生
     *
     * @param req 批量导入请求参数
     * @return 批量导入结果
     */
    StudentBatchImportResp batchImport(StudentBatchImportReq req);

    /**
     * 根据微信openid查询学生
     *
     * @param openid 微信openid
     * @return 学生信息
     */
    StudentDO getByOpenid(String openid);

    /**
     * 根据手机号查询学生
     *
     * @param phone 手机号
     * @return 学生信息
     */
    StudentDO getByPhone(String phone);

    /**
     * 根据ID查询学生实体
     *
     * @param id 学生ID
     * @return 学生信息
     */
    StudentDO getById(Long id);

    /**
     * 保存学生实体
     *
     * @param student 学生实体
     * @return 是否保存成功
     */
    boolean saveStudent(StudentDO student);

    /**
     * 更新学生实体
     *
     * @param student 学生实体
     * @return 是否更新成功
     */
    boolean updateStudent(StudentDO student);
}