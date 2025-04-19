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

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.education.model.query.ClassinUserQuery;
import top.continew.admin.education.model.req.ClassinUserReq;
import top.continew.admin.education.model.resp.ClassinUserDetailResp;
import top.continew.admin.education.model.resp.ClassinUserResp;
import top.continew.admin.education.service.ClassinUserService;
import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.starter.extension.crud.enums.Api;

/**
 * Classin用户管理 API
 *
 * @author donnie
 * @since 2025/04/12 20:49
 */
@Tag(name = "Classin用户管理 API")
@RestController
@CrudRequestMapping(value = "/education/classinUser", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class ClassinUserController extends BaseController<ClassinUserService, ClassinUserResp, ClassinUserDetailResp, ClassinUserQuery, ClassinUserReq> {

    //    @Autowired
    //    private ClassinUserService classinUserService;
    //
    //    @Operation(summary = "注册用户")
    //    @PostMapping("/register")
    //    public ClassinUserResp register(@Valid @RequestBody ClassinUserReq req) {
    //        return classinUserService.register(req);
    //    }
    //
    //    @Operation(summary = "添加学生")
    //    @PostMapping("/student")
    //    public ClassinUserResp addStudent(@Valid @RequestBody ClassinUserReq req) {
    //        return classinUserService.addStudent(req);
    //    }
    //
    //    @Operation(summary = "添加教师")
    //    @PostMapping("/teacher")
    //    public ClassinUserResp addTeacher(@Valid @RequestBody ClassinUserReq req) {
    //        return classinUserService.addTeacher(req);
    //    }
}