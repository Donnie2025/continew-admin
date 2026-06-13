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

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.continew.admin.education.mapper.PaymentChannelMapper;
import top.continew.admin.education.model.entity.PaymentChannelDO;
import top.continew.admin.education.model.resp.PaymentChannelResp;
import top.continew.admin.education.service.PaymentChannelService;
import top.continew.starter.core.exception.BusinessException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 支付渠道服务实现
 *
 * @author don
 * @since 2026/06/09
 */
@Service
@RequiredArgsConstructor
public class PaymentChannelServiceImpl implements PaymentChannelService {

    private final PaymentChannelMapper paymentChannelMapper;

    @Override
    public List<PaymentChannelResp> listActiveChannels() {
        LambdaQueryWrapper<PaymentChannelDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentChannelDO::getStatus, 1)
            .orderByAsc(PaymentChannelDO::getSort)
            .orderByAsc(PaymentChannelDO::getId);

        List<PaymentChannelDO> list = paymentChannelMapper.selectList(queryWrapper);

        return list.stream().map(this::toResp).collect(Collectors.toList());
    }

    @Override
    public PaymentChannelResp getChannelById(Long id) {
        PaymentChannelDO channel = paymentChannelMapper.selectById(id);
        if (channel == null) {
            throw new BusinessException("支付渠道不存在");
        }
        if (channel.getStatus() != 1) {
            throw new BusinessException("支付渠道已禁用");
        }
        return toResp(channel);
    }

    /**
     * 实体转响应对象
     */
    private PaymentChannelResp toResp(PaymentChannelDO channel) {
        PaymentChannelResp resp = new PaymentChannelResp();
        BeanUtil.copyProperties(channel, resp);
        return resp;
    }
}
