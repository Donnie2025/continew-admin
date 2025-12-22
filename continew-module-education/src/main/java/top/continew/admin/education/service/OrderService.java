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

package top.continew.admin.education.service;

import top.continew.admin.education.model.resp.OrderDetailResp;
import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.education.model.query.OrderQuery;
import top.continew.admin.education.model.req.OrderReq;
import top.continew.admin.education.model.resp.OrderResp;

/**
 * 订单业务接口
 *
 * @author don
 * @since 2025/11/26 22:05
 */
public interface OrderService extends BaseService<OrderResp, OrderDetailResp, OrderQuery, OrderReq> {

    /**
     * 创建订单（小程序端）
     *
     * @param req 创建订单请求
     * @return 订单详情
     */
    OrderDetailResp createOrder(OrderReq req);

    /**
     * 确认订单入账
     *
     * @param orderId 订单ID
     */
    void confirmOrder(Long orderId);

}