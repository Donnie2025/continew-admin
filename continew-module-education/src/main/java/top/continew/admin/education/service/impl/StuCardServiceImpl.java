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
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.continew.admin.common.context.UserContextHolder;
import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.enums.TransactionTypeEnum;
import top.continew.admin.education.mapper.StuCardMapper;
import top.continew.admin.education.mapper.TransactionMapper;
import top.continew.admin.education.model.entity.StuCardDO;
import top.continew.admin.education.model.entity.TransactionDO;
import top.continew.admin.education.model.query.StuCardQuery;
import top.continew.admin.education.model.req.StuCardBindReq;
import top.continew.admin.education.model.req.StuCardReq;
import top.continew.admin.education.model.resp.StuCardDetailResp;
import top.continew.admin.education.model.resp.StuCardResp;
import top.continew.admin.education.service.StuCardService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * 会员绑卡业务实现
 *
 * @author don
 * @since 2025/05/10 22:11
 */
@Service
@RequiredArgsConstructor
public class StuCardServiceImpl extends BaseServiceImpl<StuCardMapper, StuCardDO, StuCardResp, StuCardDetailResp, StuCardQuery, StuCardReq> implements StuCardService {

    private final TransactionMapper transactionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StuCardResp bindCard(StuCardBindReq req) {
        // 1. 创建会员卡绑定记录
        StuCardDO stuCardDO = new StuCardDO();
        BeanUtil.copyProperties(req, stuCardDO);

        // 设置状态为启用
        stuCardDO.setStatus(1);
        stuCardDO.setCardStatus(1);

        // 根据卡类型设置次数或余额
        BigDecimal originalBalance = BigDecimal.ZERO;
        BigDecimal newBalance = originalBalance.add(req.getBalance());

        // cardType: TL,TU 为次卡，BL,BU 为储蓄卡
        if ("TL".equals(req.getCardType()) || "TU".equals(req.getCardType())) {
            // 次卡：设置剩余次数
            stuCardDO.setRemainTimes(req.getBalance().intValue());
            stuCardDO.setRemainBalance(BigDecimal.ZERO);
        } else {
            // 储蓄卡：设置剩余余额
            stuCardDO.setRemainTimes(0);
            stuCardDO.setRemainBalance(req.getBalance());
        }

        // 保存会员卡绑定记录
        baseMapper.insert(stuCardDO);

        // 2. 创建交易记录
        TransactionDO transactionDO = new TransactionDO();
        transactionDO.setStuCardId(stuCardDO.getId());
        transactionDO.setStuId(req.getStuId());
        transactionDO.setStuName(req.getStuName());
        transactionDO.setCardId(req.getCardId());
        transactionDO.setCardTitle(req.getCardTitle());
        transactionDO.setType(TransactionTypeEnum.BIND.getCode()); // 绑定类型
        transactionDO.setCreditAmount(req.getBalance()); // 充值次数
        transactionDO.setBeforeAmount(originalBalance); // 变动前余额
        transactionDO.setAfterAmount(newBalance); // 变动后余额
        transactionDO.setActualAmount(req.getActualAmount()); // 实收金额
        transactionDO.setRemark(req.getRemark());

        // 设置操作人信息
        Long currentUserId = UserContextHolder.getUserId();
        String currentUsername = UserContextHolder.getUsername();
        transactionDO.setOperatorId(currentUserId);
        transactionDO.setOperatorName(currentUsername);

        // 保存交易记录
        transactionMapper.insert(transactionDO);

        // 返回绑定结果
        return BeanUtil.copyProperties(stuCardDO, StuCardResp.class);
    }

    @Override
    public List<StuCardResp> getAvailableCards(Long stuId) {
        if (stuId == null) {
            return new ArrayList<>();
        }

        // 构建查询条件
        LambdaQueryWrapper<StuCardDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StuCardDO::getStuId, stuId)
            .eq(StuCardDO::getStatus, 1)  // 启用状态
            .eq(StuCardDO::getCardStatus, 1)  // 卡状态启用
            .and(wrapper -> wrapper.gt(StuCardDO::getRemainTimes, 0)  // 次数大于0
                .or()
                .gt(StuCardDO::getRemainBalance, BigDecimal.ZERO)  // 或余额大于0
            )
            .and(wrapper -> wrapper.isNull(StuCardDO::getExpireDate)  // 无过期日期
                .or()
                .ge(StuCardDO::getExpireDate, LocalDate.now())  // 或未过期
            );

        // 查询结果
        List<StuCardDO> stuCardList = baseMapper.selectList(queryWrapper);

        // 转换为响应对象
        return stuCardList.stream()
            .map(card -> BeanUtil.copyProperties(card, StuCardResp.class))
            .collect(Collectors.toList());
    }
}