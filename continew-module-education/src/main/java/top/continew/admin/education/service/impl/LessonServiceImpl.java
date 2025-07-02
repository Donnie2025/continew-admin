package top.continew.admin.education.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.continew.starter.extension.crud.service.BaseServiceImpl;
import top.continew.admin.education.client.ClassinClient;
import top.continew.admin.education.mapper.LessonMapper;
import top.continew.admin.education.model.entity.ClassinUserDO;
import top.continew.admin.education.model.entity.LessonDO;
import top.continew.admin.education.model.query.LessonQuery;
import top.continew.admin.education.model.req.LessonReq;
import top.continew.admin.education.model.req.classin.ClassinCreateClassReq;
import top.continew.admin.education.model.req.classin.ClassinCreateUnitReq;
import top.continew.admin.education.model.req.classin.ClassinDeleteActivityReq;
import top.continew.admin.education.model.resp.LessonDetailResp;
import top.continew.admin.education.model.resp.LessonResp;
import top.continew.admin.education.model.resp.classin.ClassinCreateClassResp;
import top.continew.admin.education.model.resp.classin.ClassinCreateUnitResp;
import top.continew.admin.education.model.resp.classin.ClassinDeleteActivityResp;
import top.continew.admin.education.service.ClassinUserService;
import top.continew.admin.education.service.LessonService;
import top.continew.admin.education.constant.ClassinConstants;
import top.continew.starter.core.exception.BusinessException;

import java.time.ZoneOffset;
import java.util.List;

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
    private final ClassinUserService classinUserService;
    
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

    @Override
    public void beforeDelete(List<Long> ids) {
        // 删除ClassIn课堂活动
        log.info("开始删除课堂，IDs：{}", ids);
        
        // 查询课堂信息
        List<LessonDO> lessonList = super.listByIds(ids);
        
        for (LessonDO lesson : lessonList) {
            // 如果有ClassIn活动ID，则调用ClassIn API删除活动
            if (lesson.getActivityUid() != null && lesson.getCourseUid() != null) {
                try {
                    // 构建删除请求
                    ClassinDeleteActivityReq deleteReq = ClassinDeleteActivityReq.builder()
                        .courseId(lesson.getCourseUid())
                        .activityId(lesson.getActivityUid())
                        .build();
                    
                    // 调用ClassIn API删除活动
                    ClassinDeleteActivityResp deleteResp = classinClient.deleteActivity(deleteReq);
                    log.info("ClassIn活动删除成功，活动ID：{}，名称：{}", deleteResp.getActivityId(), deleteResp.getName());
                } catch (Exception e) {
                    // 删除失败不影响后续操作，只记录日志
                    log.error("删除ClassIn活动失败，课堂ID：{}，活动ID：{}，错误信息：{}", 
                        lesson.getId(), lesson.getActivityUid(), e.getMessage());
                }
            }
        }

        super.beforeDelete(ids);
    }
    
    /**
     * 创建ClassIn单元
     */
    private Long createClassinUnit(LessonReq req) {
        ClassinCreateUnitReq unitReq = ClassinCreateUnitReq.builder()
            .courseId(req.getCourseUid())
            .name("默认单元")
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
        
        // 获取教师的ClassIn用户ID
        Long teacherClassinUid = null;
        if (req.getTeacherId() != null) {
            ClassinUserDO classinUser = classinUserService.getByMemberIdAndUserType(req.getTeacherId(), ClassinConstants.USER_TYPE_TEACHER);
            if (classinUser != null && classinUser.getClassinUid() != null) {
                try {
                    teacherClassinUid = Long.parseLong(classinUser.getClassinUid());
                    log.info("获取到教师的ClassIn用户ID：{}", teacherClassinUid);
                } catch (NumberFormatException e) {
                    log.warn("教师的ClassIn用户ID格式不正确：{}", classinUser.getClassinUid());
                }
            } else {
                log.warn("未找到教师ID为{}的ClassIn用户信息", req.getTeacherId());
            }
        }
        
        // 如果未获取到教师的ClassIn用户ID，则使用默认值或抛出异常
        if (teacherClassinUid == null) {
            throw new BusinessException("未找到教师的ClassIn用户信息，请确保教师已关联ClassIn账号");
        }
        
        ClassinCreateClassReq classReq = ClassinCreateClassReq.builder()
            .courseId(req.getCourseUid())
            .unitId(unitId)
            .name(req.getName())
            .teacherUid(teacherClassinUid)  // 使用从ClassinUser获取的ClassIn用户ID
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