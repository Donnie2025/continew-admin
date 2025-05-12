package top.continew.admin.education.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.education.model.query.TransactionQuery;
import top.continew.admin.education.model.req.TransactionReq;
import top.continew.admin.education.model.resp.TransactionDetailResp;
import top.continew.admin.education.model.resp.TransactionResp;

/**
 * 订单业务接口
 *
 * @author don
 * @since 2025/05/10 22:11
 */
public interface TransactionService extends BaseService<TransactionResp, TransactionDetailResp, TransactionQuery, TransactionReq> {}