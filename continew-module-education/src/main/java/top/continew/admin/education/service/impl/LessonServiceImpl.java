package top.continew.admin.education.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.LessonMapper;
import top.continew.admin.education.model.entity.LessonDO;
import top.continew.admin.education.model.query.LessonQuery;
import top.continew.admin.education.model.req.LessonReq;
import top.continew.admin.education.model.resp.LessonDetailResp;
import top.continew.admin.education.model.resp.LessonResp;
import top.continew.admin.education.service.LessonService;

/**
 * 课堂业务实现
 *
 * @author don
 * @since 2025/06/24 23:39
 */
@Service
@RequiredArgsConstructor
public class LessonServiceImpl extends BaseServiceImpl<LessonMapper, LessonDO, LessonResp, LessonDetailResp, LessonQuery, LessonReq> implements LessonService {}