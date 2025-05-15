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

package top.continew.admin.education.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.TransactionMapper;
import top.continew.admin.education.model.entity.TransactionDO;
import top.continew.admin.education.model.query.TransactionQuery;
import top.continew.admin.education.model.req.TransactionReq;
import top.continew.admin.education.model.resp.TransactionDetailResp;
import top.continew.admin.education.model.resp.TransactionResp;
import top.continew.admin.education.service.TransactionService;

/**
 * 订单业务实现
 *
 * @author don
 * @since 2025/05/10 22:11
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl extends BaseServiceImpl<TransactionMapper, TransactionDO, TransactionResp, TransactionDetailResp, TransactionQuery, TransactionReq> implements TransactionService {}