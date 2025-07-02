package top.continew.admin.education.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.education.model.query.LessonQuery;
import top.continew.admin.education.model.req.LessonReq;
import top.continew.admin.education.model.resp.LessonDetailResp;
import top.continew.admin.education.model.resp.LessonResp;

/**
 * 课堂业务接口
 *
 * @author don
 * @since 2025/06/24 23:39
 */
public interface LessonService extends BaseService<LessonResp, LessonDetailResp, LessonQuery, LessonReq> {

}