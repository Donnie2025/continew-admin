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
import top.continew.admin.education.mapper.AccountMapper;
import top.continew.admin.education.mapper.StuCardMapper;
import top.continew.admin.education.mapper.TransactionMapper;
import top.continew.admin.education.model.entity.AccountDO;
import top.continew.admin.education.model.entity.StuCardDO;
import top.continew.admin.education.model.entity.TransactionDO;
import top.continew.admin.education.model.query.StuCardQuery;
import top.continew.admin.education.model.req.StuCardBindReq;
import top.continew.admin.education.model.req.StuCardReq;
import top.continew.admin.education.model.resp.CardPurchaseRecordResp;
import top.continew.admin.education.model.resp.StuCardDetailResp;
import top.continew.admin.education.model.resp.StuCardResp;
import top.continew.admin.education.enums.AccountTypeEnum;
import top.continew.admin.education.enums.TransactionDirectionEnum;
import top.continew.admin.education.enums.TransactionTypeEnum;
import top.continew.admin.education.service.StuCardService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

    private final AccountMapper accountMapper;
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

        // 2. 查询学生的 PAID 课时账户（正常购买类型）
        LambdaQueryWrapper<AccountDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountDO::getStudentId, req.getStuId())
            .eq(AccountDO::getAccountType, AccountTypeEnum.PAID.getCode())
            .eq(AccountDO::getStatus, 1);
        AccountDO existingAccount = accountMapper.selectOne(queryWrapper);

        AccountDO accountDO;
        String transType;
        BigDecimal originalBalance;

        if (existingAccount != null) {
            // 3. 账户已存在：充値（剖加课时余额）
            accountDO = existingAccount;
            transType = TransactionTypeEnum.RECHARGE.getCode();
            originalBalance = accountDO.getBalance() != null ? accountDO.getBalance() : BigDecimal.ZERO;

            BigDecimal newBalance = originalBalance.add(req.getBalance());
            accountDO.setBalance(newBalance);

            // 如果有限期，更新到期日
            if (cardDetail.getInitDays() != null && cardDetail.getInitDays() > 0) {
                LocalDate currentExpireDate = accountDO.getExpireDate();
                LocalDate today = LocalDate.now();
                LocalDate baseDate = (currentExpireDate == null || currentExpireDate.isBefore(today) || originalBalance
                    .compareTo(BigDecimal.ZERO) == 0) ? today : currentExpireDate;
                accountDO.setExpireDate(baseDate.plusDays(cardDetail.getInitDays()));
            }

            accountMapper.updateById(accountDO);
        } else {
            // 4. 账户不存在：创建 PAID 账户
            accountDO = new AccountDO();
            transType = TransactionTypeEnum.BIND.getCode();
            originalBalance = BigDecimal.ZERO;

            accountDO.setStudentId(req.getStuId());
            accountDO.setStudentName(req.getStuName());
            accountDO.setAccountType(AccountTypeEnum.PAID.getCode());
            accountDO.setBalance(req.getBalance());
            accountDO.setStatus(1);
            accountDO.setRemark(req.getRemark());

            if (cardDetail.getInitDays() != null && cardDetail.getInitDays() > 0) {
                accountDO.setExpireDate(LocalDate.now().plusDays(cardDetail.getInitDays()));
            }

            accountMapper.insert(accountDO);
        }

        // 5. 创建交易记录
        BigDecimal newBalance = accountDO.getBalance();
        TransactionDO transactionDO = new TransactionDO();
        transactionDO.setAccountId(accountDO.getId());
        transactionDO.setStudentId(req.getStuId());
        transactionDO.setStudentName(req.getStuName());
        transactionDO.setCardTitle(req.getCardTitle());
        transactionDO.setTransType(transType);
        transactionDO.setDirection(TransactionDirectionEnum.CREDIT.getCode());
        transactionDO.setAmount(req.getBalance());
        transactionDO.setBalance(newBalance);
        transactionDO.setCashAmount(req.getActualAmount());
        transactionDO.setRemark(existingAccount != null ? "账户充値" : "首次开户");

        // 手动设置交易记录的审计字段
        transactionDO.setCreateUser(req.getStuId());
        transactionDO.setCreateTime(java.time.LocalDateTime.now());
        transactionDO.setUpdateUser(req.getStuId());
        transactionDO.setUpdateTime(java.time.LocalDateTime.now());

        // 保存交易记录
        transactionMapper.insert(transactionDO);

        // 6. 返回结果
        StuCardResp resp = BeanUtil.copyProperties(accountDO, StuCardResp.class);
        resp.setCardTitle(req.getCardTitle());
        return resp;
    }

    @Override
    public List<StuCardResp> getAvailableCards(Long stuId) {
        if (stuId == null) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<AccountDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountDO::getStudentId, stuId).eq(AccountDO::getStatus, 1);
        List<AccountDO> accountList = accountMapper.selectList(queryWrapper);

        return accountList.stream().map(account -> {
            StuCardResp resp = BeanUtil.copyProperties(account, StuCardResp.class);
            return resp;
        }).collect(Collectors.toList());
    }

    @Override
    public List<CardPurchaseRecordResp> getPurchaseHistory(Long stuId, int limit) {
        if (stuId == null) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<TransactionDO> qw = new LambdaQueryWrapper<>();
        qw.eq(TransactionDO::getStudentId, stuId)
            .in(TransactionDO::getTransType, Arrays.asList(TransactionTypeEnum.BIND
                .getCode(), TransactionTypeEnum.RECHARGE.getCode()))
            .ge(TransactionDO::getCreateTime, LocalDateTime.now().minusYears(1))
            .orderByDesc(TransactionDO::getCreateTime)
            .last("LIMIT " + limit);
        return transactionMapper.selectList(qw).stream().map(tx -> {
            CardPurchaseRecordResp r = new CardPurchaseRecordResp();
            r.setId(tx.getId());
            r.setCardTitle(tx.getCardTitle());
            r.setAmount(tx.getAmount());
            r.setPurchasePrice(tx.getCashAmount()); // cashAmount = 实际现金金额
            r.setTransType(tx.getTransType());
            r.setCreateTime(tx.getCreateTime());
            r.setRemark(tx.getRemark());
            return r;
        }).collect(Collectors.toList());
    }
}