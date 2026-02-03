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
import top.continew.starter.extension.crud.validation.CrudValidationGroup;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import top.continew.starter.extension.crud.annotation.CrudRequestMapping;
import top.continew.admin.common.controller.BaseController;
import top.continew.admin.education.model.entity.BookingDO;
import top.continew.admin.education.model.query.BookingQuery;
import top.continew.admin.education.model.req.BookingReq;
import top.continew.admin.education.model.resp.BookingDetailResp;
import top.continew.admin.education.model.resp.BookingResp;
import top.continew.admin.education.service.BookingService;

import cn.hutool.core.bean.BeanUtil;

/**
 * 预约管理 API
 *
 * @author don
 * @since 2025/05/23 23:25
 */
@Tag(name = "预约管理 API")
@RestController
@CrudRequestMapping(value = "/education/booking", api = {Api.PAGE, Api.GET, Api.UPDATE, Api.DELETE, Api.EXPORT})
public class BookingController extends BaseController<BookingService, BookingResp, BookingDetailResp, BookingQuery, BookingReq> {

    /**
     * 创建预约（直接调用createBookingWithTransaction）
     *
     * @param req 创建参数
     * @return ID
     */
    @Operation(summary = "创建预约", description = "创建预约，使用完整的业务验证和事务处理")
    @ResponseBody
    @PostMapping
    public Long createBooking(@Validated(CrudValidationGroup.Create.class) @RequestBody BookingReq req) {
        // 转换为BookingDO
        BookingDO booking = BeanUtil.copyProperties(req, BookingDO.class);
        
        // 直接调用createBookingWithTransaction方法
        return baseService.createBookingWithTransaction(booking);
    }
}