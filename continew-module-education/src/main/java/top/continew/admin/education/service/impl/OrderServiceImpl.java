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
import top.continew.admin.education.enums.OrderStatus;
import top.continew.admin.education.mapper.CardMapper;
import top.continew.admin.education.mapper.OrderMapper;
import top.continew.admin.education.mapper.AccountMapper;
import top.continew.admin.education.mapper.TransactionMapper;
import top.continew.admin.education.mapper.PaymentChannelMapper;
import top.continew.admin.education.mapper.StudentMapper;
import top.continew.admin.education.model.entity.AccountDO;
import top.continew.admin.education.model.entity.CardDO;
import top.continew.admin.education.model.entity.OrderDO;
import top.continew.admin.education.model.entity.TransactionDO;
import top.continew.admin.education.model.entity.PaymentChannelDO;
import top.continew.admin.education.model.entity.StudentDO;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    private final PaymentChannelMapper paymentChannelMapper;
    private final StudentMapper studentMapper;

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

        // 2. 查询支付渠道信息
        PaymentChannelDO paymentChannel = null;
        if (req.getPaymentChannelId() != null) {
            paymentChannel = paymentChannelMapper.selectById(req.getPaymentChannelId());
            if (paymentChannel == null) {
                throw new BusinessException("支付渠道不存在");
            }
            if (paymentChannel.getStatus() != 1) {
                throw new BusinessException("支付渠道已禁用");
            }
        }

        // 3. 获取学生信息
        // 如果请求中提供了studentId，使用该ID（管理员为学生创建订单）
        // 否则使用当前登录用户ID（学生自己创建订单）
        Long stuId = req.getStudentId();
        if (stuId == null) {
            stuId = UserContextHolder.getUserId();
            if (stuId == null) {
                throw new BusinessException("用户未登录，请先登录后再创建订单");
            }
        }

        // 查询学生信息获取真实姓名
        StudentDO student = studentMapper.selectById(stuId);
        if (student == null) {
            throw new BusinessException("学生信息不存在");
        }
        String stuName = student.getName();

        // 4. 查找或创建课时账户（同一学生每种 accountType 唯一）
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

        // 5. 生成订单编号
        String orderNo = "ORD" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + IdUtil.randomUUID()
            .substring(0, 6);

        // 6. 创建订单
        OrderDO order = new OrderDO();
        order.setOrderNo(orderNo);
        order.setStudentId(stuId);
        order.setStudentName(stuName);
        order.setCardId(card.getId());
        order.setCardTitle(card.getTitle());
        order.setOrderPrice(card.getPrice());

        // 设置支付渠道信息
        if (paymentChannel != null) {
            order.setPaymentChannelId(paymentChannel.getId());
            order.setPaymentChannelName(paymentChannel.getChannelName());
            order.setPaymentMethod(paymentChannel.getPaymentType());
            order.setPaymentType(paymentChannel.getChannelCode()); // 保留向下兼容
        } else if (req.getPaymentType() != null) {
            // 向下兼容：如果没有传 paymentChannelId，使用旧的 paymentType
            order.setPaymentType(req.getPaymentType());
        }

        order.setOrderStatus(OrderStatus.PENDING); // 待确认
        order.setAccountId(account.getId()); // 关联课时账户
        order.setPaymentTime(LocalDateTime.now());
        order.setInstitutionId(card.getInstitutionId());
        order.setCreateUser(stuId);
        orderMapper.insert(order);

        // 7. 返回订单详情
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
        if (!OrderStatus.PENDING.equals(order.getOrderStatus())) {
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
        LocalDate now = LocalDate.now();
        LocalDate expireDate = null;

        // 根据会员卡有效天数和账户当前过期时间计算新的过期时间
        if (card.getInitDays() != null && card.getInitDays() > 0) {
            LocalDate currentExpireDate = account.getExpireDate();

            if (currentExpireDate == null) {
                // 规则1：第一次激活，过期时间 = 当前时间 + 会员卡有效天数
                expireDate = now.plusDays(card.getInitDays());
            } else if (currentExpireDate.isBefore(now)) {
                // 规则2：账户已过期，过期时间 = 当前时间 + 会员卡有效天数
                expireDate = now.plusDays(card.getInitDays());
            } else {
                // 账户未过期，判断距离当前时间是否超过一年
                LocalDate oneYearLater = now.plusYears(1);
                if (currentExpireDate.isBefore(oneYearLater) || currentExpireDate.isEqual(oneYearLater)) {
                    // 规则3：过期时间距离当前时间不超过一年，过期时间 = 当前过期时间 + 会员卡有效天数
                    expireDate = currentExpireDate.plusDays(card.getInitDays());
                }
                // 规则4：过期时间距离当前时间超过一年，不更新过期时间（expireDate 保持为 null）
            }
        }

        // 计算充值金额
        BigDecimal rechargeAmount = card.getInitBalance() != null ? card.getInitBalance() : BigDecimal.ZERO;

        // 计算新余额：原余额 + 充值金额
        BigDecimal newBalance = account.getBalance().add(rechargeAmount);

        // 更新账户：累加余额和更新到期日
        AccountDO updateAccount = new AccountDO();
        updateAccount.setId(account.getId());
        updateAccount.setBalance(newBalance); // 累加余额

        // 更新到期日：仅在计算出新的过期时间时更新
        if (expireDate != null) {
            updateAccount.setExpireDate(expireDate);
        }

        updateAccount.setStatus(1); // 学生端可见
        updateAccount.setUpdateUser(UserContextHolder.getUserId());
        accountMapper.updateById(updateAccount);

        // 5. 生成交易流水
        TransactionDO transaction = new TransactionDO();
        transaction.setAccountId(account.getId());
        transaction.setStudentId(order.getStudentId());
        transaction.setStudentName(order.getStudentName());
        transaction.setCardTitle(order.getCardTitle());
        transaction.setTransType(TransactionTypeEnum.RECHARGE.getCode());
        transaction.setDirection(TransactionDirectionEnum.CREDIT.getCode());

        transaction.setAmount(rechargeAmount); // 充值金额
        transaction.setBalance(newBalance); // 交易后余额快照（新余额）
        transaction.setCashAmount(order.getOrderPrice()); // 实际支付金额
        transaction.setRemark("订单确认激活：" + order.getOrderNo());
        transaction.setCreateUser(UserContextHolder.getUserId());
        transactionMapper.insert(transaction);

        // 6. 更新订单状态
        order.setOrderStatus(OrderStatus.COMPLETED); // 已完成
        order.setConfirmTime(LocalDateTime.now());
        order.setUpdateUser(UserContextHolder.getUserId());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo) {
        // 1. 查询订单
        LambdaQueryWrapper<OrderDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDO::getOrderNo, orderNo);
        OrderDO order = orderMapper.selectOne(queryWrapper);

        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 只有待确认状态的订单才能取消
        if (!OrderStatus.PENDING.equals(order.getOrderStatus())) {
            throw new BusinessException("订单状态不正确，无法取消");
        }

        // 3. 更新订单状态为已取消
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setUpdateUser(UserContextHolder.getUserId());
        order.setRemark("支付失败自动取消");
        orderMapper.updateById(order);
    }

    @Override
    public List<OrderResp> getStudentOrders(Long studentId, int limit) {
        if (studentId == null) {
            return new ArrayList<>();
        }

        // 查询近1年内的订单，状态为 COMPLETED 或 PENDING
        LambdaQueryWrapper<OrderDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDO::getStudentId, studentId)
            .in(OrderDO::getOrderStatus, Arrays.asList(OrderStatus.COMPLETED, OrderStatus.PENDING))
            .ge(OrderDO::getCreateTime, LocalDateTime.now().minusYears(1))
            .orderByDesc(OrderDO::getCreateTime)
            .last("LIMIT " + limit);

        List<OrderDO> orders = orderMapper.selectList(queryWrapper);

        // 转换为响应对象，并关联查询会员卡的课时数
        return orders.stream().map(order -> {
            OrderResp resp = BeanUtil.copyProperties(order, OrderResp.class);
            resp.setStudentId(order.getStudentId());
            resp.setStudentName(order.getStudentName());

            // 查询会员卡信息获取课时数
            if (order.getCardId() != null) {
                CardDO card = cardMapper.selectById(order.getCardId());
                if (card != null && card.getInitBalance() != null) {
                    resp.setAmount(card.getInitBalance().intValue());
                }
            }

            return resp;
        }).collect(Collectors.toList());
    }
}