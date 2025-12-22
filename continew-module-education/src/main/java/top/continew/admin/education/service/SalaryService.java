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
import top.continew.admin.education.model.query.SalaryQuery;
import top.continew.admin.education.model.req.SalaryBatchImportReq;
import top.continew.admin.education.model.req.SalaryBatchSettleReq;
import top.continew.admin.education.model.req.SalaryReq;
import top.continew.admin.education.model.resp.SalaryBatchImportResp;
import top.continew.admin.education.model.resp.SalaryDetailResp;
import top.continew.admin.education.model.resp.SalaryResp;

/**
 * 薪资业务接口
 *
 * @author don
 * @since 2025/05/13 22:43
 */
public interface SalaryService extends BaseService<SalaryResp, SalaryDetailResp, SalaryQuery, SalaryReq> {

    /**
     * 批量导入教师课程数量
     *
     * @param req 批量导入请求参数
     * @return 导入结果
     */
    SalaryBatchImportResp batchImport(SalaryBatchImportReq req);

    /**
     * 批量结算薪资
     *
     * @param req 批量结算请求参数
     * @return 成功结算的记录数量
     */
    int batchSettle(SalaryBatchSettleReq req);
}