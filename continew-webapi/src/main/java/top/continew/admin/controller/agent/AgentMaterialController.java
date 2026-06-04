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

package top.continew.admin.controller.agent;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.common.context.UserContextHolder;
import top.continew.admin.education.mapper.MaterialMapper;
import top.continew.admin.education.model.entity.MaterialDO;
import top.continew.admin.education.model.req.MaterialReq;
import top.continew.admin.education.model.resp.MaterialDetailResp;
import top.continew.admin.education.model.resp.MaterialResp;
import top.continew.admin.education.service.MaterialService;
import top.continew.starter.extension.crud.model.resp.IdResp;
import top.continew.starter.web.model.R;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 机构教材管理 API（代理机构视角）
 *
 * @author don
 * @since 2025/05/27
 */
@Tag(name = "机构教材管理 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/agent/material")
public class AgentMaterialController {

    private final MaterialService materialService;
    private final MaterialMapper materialMapper;

    private static final String YOUYAN_AGENT_CODE = "youyan";
    private static final String CO_READING_BOOKS_NAME = "【H】Co-reading books";

    /**
     * 获取 "【H】Co-reading books" CATEGORY 节点的 ID
     */
    private Long getCoReadingBooksId() {
        MaterialDO node = materialMapper.selectOne(Wrappers.<MaterialDO>lambdaQuery()
            .eq(MaterialDO::getName, CO_READING_BOOKS_NAME)
            .eq(MaterialDO::getType, "CATEGORY")
            .last("LIMIT 1"));
        return node != null ? node.getId() : null;
    }

    /**
     * 从全量列表中提取某节点（含自身）及其所有后代
     */
    private List<MaterialResp> filterSubtree(List<MaterialResp> all, Long rootId) {
        Set<String> included = new HashSet<>();
        included.add(String.valueOf(rootId));
        boolean changed = true;
        while (changed) {
            changed = false;
            for (MaterialResp item : all) {
                if (!included.contains(String.valueOf(item.getId())) && included.contains(String.valueOf(item
                    .getPid()))) {
                    included.add(String.valueOf(item.getId()));
                    changed = true;
                }
            }
        }
        return all.stream().filter(m -> included.contains(String.valueOf(m.getId()))).collect(Collectors.toList());
    }

    /**
     * 检查当前用户是否有操作权限（youyan 代理）
     */
    private boolean hasPermission() {
        String agentCode = UserContextHolder.getAgentCode();
        return YOUYAN_AGENT_CODE.equals(agentCode);
    }

    /**
     * 获取全量机构教材树（不分页，用于构建树形结构）
     */
    @Operation(summary = "获取全量机构教材列表", description = "获取 Co-reading books 节点下的所有教材，用于构建树形结构")
    @GetMapping("/list-all")
    public R<List<MaterialResp>> listAll() {
        if (!hasPermission()) {
            return R.ok(Collections.emptyList());
        }
        Long rootId = getCoReadingBooksId();
        if (rootId == null) {
            return R.ok(Collections.emptyList());
        }
        List<MaterialResp> all = materialService.listAll();
        return R.ok(filterSubtree(all, rootId));
    }

    /**
     * 查询教材节点详情
     */
    @Operation(summary = "查询机构教材节点详情")
    @GetMapping("/{id}")
    public R<MaterialDetailResp> get(@PathVariable Long id) {
        if (!hasPermission()) {
            return R.fail("403", "无权限访问");
        }
        return R.ok(materialService.get(id));
    }

    /**
     * 新增教材节点
     */
    @Operation(summary = "新增机构教材节点")
    @PostMapping
    public R<IdResp<Long>> create(@Valid @RequestBody MaterialReq req) {
        if (!hasPermission()) {
            return R.fail("403", "无权限操作");
        }
        Long id = materialService.create(req);
        return R.ok(new IdResp<>(id));
    }

    /**
     * 修改教材节点
     */
    @Operation(summary = "修改机构教材节点")
    @PutMapping("/{id}")
    public R<Void> update(@Valid @RequestBody MaterialReq req, @PathVariable Long id) {
        if (!hasPermission()) {
            return R.fail("403", "无权限操作");
        }
        materialService.update(req, id);
        return R.ok();
    }

    /**
     * 删除教材节点（支持批量）
     */
    @Operation(summary = "删除机构教材节点")
    @DeleteMapping
    public R<Void> delete(@RequestBody List<Long> ids) {
        if (!hasPermission()) {
            return R.fail("403", "无权限操作");
        }
        materialService.delete(ids);
        return R.ok();
    }
}
