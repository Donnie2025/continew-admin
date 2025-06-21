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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.education.model.query.StuCardQuery;
import top.continew.admin.education.model.req.StuCardBindReq;
import top.continew.admin.education.model.req.StuCardReq;
import top.continew.admin.education.model.resp.StuCardDetailResp;
import top.continew.admin.education.model.resp.StuCardResp;
import top.continew.admin.education.service.StuCardService;
import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.starter.extension.crud.enums.Api;

import java.util.List;

/**
 * 会员绑卡管理 API
 *
 * @author don
 * @since 2025/05/10 22:11
 */
@Tag(name = "会员绑卡管理 API")
@RestController
@RequiredArgsConstructor
@CrudRequestMapping(value = "/education/stuCard", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class StuCardController extends BaseController<StuCardService, StuCardResp, StuCardDetailResp, StuCardQuery, StuCardReq> {

    private final StuCardService stuCardService;

    @Operation(summary = "绑定会员卡", description = "绑定学生会员卡并保存交易记录")
    @PostMapping("/bind")
    public StuCardResp bindCard(@Valid @RequestBody StuCardBindReq req) {
        return stuCardService.bindCard(req);
    }

    @Operation(summary = "获取会员可用的会员卡列表", description = "根据会员ID获取可用的会员卡列表，支持按教师ID筛选")
    @GetMapping("/available")
    public List<StuCardResp> getAvailableCards(@Parameter(description = "会员ID", required = true) @RequestParam Long stuId) {
        return stuCardService.getAvailableCards(stuId);
    }
}