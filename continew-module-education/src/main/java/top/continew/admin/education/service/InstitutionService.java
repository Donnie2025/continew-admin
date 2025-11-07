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

import top.continew.admin.education.model.resp.InstitutionResp;

import java.util.List;

/**
 * 机构业务接口
 *
 * @author don
 * @since 2025/11/07
 */
public interface InstitutionService {

    /**
     * 获取所有启用的机构列表
     *
     * @return 机构列表
     */
    List<InstitutionResp> listActiveInstitutions();

    /**
     * 获取所有机构列表
     *
     * @return 机构列表
     */
    List<InstitutionResp> listAll();

    /**
     * 获取当前激活的机构
     *
     * @return 激活的机构信息，如果没有激活的机构则返回 null
     */
    InstitutionResp getActiveInstitution();

    /**
     * 根据ID获取机构信息
     *
     * @param institutionId 机构ID
     * @return 机构信息，如果不存在则返回 null
     */
    InstitutionResp getById(Long institutionId);
}
