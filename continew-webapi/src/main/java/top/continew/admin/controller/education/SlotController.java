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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.starter.web.model.R;
import top.continew.admin.education.model.query.SlotQuery;
import top.continew.admin.education.model.req.SlotReq;
import top.continew.admin.education.model.req.BatchSlotReq;
import top.continew.admin.education.model.resp.SlotDetailResp;
import top.continew.admin.education.model.resp.SlotResp;
import top.continew.admin.education.service.SlotService;

/**
 * 课程管理管理 API
 *
 * @author don
 * @since 2025/04/25 23:24
 */
@Tag(name = "课程管理管理 API")
@RestController
@CrudRequestMapping(value = "/education/slot", api = {Api.PAGE, Api.GET, Api.CREATE, Api.UPDATE, Api.DELETE,
    Api.EXPORT})
public class SlotController extends BaseController<SlotService, SlotResp, SlotDetailResp, SlotQuery, SlotReq> {

    @Autowired
    private SlotService slotService;

    /**
     * 批量添加课程时间
     *
     * @param batchSlotReq 批量课程时间请求
     * @return 添加结果
     */
    @Operation(summary = "批量添加课程时间")
    @PostMapping("/batch")
    public R<List<SlotResp>> batchCreateSlot(@RequestBody BatchSlotReq batchSlotReq) {
        return R.ok(slotService.batchCreateSlot(batchSlotReq));
    }
}