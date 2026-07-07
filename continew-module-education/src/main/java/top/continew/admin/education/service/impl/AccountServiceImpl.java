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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.continew.admin.common.context.UserContextHolder;
import top.continew.admin.education.enums.AccountTypeEnum;
import top.continew.admin.education.mapper.AccountMapper;
import top.continew.admin.education.model.entity.AccountDO;
import top.continew.admin.education.model.resp.AccountResp;
import top.continew.admin.education.service.AccountService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 账户业务实现
 *
 * @author continew-org
 * @since 2026-06-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountMapper accountMapper;

    @Override
    public Map<String, Object> getCurrentStudentAccountBalance() {
        Map<String, Object> result = new HashMap<>();
        result.put("accountId", null);
        result.put("balance", 0);

        try {
            Long studentId = UserContextHolder.getUserId();
            if (studentId == null) {
                log.warn("用户未登录，无法获取账户余额");
                return result;
            }

            log.info("查询学生账户余额: studentId={}", studentId);

            // 优先查找有余额的账户
            AccountDO account = findAccountWithBalance(studentId);
            if (account != null) {
                result.put("accountId", account.getId());
                result.put("balance", account.getBalance().intValue());
                log.info("找到学生可用账户: studentId={}, accountId={}, balance={}",
                    studentId, account.getId(), account.getBalance());
                return result;
            }

            // 没有余额时，查找最近创建的有效账户
            account = findLatestActiveAccount(studentId);
            if (account != null) {
                result.put("accountId", account.getId());
                result.put("balance", account.getBalance() != null ? account.getBalance().intValue() : 0);
                log.info("学生有账户但余额为0: studentId={}, accountId={}", studentId, account.getId());
            } else {
                log.warn("学生没有可用的课时账户，自动创建默认账户: studentId={}", studentId);
                account = createDefaultAccount(studentId);
                result.put("accountId", account.getId());
                result.put("balance", 0);
                log.info("已为学生创建默认课时账户: studentId={}, accountId={}", studentId, account.getId());
            }

            return result;
        } catch (Exception e) {
            log.error("获取学生账户余额失败", e);
            return result;
        }
    }

    @Override
    public List<AccountResp> getStudentAccounts(Long studentId) {
        LambdaQueryWrapper<AccountDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountDO::getStudentId, studentId)
            .eq(AccountDO::getStatus, 1)
            .orderByAsc(AccountDO::getAccountType);

        List<AccountDO> accounts = accountMapper.selectList(wrapper);

        return accounts.stream()
            .map(this::convertToResp)
            .collect(Collectors.toList());
    }

    @Override
    public AccountResp getStudentAccountByType(Long studentId, AccountTypeEnum accountType) {
        LambdaQueryWrapper<AccountDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountDO::getStudentId, studentId)
            .eq(AccountDO::getAccountType, accountType.getCode())
            .eq(AccountDO::getStatus, 1)
            .last("LIMIT 1");

        AccountDO account = accountMapper.selectOne(wrapper);
        return account != null ? convertToResp(account) : null;
    }

    /**
     * 查找有余额的账户（按余额降序）
     */
    private AccountDO findAccountWithBalance(Long studentId) {
        LambdaQueryWrapper<AccountDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountDO::getStudentId, studentId)
            .eq(AccountDO::getStatus, 1)
            .gt(AccountDO::getBalance, BigDecimal.ZERO)
            .orderByDesc(AccountDO::getBalance)
            .last("LIMIT 1");

        return accountMapper.selectOne(wrapper);
    }

    /**
     * 查找最近创建的有效账户
     */
    private AccountDO findLatestActiveAccount(Long studentId) {
        LambdaQueryWrapper<AccountDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountDO::getStudentId, studentId)
            .eq(AccountDO::getStatus, 1)
            .orderByDesc(AccountDO::getCreateTime)
            .last("LIMIT 1");

        return accountMapper.selectOne(wrapper);
    }

    /**
     * 创建默认课时账户
     */
    private AccountDO createDefaultAccount(Long studentId) {
        AccountDO account = new AccountDO();
        account.setStudentId(studentId);
        account.setAccountType("PAID");
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(1);
        account.setRemark("系统自动创建的付费课时账户");
        accountMapper.insert(account);
        return account;
    }

    /**
     * 转换实体为响应对象
     */
    private AccountResp convertToResp(AccountDO account) {
        AccountResp resp = new AccountResp();
        BeanUtil.copyProperties(account, resp);
        resp.setAccountTypeName(AccountTypeEnum.getDescByCode(account.getAccountType()));
        return resp;
    }
}
