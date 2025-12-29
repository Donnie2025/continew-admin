package top.continew.admin.education.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.education.model.query.MaterialQuery;
import top.continew.admin.education.model.req.MaterialReq;
import top.continew.admin.education.model.resp.MaterialDetailResp;
import top.continew.admin.education.model.resp.MaterialResp;

/**
 * 教材业务接口
 *
 * @author don
 * @since 2025/12/29 21:22
 */
public interface MaterialService extends BaseService<MaterialResp, MaterialDetailResp, MaterialQuery, MaterialReq> {}