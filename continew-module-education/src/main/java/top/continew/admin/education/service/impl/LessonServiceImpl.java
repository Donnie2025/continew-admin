package top.continew.admin.education.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.client.ClassinClient;
import top.continew.admin.education.mapper.LessonMapper;
import top.continew.admin.education.model.entity.LessonDO;
import top.continew.admin.education.model.query.LessonQuery;
import top.continew.admin.education.model.req.LessonReq;
import top.continew.admin.education.model.req.classin.ClassinCreateClassReq;
import top.continew.admin.education.model.req.classin.ClassinCreateUnitReq;
import top.continew.admin.education.model.resp.LessonDetailResp;
import top.continew.admin.education.model.resp.LessonResp;
import top.continew.admin.education.model.resp.classin.ClassinCreateClassResp;
import top.continew.admin.education.model.resp.classin.ClassinCreateUnitResp;
import top.continew.admin.education.service.LessonService;
import top.continew.starter.core.exception.BusinessException;

import java.time.ZoneOffset;
import cn.hutool.core.bean.BeanUtil;

/**
 * 课堂业务实现
 *
 * @author don
 * @since 2025/06/24 23:39
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LessonServiceImpl extends BaseServiceImpl<LessonMapper, LessonDO, LessonResp, LessonDetailResp, LessonQuery, LessonReq> implements LessonService {

    private final ClassinClient classinClient;
    
    /**
     * 重写创建方法，增加对接ClassIn创建教室功能
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(LessonReq req) {
        log.info("开始创建课堂，请求参数：{}", req);
        
        // 1. 先创建ClassIn单元（如果不存在）
        Long unitId = createClassinUnit(req);
        
        // 2. 调用ClassIn API创建课堂活动
        ClassinCreateClassResp classResp = createClassinClass(req, unitId);
        
        // 3. 保存课堂信息到数据库
        LessonDO entity = BeanUtil.copyProperties(req, LessonDO.class);
        
        // 设置ClassIn返回的信息
        entity.setActivityUid(classResp.getActivityId());
        entity.setClassUid(classResp.getClassId());
        entity.setUnitUid(unitId);
        entity.setName(classResp.getName());
        entity.setLiveUrl(classResp.getLiveUrl());
        
        // 设置拉流地址
        if (classResp.getLiveInfo() != null) {
            entity.setRtmpUrl(classResp.getLiveInfo().getRTMP());
            entity.setHlsUrl(classResp.getLiveInfo().getHLS());
            entity.setFlvUrl(classResp.getLiveInfo().getFLV());
        }
        
        // 设置默认状态
        entity.setStatus(0); // 0-正常
        
        // 保存到数据库
        super.save(entity);
        log.info("课堂创建成功，ID：{}", entity.getId());
        
        return entity.getId();
    }
    
    /**
     * 创建ClassIn单元
     */
    private Long createClassinUnit(LessonReq req) {
        // 使用课堂名称作为单元名称
        String unitName = req.getName();
        
        ClassinCreateUnitReq unitReq = ClassinCreateUnitReq.builder()
            .courseId(req.getCourseUid())
            .name(unitName)
            .publishFlag(2) // 2-已发布
            .content("系统自动创建的单元")
            .build();
            
        try {
            ClassinCreateUnitResp unitResp = classinClient.createUnit(unitReq);
            log.info("ClassIn单元创建成功，单元ID：{}", unitResp.getUnitId());
            return unitResp.getUnitId();
        } catch (Exception e) {
            log.warn("创建ClassIn单元失败: {}", e.getMessage());
            
            // 如果无法解析单元ID或者是其他错误，则抛出异常
            throw new BusinessException("创建ClassIn单元失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建ClassIn课堂活动
     */
    private ClassinCreateClassResp createClassinClass(LessonReq req, Long unitId) {
        // 转换时间格式（LocalDateTime -> Unix timestamp in seconds）
        long startTimeSeconds = req.getStartTime().toEpochSecond(ZoneOffset.UTC);
        long endTimeSeconds = req.getEndTime().toEpochSecond(ZoneOffset.UTC);
        
        ClassinCreateClassReq classReq = ClassinCreateClassReq.builder()
            .courseId(req.getCourseUid())
            .unitId(unitId)
            .name(req.getName())
            .teacherUid(req.getTeacherUid())
            .startTime(startTimeSeconds)
            .endTime(endTimeSeconds)
            .recordType(0) // 0-云端录制
            .recordState(req.getRecordState() != null ? req.getRecordState() : 0)
            .liveState(req.getLiveState() != null ? req.getLiveState() : 0)
            .openState(req.getOpenState() != null ? req.getOpenState() : 0)
            .cameraHide(0) // 0-显示坐席区
            .seatNum(req.getSeatNum() != null ? req.getSeatNum() : 0) // 设置上台人数，默认不限制
            .build();
            
        try {
            ClassinCreateClassResp classResp = classinClient.createClass(classReq);
            log.info("ClassIn课堂活动创建成功，活动ID：{}，课堂ID：{}", classResp.getActivityId(), classResp.getClassId());
            return classResp;
        } catch (Exception e) {
            log.error("创建ClassIn课堂活动失败", e);
            throw new BusinessException("创建ClassIn课堂活动失败：" + e.getMessage());
        }
    }
}