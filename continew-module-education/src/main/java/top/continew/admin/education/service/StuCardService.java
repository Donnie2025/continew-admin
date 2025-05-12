package top.continew.admin.education.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.education.model.query.StuCardQuery;
import top.continew.admin.education.model.req.StuCardBindReq;
import top.continew.admin.education.model.req.StuCardReq;
import top.continew.admin.education.model.resp.StuCardDetailResp;
import top.continew.admin.education.model.resp.StuCardResp;

/**
 * 会员绑卡业务接口
 *
 * @author don
 * @since 2025/05/10 22:11
 */
public interface StuCardService extends BaseService<StuCardResp, StuCardDetailResp, StuCardQuery, StuCardReq> {
    
    /**
     * 绑定会员卡
     *
     * @param req 绑定会员卡请求参数
     * @return 绑定结果
     */
    StuCardResp bindCard(StuCardBindReq req);
}