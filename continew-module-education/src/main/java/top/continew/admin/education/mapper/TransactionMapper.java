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

package top.continew.admin.education.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import top.continew.starter.data.mp.base.BaseMapper;
import top.continew.admin.education.model.entity.TransactionDO;
import top.continew.admin.education.model.query.TransactionQuery;
import top.continew.admin.education.model.resp.TransactionResp;

/**
 * 订单 Mapper
 *
 * @author don
 * @since 2025/05/10 22:11
 */
public interface TransactionMapper extends BaseMapper<TransactionDO> {

    /**
     * 分页查询交易记录
     */
    IPage<TransactionResp> selectPageList(IPage<TransactionResp> page, @Param("query") TransactionQuery query);
}