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

package top.continew.admin.controller.mini;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.continew.admin.education.model.resp.MaterialLessonWithCompletionResp;
import top.continew.admin.education.service.MaterialService;

import java.util.List;

/**
 * 小程序教材管理 API
 *
 * @author continew-org
 * @since 2024-12-29
 */
@Tag(name = "小程序教材管理 API")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mini/materials")
public class MiniMaterialController {

    private final MaterialService materialService;

    @Operation(summary = "获取教材课程内容列表", description = "获取指定教材的所有课程内容，包含完成状态")
    @GetMapping("/{materialId}/coursewares")
    public List<MaterialLessonWithCompletionResp> getCoursewares(@Parameter(description = "教材ID") @PathVariable Long materialId) {
        return materialService.listLessonsWithCompletionStatus(materialId);
    }
}
