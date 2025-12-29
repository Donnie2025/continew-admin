package top.continew.admin.education.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.MaterialMapper;
import top.continew.admin.education.model.entity.MaterialDO;
import top.continew.admin.education.model.query.MaterialQuery;
import top.continew.admin.education.model.req.MaterialReq;
import top.continew.admin.education.model.resp.MaterialDetailResp;
import top.continew.admin.education.model.resp.MaterialResp;
import top.continew.admin.education.service.MaterialService;

/**
 * 教材业务实现
 *
 * @author don
 * @since 2025/12/29 21:22
 */
@Service
@RequiredArgsConstructor
public class MaterialServiceImpl extends BaseServiceImpl<MaterialMapper, MaterialDO, MaterialResp, MaterialDetailResp, MaterialQuery, MaterialReq> implements MaterialService {}