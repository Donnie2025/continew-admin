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

package top.continew.admin.controller.education;

import top.continew.starter.extension.crud.enums.Api;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.education.model.query.TeacherQuery;
import top.continew.admin.education.model.req.TeacherReq;
import top.continew.admin.education.model.resp.TeacherDetailResp;
import top.continew.admin.education.model.resp.TeacherResp;
import top.continew.admin.education.service.TeacherService;

import java.util.List;

/**
 * 教师管理 API
 *
 * @author donnie
 * @since 2025/04/04 18:33
 */
@Tag(name = "教师管理 API")
@RestController
@CrudRequestMapping(value = "/education/teacher", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class TeacherController extends BaseController<TeacherService, TeacherResp, TeacherDetailResp, TeacherQuery, TeacherReq> {

    @GetMapping("/active")
    @Operation(summary = "查询活跃教师列表", description = "查询所有状态为活跃的教师列表，按照排序字段升序排列，可选择按教师姓名进行模糊查询")
    public List<TeacherResp> listActiveTeachers(@RequestParam(required = false) String name) {
        return this.baseService.listActiveTeachers(name);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索教师", description = "根据关键字（姓名或手机号）搜索启用状态的教师")
    public List<TeacherResp> search(@RequestParam String keyword) {
        return this.baseService.searchTeachers(keyword);
    }
}