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
import top.continew.admin.education.mapper.CardMapper;
import top.continew.admin.education.mapper.OrderMapper;
import top.continew.admin.education.mapper.StuCardMapper;
import top.continew.admin.education.mapper.TransactionMapper;
import top.continew.admin.education.model.entity.CardDO;
import top.continew.admin.education.model.entity.OrderDO;
import top.continew.admin.education.model.entity.StuCardDO;
import top.continew.admin.education.model.entity.TransactionDO;
import top.continew.admin.education.model.query.OrderQuery;
import top.continew.admin.education.model.req.OrderReq;
import top.continew.admin.education.model.resp.OrderDetailResp;
import top.continew.admin.education.model.resp.OrderResp;
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
    private final StuCardMapper stuCardMapper;
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

        // 3. 创建空白学生会员卡
        StuCardDO stuCard = new StuCardDO();
        stuCard.setStuId(stuId);
        stuCard.setStuName(stuName);
        stuCard.setCardId(card.getId());
        stuCard.setCardName(card.getTitle());
        stuCard.setCardType(card.getType()); // String类型
        // 空白卡，余额为0（统一使用balance）
        stuCard.setBalance(BigDecimal.ZERO); // 空白卡，余额为0
        stuCard.setActivateDate(null); // 未激活
        stuCard.setExpireDate(null); // 未设置过期时间
        stuCard.setPurchasePrice(card.getPrice());
        stuCard.setStatus(1); // 启用
        stuCard.setCardStatus(0); // 学生端不可见（待确认后可见）
        stuCard.setCreateUser(stuId);
        stuCardMapper.insert(stuCard);

        // 4. 生成订单编号
        String orderNo = "ORD" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + IdUtil.randomUUID()
            .substring(0, 6);

        // 5. 创建订单
        OrderDO order = new OrderDO();
        order.setOrderNo(orderNo);
        order.setStuId(stuId);
        order.setStuName(stuName);
        order.setCardId(card.getId());
        order.setCardTitle(card.getTitle());
        order.setCardType(card.getType()); // 直接使用String类型
        order.setOrderPrice(card.getPrice());
        order.setPaymentType(req.getPaymentType());
        order.setOrderStatus("PENDING"); // 待确认
        order.setStuCardId(stuCard.getId()); // 关联空白卡
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

        // 2. 查询空白学生会员卡
        StuCardDO stuCard = stuCardMapper.selectById(order.getStuCardId());
        if (stuCard == null) {
            throw new BusinessException("学生会员卡不存在");
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

        // 统一使用 initBalance 管理所有卡类型的余额
        if (card.getInitBalance() != null) {
            // 统一使用初始余额
            stuCard.setBalance(card.getInitBalance());
        } else {
            // 默认余额为0
            stuCard.setBalance(BigDecimal.ZERO);
        }

        stuCard.setActivateDate(activateDate);
        stuCard.setExpireDate(expireDate);
        stuCard.setCardStatus(1); // 学生端可见
        stuCard.setUpdateUser(UserContextHolder.getUserId());
        stuCardMapper.updateById(stuCard);

        // 5. 生成交易流水
        TransactionDO transaction = new TransactionDO();
        transaction.setStuCardId(stuCard.getId());
        transaction.setStuId(order.getStuId());
        transaction.setStuName(order.getStuName());
        transaction.setCardTitle(order.getCardTitle());
        transaction.setTransType("activate"); // 激活

        // 统一使用 initBalance 记录交易金额
        BigDecimal amount = card.getInitBalance() != null ? card.getInitBalance() : BigDecimal.ZERO;

        transaction.setBeforeAmt(BigDecimal.ZERO);
        transaction.setAfterAmt(amount);
        transaction.setAmount(order.getOrderPrice());
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