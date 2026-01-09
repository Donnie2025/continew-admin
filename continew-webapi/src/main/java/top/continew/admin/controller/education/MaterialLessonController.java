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

import cn.dev33.satoken.annotation.SaIgnore;
import top.continew.starter.extension.crud.enums.Api;
import top.continew.starter.web.model.R;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.education.model.query.MaterialLessonQuery;
import top.continew.admin.education.model.req.MaterialLessonImportReq;
import top.continew.admin.education.model.req.MaterialLessonReq;
import top.continew.admin.education.model.resp.MaterialLessonDetailResp;
import top.continew.admin.education.model.resp.MaterialLessonImportResp;
import top.continew.admin.education.model.resp.MaterialLessonResp;
import top.continew.admin.education.service.MaterialLessonService;

/**
 * 课节管理 API
 *
 * @author don
 * @since 2025/12/29 21:22
 */
@Tag(name = "课节管理 API")
@SaIgnore
@RestController
@CrudRequestMapping(value = "/education/materialLesson", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class MaterialLessonController extends BaseController<MaterialLessonService, MaterialLessonResp, MaterialLessonDetailResp, MaterialLessonQuery, MaterialLessonReq> {

    /**
     * 从飞书链接导入课节
     *
     * @param req 导入请求参数
     * @return 导入结果
     */
    @Operation(summary = "从飞书链接导入课节", description = "根据飞书文件夹链接批量导入课节")
    @PostMapping("/import")
    public R<MaterialLessonImportResp> importFromFeishu(@Valid @RequestBody MaterialLessonImportReq req) {
        MaterialLessonImportResp result = baseService.importFromFeishu(req);
        return R.ok(result);
    }
}