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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.CardMapper;
import top.continew.admin.education.model.entity.CardDO;
import top.continew.admin.education.model.query.CardQuery;
import top.continew.admin.education.model.req.CardReq;
import top.continew.admin.education.model.resp.CardDetailResp;
import top.continew.admin.education.model.resp.CardResp;
import top.continew.admin.education.service.CardService;

import java.util.List;

/**
 * 会员卡管理业务实现
 *
 * @author don
 * @since 2025/05/10 00:06
 */
@Service
@RequiredArgsConstructor
public class CardServiceImpl extends BaseServiceImpl<CardMapper, CardDO, CardResp, CardDetailResp, CardQuery, CardReq> implements CardService {

    @Override
    public List<CardResp> listActiveCards() {
        // 查询状态为1的会员卡，首先按sort字段降序排列，如果sort相同，则按更新时间倒序排列
        LambdaQueryWrapper<CardDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CardDO::getStatus, 1).orderByDesc(CardDO::getSort).orderByDesc(CardDO::getUpdateTime);
        List<CardDO> cardDOList = baseMapper.selectList(queryWrapper);

        // 转换为响应对象，initTimes 从 initBalance 映射（init_times 已合并到 init_balance）
        List<CardResp> result = BeanUtil.copyToList(cardDOList, CardResp.class);
        result.forEach(resp -> {
            if (resp.getInitBalance() != null) {
                resp.setInitTimes(resp.getInitBalance().intValue());
            }
        });
        return result;
    }

    @Override
    public CardDetailResp get(Long id) {
        CardDetailResp detail = super.get(id);
        if (detail != null && detail.getInitBalance() != null) {
            detail.setInitTimes(detail.getInitBalance().intValue());
        }
        return detail;
    }
}