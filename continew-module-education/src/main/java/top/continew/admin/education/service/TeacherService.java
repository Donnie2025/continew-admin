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
import top.continew.admin.education.model.query.TeacherQuery;
import top.continew.admin.education.model.req.TeacherReq;
import top.continew.admin.education.model.req.TeacherRegisterReq;
import top.continew.admin.education.model.resp.TeacherDetailResp;
import top.continew.admin.education.model.resp.TeacherResp;
import top.continew.admin.education.model.entity.TeacherDO;

import java.util.List;
import java.util.Map;

/**
 * 教师业务接口
 *
 * @author donnie
 * @since 2025/04/04 18:33
 */
public interface TeacherService extends BaseService<TeacherResp, TeacherDetailResp, TeacherQuery, TeacherReq> {

    /**
     * 查询所有状态为活跃的教师列表
     *
     * @param name 教师姓名(可选)
     * @return 教师列表
     */
    List<TeacherResp> listActiveTeachers(String name);

    /**
     * 分页查询活跃教师列表（status=1 且 is_show=1）
     *
     * @param name          教师姓名（模糊查询，可选）
     * @param startDate     指定日期筛选（格式YYYYMMDD，可选）
     * @param startTimeFrom 时间范围起始（格式HH:mm，可选）
     * @param startTimeTo   时间范围结束（格式HH:mm，可选）
     * @param page          页码
     * @param pageSize      每页条数
     * @return 分页结果
     */
    Map<String, Object> listActiveTeachersPage(String name,
                                               String startDate,
                                               String startTimeFrom,
                                               String startTimeTo,
                                               int page,
                                               int pageSize);

    /**
     * 根据手机号查询教师
     * 
     * @param phone 手机号
     * @return 教师实体
     */
    TeacherDO getByPhone(String phone);

    /**
     * 搜索教师（根据姓名或手机号模糊查询）
     *
     * @param keyword 关键字（姓名或手机号）
     * @return 教师列表
     */
    List<TeacherResp> searchTeachers(String keyword);

    /**
     * 置顶教师（将sort字段设置为1）
     *
     * @param id 教师ID
     */
    void setTop(Long id);

    /**
     * 根据ID获取教师实体
     *
     * @param id 教师ID
     * @return 教师实体
     */
    TeacherDO getById(Long id);

    /**
     * 获取教师公开信息（学生端）
     * 不包含敏感信息：rate、phone、email等
     *
     * @param id 教师ID
     * @return 教师公开信息
     */
    top.continew.admin.education.model.resp.TeacherPublicResp getPublicInfo(Long id);

    /**
     * 教师注册
     *
     * @param req 注册请求参数
     * @return 教师ID
     */
    Long register(TeacherRegisterReq req);
}