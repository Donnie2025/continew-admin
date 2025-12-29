package top.continew.admin.education.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.education.model.query.MaterialLessonQuery;
import top.continew.admin.education.model.req.MaterialLessonReq;
import top.continew.admin.education.model.resp.MaterialLessonDetailResp;
import top.continew.admin.education.model.resp.MaterialLessonResp;

/**
 * 课节业务接口
 *
 * @author don
 * @since 2025/12/29 21:22
 */
public interface MaterialLessonService extends BaseService<MaterialLessonResp, MaterialLessonDetailResp, MaterialLessonQuery, MaterialLessonReq> {}