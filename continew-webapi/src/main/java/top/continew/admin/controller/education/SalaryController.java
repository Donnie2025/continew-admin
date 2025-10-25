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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.education.model.query.SalaryQuery;
import top.continew.admin.education.model.req.SalaryBatchImportReq;
import top.continew.admin.education.model.req.SalaryInitializeReq;
import top.continew.admin.education.model.req.SalaryReq;
import top.continew.admin.education.model.resp.SalaryBatchImportResp;
import top.continew.admin.education.model.resp.SalaryDetailResp;
import top.continew.admin.education.model.resp.SalaryResp;
import top.continew.admin.education.service.SalaryJobService;
import top.continew.admin.education.service.SalaryService;
import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.starter.extension.crud.enums.Api;
import top.continew.starter.web.model.R;

/**
 * 薪资管理 API
 *
 * @author don
 * @since 2025/05/13 22:43
 */
@Tag(name = "薪资管理 API")
@RestController
@Validated
@RequiredArgsConstructor
@CrudRequestMapping(value = "/education/salary", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class SalaryController extends BaseController<SalaryService, SalaryResp, SalaryDetailResp, SalaryQuery, SalaryReq> {

    private final SalaryJobService salaryJobService;

    /**
     * 生成工资流水
     */
    @Operation(summary = "生成工资流水", description = "为所有符合条件的老师创建指定日期范围的薪资记录")
    @PostMapping("/initialize-weekly")
    public R<?> initializeWeeklySalaryData(@RequestBody(required = false) SalaryInitializeReq req) {
        int count = salaryJobService.initializeWeeklySalaryData(req);
        return R.ok(count);
    }

    /**
     * 批量导入教师课程数量
     */
    @Operation(summary = "批量导入教师课程数量", description = "通过粘贴文本方式批量导入教师的课程数量数据")
    @PostMapping("/batch-import")
    public SalaryBatchImportResp batchImport(@Validated @RequestBody SalaryBatchImportReq req) {
        return baseService.batchImport(req);
    }
}