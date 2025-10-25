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

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.education.model.query.StuCardQuery;
import top.continew.admin.education.model.req.StuCardBindReq;
import top.continew.admin.education.model.req.StuCardReq;
import top.continew.admin.education.model.resp.StuCardDetailResp;
import top.continew.admin.education.model.resp.StuCardResp;

import java.util.List;

/**
 * 会员绑卡业务接口
 *
 * @author don
 * @since 2025/05/10 22:11
 */
public interface StuCardService extends BaseService<StuCardResp, StuCardDetailResp, StuCardQuery, StuCardReq> {

    /**
     * 绑定会员卡
     *
     * @param req 绑定会员卡请求参数
     * @return 绑定结果
     */
    StuCardResp bindCard(StuCardBindReq req);

    /**
     * 获取会员可用的会员卡列表
     *
     * @param stuId 会员ID
     * @return 可用会员卡列表
     */
    List<StuCardResp> getAvailableCards(Long stuId);
}