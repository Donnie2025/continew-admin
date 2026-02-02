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
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.education.model.query.StudentQuery;
import top.continew.admin.education.model.req.StudentBatchImportReq;
import top.continew.admin.education.model.req.StudentReq;
import top.continew.admin.education.model.resp.StudentBatchImportResp;
import top.continew.admin.education.model.resp.StudentDetailResp;
import top.continew.admin.education.model.resp.StudentResp;
import top.continew.admin.education.service.StudentService;
import net.dreamlu.mica.core.result.R;

import java.util.List;

/**
 * 学生管理管理 API
 *
 * @author don
 * @since 2025/04/20 01:32
 */
@Tag(name = "学生管理管理 API")
@RestController
@CrudRequestMapping(value = "/education/student", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class StudentController extends BaseController<StudentService, StudentResp, StudentDetailResp, StudentQuery, StudentReq> {

    /**
     * 搜索学生
     *
     * @param keyword 关键字（姓名或手机号）
     * @return 学生列表
     */
    @GetMapping("/search")
    @Operation(summary = "搜索学生", description = "根据关键字搜索启用状态的学生")
    public R<List<StudentResp>> search(@Parameter(description = "关键字（姓名或手机号）") @RequestParam String keyword) {
        return R.success(baseService.searchStudents(keyword));
    }

    /**
     * 批量导入学生
     *
     * @param req 批量导入请求参数
     * @return 导入结果
     */
    @PostMapping("/batch-import")
    @Operation(summary = "批量导入学生", description = "批量导入学生信息（格式：学生姓名[Tab]手机号码）")
    public StudentBatchImportResp batchImport(@Valid @RequestBody StudentBatchImportReq req) {
        return baseService.batchImport(req);
    }

    /**
     * 修改学生姓名并同步到ClassIn
     *
     * @param id      学生ID
     * @param newName 新姓名
     * @return 操作结果
     */
    @PutMapping("/{id}/name")
    @Operation(summary = "修改学生姓名", description = "修改学生姓名并同步到ClassIn平台")
    public R<String> updateStudentName(
        @Parameter(description = "学生ID") @PathVariable Long id,
        @Parameter(description = "新姓名") @RequestParam String newName) {
        
        boolean success = baseService.updateStudentNameAndSyncClassin(id, newName);
        if (success) {
            return R.success("学生姓名修改成功");
        } else {
            return R.fail("学生姓名修改失败");
        }
    }
}