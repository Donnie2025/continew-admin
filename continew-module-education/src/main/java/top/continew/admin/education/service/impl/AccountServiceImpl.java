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

        try {
            // 获取当前登录学生ID
            Long studentId = UserContextHolder.getUserId();
            if (studentId == null) {
                log.warn("用户未登录，无法获取账户余额");
                result.put("accountId", null);
                result.put("balance", 0);
                return result;
            }

            log.info("查询学生账户余额: studentId={}", studentId);

            // 查询该学生的有效账户（状态为1且余额>0）
            // 优先返回余额最多的账户
            LambdaQueryWrapper<AccountDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AccountDO::getStudentId, studentId)
                .eq(AccountDO::getStatus, 1) // 状态为有效
                .gt(AccountDO::getBalance, BigDecimal.ZERO) // 余额大于0
                .orderByDesc(AccountDO::getBalance) // 按余额降序排列
                .last("LIMIT 1"); // 只取第一个（余额最多的）

            AccountDO account = accountMapper.selectOne(queryWrapper);

            if (account != null) {
                result.put("accountId", account.getId());
                result.put("balance", account.getBalance().intValue());
                log.info("找到学生可用账户: studentId={}, accountId={}, balance={}", studentId, account.getId(), account
                    .getBalance());
            } else {
                // 如果没有余额>0的账户，查找所有有效账户（包括余额为0的）
                LambdaQueryWrapper<AccountDO> allAccountQuery = new LambdaQueryWrapper<>();
                allAccountQuery.eq(AccountDO::getStudentId, studentId)
                    .eq(AccountDO::getStatus, 1)
                    .orderByDesc(AccountDO::getCreateTime)
                    .last("LIMIT 1");

                AccountDO emptyAccount = accountMapper.selectOne(allAccountQuery);

                if (emptyAccount != null) {
                    result.put("accountId", emptyAccount.getId());
                    result.put("balance", emptyAccount.getBalance() != null ? emptyAccount.getBalance().intValue() : 0);
                    log.info("学生有账户但余额为0: studentId={}, accountId={}", studentId, emptyAccount.getId());
                } else {
                    result.put("accountId", null);
                    result.put("balance", 0);
                    log.warn("学生没有可用的课时账户: studentId={}", studentId);
                }
            }

            return result;

        } catch (Exception e) {
            log.error("获取学生账户余额失败: error={}", e.getMessage(), e);
            result.put("accountId", null);
            result.put("balance", 0);
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

        return accounts.stream().map(account -> {
            AccountResp resp = new AccountResp();
            BeanUtil.copyProperties(account, resp);
            resp.setAccountTypeName(getAccountTypeName(account.getAccountType()));
            return resp;
        }).collect(Collectors.toList());
    }

    private String getAccountTypeName(String accountType) {
        for (AccountTypeEnum type : AccountTypeEnum.values()) {
            if (type.getCode().equals(accountType)) {
                return type.getDesc();
            }
        }
        return accountType;
    }
}
