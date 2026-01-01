package top.continew.admin.education.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.mapper.MaterialLessonMapper;
import top.continew.admin.education.mapper.MaterialMapper;
import top.continew.admin.education.model.entity.MaterialDO;
import top.continew.admin.education.model.entity.MaterialLessonDO;
import top.continew.admin.education.model.query.MaterialLessonQuery;
import top.continew.admin.education.model.req.MaterialLessonReq;
import top.continew.admin.education.model.resp.MaterialLessonDetailResp;
import top.continew.admin.education.model.resp.MaterialLessonResp;
import top.continew.admin.education.service.MaterialLessonService;

import java.time.LocalDateTime;

/**
 * 课节业务实现
 *
 * @author don
 * @since 2025/12/29 21:22
 */
@Service
@RequiredArgsConstructor
public class MaterialLessonServiceImpl extends BaseServiceImpl<MaterialLessonMapper, MaterialLessonDO, MaterialLessonResp, MaterialLessonDetailResp, MaterialLessonQuery, MaterialLessonReq> implements MaterialLessonService {

    private final MaterialMapper materialMapper;

    @Override
    protected void afterCreate(MaterialLessonReq req, MaterialLessonDO entity) {
        boolean needUpdate = false;
        
        // 自动拼装教材名称
        if (req.getMaterialName() == null && req.getMaterialId() != null) {
            MaterialDO material = materialMapper.selectById(req.getMaterialId());
            if (material != null) {
                String materialName = material.getName() + " (" + material.getLevel() + ")";
                entity.setMaterialName(materialName);
                needUpdate = true;
            }
        }
        
        // 设置默认状态
        if (req.getStatus() == null) {
            entity.setStatus(true); // 默认启用
            needUpdate = true;
        }
        
        // 设置创建人
        if (req.getCreateUser() == null) {
            try {
                entity.setCreateUser(StpUtil.getLoginIdAsLong());
            } catch (Exception e) {
                entity.setCreateUser(1L); // 默认用户ID
            }
            needUpdate = true;
        }
        
        // 设置创建时间
        if (req.getCreateTime() == null) {
            entity.setCreateTime(LocalDateTime.now());
            needUpdate = true;
        }
        
        if (needUpdate) {
            baseMapper.updateById(entity);
        }
        
        super.afterCreate(req, entity);
    }
}