package top.continew.admin.education.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.SlotMapper;
import top.continew.admin.education.model.entity.SlotDO;
import top.continew.admin.education.model.query.SlotQuery;
import top.continew.admin.education.model.req.SlotReq;
import top.continew.admin.education.model.resp.SlotDetailResp;
import top.continew.admin.education.model.resp.SlotResp;
import top.continew.admin.education.service.SlotService;

/**
 * 课程管理业务实现
 *
 * @author don
 * @since 2025/04/25 23:24
 */
@Service
@RequiredArgsConstructor
public class SlotServiceImpl extends BaseServiceImpl<SlotMapper, SlotDO, SlotResp, SlotDetailResp, SlotQuery, SlotReq> implements SlotService {}