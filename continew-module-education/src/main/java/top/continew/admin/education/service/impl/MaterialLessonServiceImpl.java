package top.continew.admin.education.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.MaterialLessonMapper;
import top.continew.admin.education.model.entity.MaterialLessonDO;
import top.continew.admin.education.model.query.MaterialLessonQuery;
import top.continew.admin.education.model.req.MaterialLessonReq;
import top.continew.admin.education.model.resp.MaterialLessonDetailResp;
import top.continew.admin.education.model.resp.MaterialLessonResp;
import top.continew.admin.education.service.MaterialLessonService;

/**
 * 课节业务实现
 *
 * @author don
 * @since 2025/12/29 21:22
 */
@Service
@RequiredArgsConstructor
public class MaterialLessonServiceImpl extends BaseServiceImpl<MaterialLessonMapper, MaterialLessonDO, MaterialLessonResp, MaterialLessonDetailResp, MaterialLessonQuery, MaterialLessonReq> implements MaterialLessonService {}