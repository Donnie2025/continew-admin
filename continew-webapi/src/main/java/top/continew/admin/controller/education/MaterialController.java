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

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.education.model.query.MaterialQuery;
import top.continew.admin.education.model.req.MaterialReq;
import top.continew.admin.education.model.req.MaterialSortReq;
import top.continew.admin.education.model.req.SyncCloudFoldersReq;
import top.continew.admin.education.model.resp.MaterialDetailResp;
import top.continew.admin.education.model.resp.MaterialResp;
import top.continew.admin.education.model.resp.MaterialStatisticsResp;
import top.continew.admin.education.model.resp.MaterialImportResp;
import top.continew.admin.education.model.resp.MaterialLessonWithCompletionResp;
import top.continew.admin.education.model.req.MaterialLessonImportReq;
import top.continew.admin.education.service.MaterialService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 教材管理 API
 *
 * @author don
 * @since 2025/12/29 21:22
 */
@Tag(name = "教材管理 API")
@SaIgnore
@RestController
@Validated
@CrudRequestMapping(value = "/education/material", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class MaterialController extends BaseController<MaterialService, MaterialResp, MaterialDetailResp, MaterialQuery, MaterialReq> {

    /**
     * 获取全量教材列表（不分页，用于构建树结构）
     */
    @Operation(summary = "获取全量教材列表（不分页）", description = "获取全量教材数据，用于构建树形结构")
    @GetMapping("/list-all")
    public R<List<MaterialResp>> listAll() {
        return R.ok(baseService.listAll());
    }

    /**
     * 根据分类获取教材列表
     */
    @Operation(summary = "根据分类获取教材列表", description = "根据分类获取教材列表")
    @Parameter(name = "category", description = "分类", required = true)
    @GetMapping("/category/{category}")
    public R<List<MaterialResp>> listByCategory(@PathVariable @NotBlank(message = "分类不能为空") String category) {
        return R.ok(baseService.listByCategory(category));
    }

    /**
     * 获取教材统计信息
     */
    @Operation(summary = "获取教材统计信息", description = "获取教材统计信息")
    @GetMapping("/statistics")
    public R<MaterialStatisticsResp> getStatistics() {
        return R.ok(baseService.getStatistics());
    }

    /**
     * 批量更新排序
     */
    @Operation(summary = "批量更新排序", description = "批量更新排序")
    @PutMapping("/sort")
    public R<Void> batchUpdateSort(@RequestBody @Valid List<MaterialSortReq> sortList) {
        if (sortList == null || sortList.isEmpty()) {
            throw new IllegalArgumentException("排序列表不能为空");
        }
        baseService.batchUpdateSort(sortList);
        return R.ok();
    }

    /**
     * 同步云盘文件夹列表到教材表
     */
    @Operation(summary = "同步云盘文件夹到教材表", description = "将云盘文件夹列表批量写入edu_material，支持跳过已有cloud_id")
    @PostMapping("/sync-cloud-folders")
    public R<Integer> syncCloudFolders(@RequestBody @Valid SyncCloudFoldersReq req) {
        int count = baseService.syncCloudFolders(req);
        return R.ok(count);
    }

    /**
     * 从ClassIn云盘同步数据到数据库
     */
    @Operation(summary = "从ClassIn云盘同步数据到数据库", description = "根据cloudId从ClassIn拉取最新名称，更新edu_material的cloudName字段")
    @PostMapping("/sync-cloud-data")
    public R<Integer> syncCloudData(@RequestBody @NotEmpty List<Long> ids) {
        int count = baseService.syncCloudData(ids);
        return R.ok(count);
    }

    /**
     * 同步飞书文件夹token
     */
    @Operation(summary = "同步飞书文件夹token", description = "同步指定节点的飞书token和lesson_url，可选择是否递归同步子节点")
    @PostMapping("/{id}/sync-feishu")
    public R<java.util.Map<String, Object>> syncFeishu(@Parameter(description = "节点ID") @PathVariable Long id,
                                                       @RequestBody(required = false) java.util.Map<String, Object> params) {
        boolean recursive = params != null && Boolean.TRUE.equals(params.get("recursive"));
        return R.ok(baseService.syncFeishu(id, recursive));
    }

    // ========== 课节相关端点（原MaterialLessonController） ==========

    /**
     * 从飞书链接导入课节
     */
    @Operation(summary = "从飞书链接导入课节", description = "根据飞书文件夹链接批量导入课节到指定教材下")
    @PostMapping("/lessons/import-feishu")
    public R<MaterialImportResp> importLessonsFromFeishu(@Valid @RequestBody MaterialLessonImportReq req) {
        MaterialImportResp result = baseService.importLessonsFromFeishu(req);
        return R.ok(result);
    }

    /**
     * 获取教材的课程列表（包含完成状态）
     */
    @Operation(summary = "获取教材课程列表（含完成状态）", description = "获取指定教材下的所有课程，包含学生完成状态")
    @Parameter(name = "materialId", description = "教材ID", required = true)
    @GetMapping("/{materialId}/lessons-with-completion")
    public R<List<MaterialLessonWithCompletionResp>> listLessonsWithCompletionStatus(@PathVariable Long materialId) {
        return R.ok(baseService.listLessonsWithCompletionStatus(materialId));
    }
}