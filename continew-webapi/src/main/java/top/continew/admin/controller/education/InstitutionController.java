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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.education.model.resp.InstitutionResp;
import top.continew.admin.education.service.InstitutionService;

import java.util.List;

/**
 * 机构管理 API
 *
 * @author don
 * @since 2025/11/07
 */
@Tag(name = "机构管理 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/education/institution")
public class InstitutionController {

    private final InstitutionService institutionService;

    /**
     * 获取所有启用的机构列表
     *
     * @return 机构列表
     */
    @Operation(summary = "获取所有启用的机构列表", description = "获取所有启用的机构列表")
    @GetMapping("/active")
    public List<InstitutionResp> listActiveInstitutions() {
        return institutionService.listActiveInstitutions();
    }

    /**
     * 获取所有机构列表
     *
     * @return 机构列表
     */
    @Operation(summary = "获取所有机构列表", description = "获取所有机构列表")
    @GetMapping("/list")
    public List<InstitutionResp> listInstitutions() {
        return institutionService.listAll();
    }
}
