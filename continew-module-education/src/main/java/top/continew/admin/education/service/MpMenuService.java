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

import top.continew.admin.education.model.req.MpMenuReq;
import top.continew.admin.education.model.resp.MpMenuResp;

/**
 * 微信公众号菜单业务接口
 *
 * @author don
 * @since 2025/05/02
 */
public interface MpMenuService {

    /**
     * 查询当前菜单
     *
     * @return 菜单信息
     */
    MpMenuResp getMenu();

    /**
     * 创建/更新菜单
     *
     * @param req 菜单请求参数
     */
    void saveMenu(MpMenuReq req);

    /**
     * 删除菜单
     */
    void deleteMenu();
}
