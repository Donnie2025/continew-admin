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
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.common.context.UserContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import top.continew.admin.education.enums.AccountTypeEnum;
import top.continew.admin.education.mapper.CardMapper;
import top.continew.admin.education.mapper.OrderMapper;
import top.continew.admin.education.mapper.AccountMapper;
import top.continew.admin.education.mapper.TransactionMapper;
import top.continew.admin.education.model.entity.AccountDO;
import top.continew.admin.education.model.entity.CardDO;
import top.continew.admin.education.model.entity.OrderDO;
import top.continew.admin.education.model.entity.TransactionDO;
import top.continew.admin.education.model.query.OrderQuery;
import top.continew.admin.education.model.req.OrderReq;
import top.continew.admin.education.model.resp.OrderDetailResp;
import top.continew.admin.education.model.resp.OrderResp;
import top.continew.admin.education.enums.TransactionDirectionEnum;
import top.continew.admin.education.enums.TransactionTypeEnum;
import top.continew.admin.education.service.OrderService;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.extension.crud.service.BaseServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单业务实现
 *
 * @author don
 * @since 2025/11/26 22:05
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends BaseServiceImpl<OrderMapper, OrderDO, OrderResp, OrderDetailResp, OrderQuery, OrderReq> implements OrderService {

    private final CardMapper cardMapper;
    private final AccountMapper accountMapper;
    private final OrderMapper orderMapper;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDetailResp createOrder(OrderReq req) {
        // 1. 查询会员卡信息
        CardDO card = cardMapper.selectById(req.getCardId());
        if (card == null) {
            throw new BusinessException("会员卡不存在");
        }
        if (card.getStatus() != 1) {
            throw new BusinessException("会员卡已下架");
        }

        // 2. 获取当前登录学生信息
        Long stuId = UserContextHolder.getUserId();
        if (stuId == null) {
            throw new BusinessException("用户未登录，请先登录后再创建订单");
        }
        String stuName = UserContextHolder.getUsername();

        // 3. 查找或创建课时账户（同一学生每种 accountType 唯一）
        LambdaQueryWrapper<AccountDO> existCheck = new LambdaQueryWrapper<>();
        existCheck.eq(AccountDO::getStudentId, stuId).eq(AccountDO::getAccountType, AccountTypeEnum.PAID.getCode());
        AccountDO account = accountMapper.selectOne(existCheck);
        if (account == null) {
            // 不存在则创建待激活账户
            account = new AccountDO();
            account.setStudentId(stuId);
            account.setStudentName(stuName);
            account.setAccountType(AccountTypeEnum.PAID.getCode());
            account.setBalance(BigDecimal.ZERO);
            account.setExpireDate(null);
            account.setStatus(0); // 待激活（学生端不可见）
            account.setRemark(card.getTitle());
            account.setCreateUser(stuId);
            accountMapper.insert(account);
        }

        // 4. 生成订单编号
        String orderNo = "ORD" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + IdUtil.randomUUID()
            .substring(0, 6);

        // 5. 创建订单
        OrderDO order = new OrderDO();
        order.setOrderNo(orderNo);
        order.setStudentId(stuId);
        order.setStudentName(stuName);
        order.setCardId(card.getId());
        order.setCardTitle(card.getTitle());
        order.setOrderPrice(card.getPrice());
        order.setPaymentType(req.getPaymentType());
        order.setOrderStatus("PENDING"); // 待确认
        order.setAccountId(account.getId()); // 关联课时账户
        order.setPaymentTime(LocalDateTime.now());
        order.setInstitutionId(card.getInstitutionId());
        order.setCreateUser(stuId);
        orderMapper.insert(order);

        // 6. 返回订单详情
        OrderDetailResp resp = new OrderDetailResp();
        BeanUtil.copyProperties(order, resp);
        return resp;
    }

    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(Long orderId) {
        // 1. 查询订单
        OrderDO order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"PENDING".equals(order.getOrderStatus())) {
            throw new BusinessException("订单状态不正确");
        }

        // 2. 查询课时账户
        AccountDO account = accountMapper.selectById(order.getAccountId());
        if (account == null) {
            throw new BusinessException("课时账户不存在");
        }

        // 3. 重新查询会员卡模板获取最新配置
        CardDO card = cardMapper.selectById(order.getCardId());
        if (card == null) {
            throw new BusinessException("会员卡模板不存在");
        }

        // 4. 激活会员卡（赋值）
        LocalDate activateDate = LocalDate.now();
        LocalDate expireDate = null;
        if (card.getInitDays() != null && card.getInitDays() > 0) {
            expireDate = activateDate.plusDays(card.getInitDays());
        }

        // 激活账户：设置初始余额和到期日
        AccountDO updateAccount = new AccountDO();
        updateAccount.setId(account.getId());
        updateAccount.setBalance(card.getInitBalance() != null ? card.getInitBalance() : BigDecimal.ZERO);
        updateAccount.setExpireDate(expireDate);
        updateAccount.setStatus(1); // 学生端可见
        updateAccount.setUpdateUser(UserContextHolder.getUserId());
        accountMapper.updateById(updateAccount);

        // 5. 生成交易流水
        TransactionDO transaction = new TransactionDO();
        transaction.setAccountId(account.getId());
        transaction.setStudentId(order.getStudentId());
        transaction.setStudentName(order.getStudentName());
        transaction.setCardTitle(order.getCardTitle());
        transaction.setTransType(TransactionTypeEnum.BIND.getCode());
        transaction.setDirection(TransactionDirectionEnum.CREDIT.getCode());

        // 统一使用 initBalance 记录课时变动数量
        BigDecimal amount = card.getInitBalance() != null ? card.getInitBalance() : BigDecimal.ZERO;

        transaction.setAmount(amount); // 课时变动数量
        transaction.setBalance(amount); // 交易后余额快照
        transaction.setCashAmount(order.getOrderPrice()); // 实际支付金额
        transaction.setRemark("订单确认激活：" + order.getOrderNo());
        transaction.setCreateUser(UserContextHolder.getUserId());
        transactionMapper.insert(transaction);

        // 6. 更新订单状态
        order.setOrderStatus("COMPLETED"); // 已完成
        order.setConfirmTime(LocalDateTime.now());
        order.setUpdateUser(UserContextHolder.getUserId());
        orderMapper.updateById(order);
    }
}