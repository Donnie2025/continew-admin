package top.continew.admin.education.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.education.model.query.CardQuery;
import top.continew.admin.education.model.req.CardReq;
import top.continew.admin.education.model.resp.CardDetailResp;
import top.continew.admin.education.model.resp.CardResp;

import java.util.List;

/**
 * 会员卡管理业务接口
 *
 * @author don
 * @since 2025/05/10 00:06
 */
public interface CardService extends BaseService<CardResp, CardDetailResp, CardQuery, CardReq> {
    
    /**
     * 获取所有可用会员卡
     * 
     * @return 状态为1的会员卡列表，按照sort字段升序排列，sort相同时按更新时间倒序排列
     */
    List<CardResp> listActiveCards();
}