package top.continew.admin.education.service;

import top.continew.starter.extension.crud.service.BaseService;
import top.continew.admin.education.model.query.CourseQuery;
import top.continew.admin.education.model.req.CourseReq;
import top.continew.admin.education.model.resp.CourseDetailResp;
import top.continew.admin.education.model.resp.CourseResp;

/**
 * 班级业务接口
 *
 * @author don
 * @since 2025/06/21 23:25
 */
public interface CourseService extends BaseService<CourseResp, CourseDetailResp, CourseQuery, CourseReq> {}