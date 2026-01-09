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

import java.time.LocalDate;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.service.CardService;
import top.continew.admin.education.constants.CardTypeConstants;
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
    private final CardService cardService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StuCardResp bindCard(StuCardBindReq req) {
        // 1. 获取会员卡模板信息
        var cardDetail = cardService.get(req.getCardId());
        if (cardDetail == null) {
            throw new RuntimeException("会员卡不存在");
        }

        // 2. 检查用户是否已经绑定过该会员卡
        LambdaQueryWrapper<StuCardDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StuCardDO::getStuId, req.getStuId())
            .eq(StuCardDO::getCardId, req.getCardId())
            .eq(StuCardDO::getStatus, 1)
            .eq(StuCardDO::getCardStatus, 1); // 确保卡状态也是启用的
        StuCardDO existingCard = baseMapper.selectOne(queryWrapper);

        // 添加调试日志
        System.out.println("=== 购买会员卡调试信息 ===");
        System.out.println("学生ID: " + req.getStuId());
        System.out.println("卡片ID: " + req.getCardId());
        System.out.println("查询到的现有卡片: " + (existingCard != null ? "存在" : "不存在"));
        if (existingCard != null) {
            System.out.println("现有卡片余额: " + existingCard.getBalance());
            System.out.println("现有卡片有效期: " + existingCard.getExpireDate());
        }

        StuCardDO stuCardDO;
        String transType;
        BigDecimal originalBalance;

        if (existingCard != null) {
            // 3. 已绑定：更新余额和有效期
            stuCardDO = existingCard;
            transType = "recharge";
            originalBalance = stuCardDO.getBalance() != null ? stuCardDO.getBalance() : BigDecimal.ZERO;

            // 增加余额
            BigDecimal newBalance = originalBalance.add(req.getBalance());
            stuCardDO.setBalance(newBalance);

            // 如果是有限期卡类型，需要处理有效期
            if (CardTypeConstants.isLimitedCard(cardDetail.getType()) && cardDetail
                .getInitDays() != null && cardDetail.getInitDays() > 0) {
                LocalDate currentExpireDate = stuCardDO.getExpireDate();
                LocalDate today = LocalDate.now();
                
                // 判断是否余额为0（包括次数和金额）
                boolean isZeroBalance = false;
                if (CardTypeConstants.isTimesCard(cardDetail.getType())) {
                    // 次卡类型：检查余额（作为次数使用）
                    isZeroBalance = (originalBalance.compareTo(BigDecimal.ZERO) == 0);
                } else if (CardTypeConstants.isBalanceCard(cardDetail.getType())) {
                    // 储蓄卡类型：检查余额
                    isZeroBalance = (originalBalance.compareTo(BigDecimal.ZERO) == 0);
                }

                // 如果余额为0，或者当前有效期已过期或为空，从今天开始计算
                LocalDate baseDate;
                if (isZeroBalance || currentExpireDate == null || currentExpireDate.isBefore(today)) {
                    baseDate = today;
                    System.out.println("余额为0或已过期，有效期从今天开始计算: " + today);
                } else {
                    baseDate = currentExpireDate;
                    System.out.println("有余额且未过期，有效期从原到期日延长: " + currentExpireDate);
                }
                
                LocalDate newExpireDate = baseDate.plusDays(cardDetail.getInitDays());
                stuCardDO.setExpireDate(newExpireDate);
                System.out.println("新的到期日期: " + newExpireDate);
            }

            // 更新记录
            stuCardDO.setUpdateUser(req.getStuId());
            stuCardDO.setUpdateTime(java.time.LocalDateTime.now());
            baseMapper.updateById(stuCardDO);
        } else {
            // 4. 未绑定：创建新的绑定记录
            System.out.println("未找到现有卡片，创建新的绑定记录");
            stuCardDO = new StuCardDO();
            transType = "bind";
            originalBalance = BigDecimal.ZERO;

            BeanUtil.copyProperties(req, stuCardDO);

            // 手动设置字段名不匹配的属性
            stuCardDO.setCardName(req.getCardTitle()); // cardTitle -> cardName
            stuCardDO.setCardType(cardDetail.getType()); // 从模板获取卡类型

            // 设置激活日期和购买价格
            stuCardDO.setActivateDate(LocalDate.now());
            stuCardDO.setPurchasePrice(req.getActualAmount());

            // 设置有效期（如果是有限期卡类型）
            if (CardTypeConstants.isLimitedCard(cardDetail.getType()) && cardDetail.getInitDays() != null && cardDetail
                .getInitDays() > 0) {
                stuCardDO.setExpireDate(LocalDate.now().plusDays(cardDetail.getInitDays()));
            }

            // 设置余额
            stuCardDO.setBalance(req.getBalance());

            // 设置状态为启用
            stuCardDO.setStatus(1);
            stuCardDO.setCardStatus(1);

            // 手动设置审计字段
            stuCardDO.setCreateUser(req.getStuId());
            stuCardDO.setCreateTime(java.time.LocalDateTime.now());
            stuCardDO.setUpdateUser(req.getStuId());
            stuCardDO.setUpdateTime(java.time.LocalDateTime.now());

            // 保存会员卡绑定记录
            baseMapper.insert(stuCardDO);
        }

        // 5. 创建交易记录
        BigDecimal newBalance = stuCardDO.getBalance();
        TransactionDO transactionDO = new TransactionDO();
        transactionDO.setStuCardId(stuCardDO.getId());
        transactionDO.setStuId(req.getStuId());
        transactionDO.setStuName(req.getStuName());
        transactionDO.setCardTitle(req.getCardTitle());
        transactionDO.setTransType(transType);

        // 设置交易金额和余额变动
        transactionDO.setBeforeAmt(originalBalance);
        transactionDO.setAfterAmt(newBalance);
        transactionDO.setAmount(req.getBalance()); // 本次充值金额
        transactionDO.setRemark(existingCard != null ? "会员卡续费充值" : "首次购买会员卡");

        // 手动设置交易记录的审计字段
        transactionDO.setCreateUser(req.getStuId());
        transactionDO.setCreateTime(java.time.LocalDateTime.now());
        transactionDO.setUpdateUser(req.getStuId());
        transactionDO.setUpdateTime(java.time.LocalDateTime.now());

        // 保存交易记录
        transactionMapper.insert(transactionDO);

        // 6. 返回结果
        StuCardResp resp = BeanUtil.copyProperties(stuCardDO, StuCardResp.class);
        resp.setCardTitle(stuCardDO.getCardName());
        return resp;
    }

    @Override
    public List<StuCardResp> getAvailableCards(Long stuId) {
        if (stuId == null) {
            return new ArrayList<>();
        }

        // 构建查询条件 - 先简化条件，只查询该学生的所有会员卡
        LambdaQueryWrapper<StuCardDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StuCardDO::getStuId, stuId).eq(StuCardDO::getStatus, 1);  // 只要求启用状态

        // 查询结果
        List<StuCardDO> stuCardList = baseMapper.selectList(queryWrapper);

        // 转换为响应对象，手动处理字段映射
        return stuCardList.stream().map(card -> {
            StuCardResp resp = BeanUtil.copyProperties(card, StuCardResp.class);
            // 手动设置字段名不匹配的属性
            resp.setCardTitle(card.getCardName()); // cardName -> cardTitle
            return resp;
        }).collect(Collectors.toList());
    }
}