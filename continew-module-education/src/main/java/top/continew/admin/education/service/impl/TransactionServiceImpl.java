package top.continew.admin.education.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.TransactionMapper;
import top.continew.admin.education.model.entity.TransactionDO;
import top.continew.admin.education.model.query.TransactionQuery;
import top.continew.admin.education.model.req.TransactionReq;
import top.continew.admin.education.model.resp.TransactionDetailResp;
import top.continew.admin.education.model.resp.TransactionResp;
import top.continew.admin.education.service.TransactionService;

/**
 * 订单业务实现
 *
 * @author don
 * @since 2025/05/10 22:11
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl extends BaseServiceImpl<TransactionMapper, TransactionDO, TransactionResp, TransactionDetailResp, TransactionQuery, TransactionReq> implements TransactionService {}