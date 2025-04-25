package top.continew.admin.education.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.education.model.query.SlotQuery;
import top.continew.admin.education.model.req.SlotReq;
import top.continew.admin.education.model.resp.SlotDetailResp;
import top.continew.admin.education.model.resp.SlotResp;

/**
 * 课程管理业务接口
 *
 * @author don
 * @since 2025/04/25 23:24
 */
public interface SlotService extends BaseService<SlotResp, SlotDetailResp, SlotQuery, SlotReq> {}