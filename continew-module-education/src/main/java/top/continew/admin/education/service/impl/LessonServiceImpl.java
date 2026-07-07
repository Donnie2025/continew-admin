/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.continew.admin.education.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.starter.core.exception.BusinessException;
import top.continew.admin.education.enums.RecordStatusEnum;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import top.continew.admin.education.client.ClassinClient;
import top.continew.admin.education.constant.ClassinConstants;
import top.continew.admin.education.mapper.CourseMapper;
import top.continew.admin.education.mapper.LessonMapper;
import top.continew.admin.education.mapper.MaterialMapper;
import top.continew.admin.education.mapper.TeacherMapper;
import top.continew.admin.education.model.entity.ClassinUserDO;
import top.continew.admin.education.model.entity.CourseDO;
import top.continew.admin.education.model.entity.LessonDO;
import top.continew.admin.education.model.entity.MaterialDO;
import top.continew.admin.education.model.entity.TeacherDO;
import top.continew.admin.education.model.query.LessonQuery;
import top.continew.admin.education.model.req.LessonReq;
import top.continew.admin.education.model.req.BatchLessonReq;
import top.continew.admin.education.model.req.classin.ClassinCreateClassReq;
import top.continew.admin.education.model.req.classin.ClassinCreateUnitReq;
import top.continew.admin.education.model.req.classin.ClassinUpdateClassReq;
import top.continew.admin.education.model.resp.LessonDetailResp;
import top.continew.admin.education.model.resp.LessonResp;
import top.continew.admin.education.model.resp.classin.ClassinCreateClassResp;
import top.continew.admin.education.model.resp.classin.ClassinCreateUnitResp;
import top.continew.admin.education.model.resp.classin.ClassinUpdateClassResp;
import top.continew.admin.education.service.ClassinUserService;
import top.continew.admin.education.service.LessonService;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.extension.crud.service.BaseServiceImpl;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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
    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;
    private final MaterialMapper materialMapper;
    private final top.continew.admin.education.helper.ClassinHelper classinHelper;

    /**
     * 重写创建方法，增加对接ClassIn创建教室功能
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(LessonReq req) {
        log.info("开始创建课堂，请求参数：{}", req);

        // 1. 获取课程信息以获取机构ID
        CourseDO course = courseMapper.selectById(req.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在，课程ID：" + req.getCourseId());
        }
        Long institutionId = course.getInstitutionId();
        if (institutionId == null) {
            throw new BusinessException("课程未关联机构，课程ID：" + req.getCourseId());
        }

        // 2. 获取教师信息
        TeacherDO teacher = teacherMapper.selectById(req.getTeacherId());
        if (teacher == null) {
            throw new BusinessException("教师不存在，教师ID：" + req.getTeacherId());
        }

        // 3. 获取或自动创建教师的ClassIn用户信息
        ClassinUserDO classinUser = classinUserService.getByMemberIdAndUserTypeAndInstitution(req
            .getTeacherId(), ClassinConstants.USER_TYPE_TEACHER, institutionId);

        // 如果教师在该机构下没有ClassIn账号，自动创建
        if (classinUser == null || classinUser.getClassinUid() == null) {
            log.info("教师[{}]在机构[{}]下没有ClassIn账号，开始自动创建", teacher.getName(), institutionId);
            classinUser = classinHelper.registerTeacherIfAbsent(req.getTeacherId(), teacher, institutionId);

            // 如果自动创建失败，抛出异常
            if (classinUser == null || classinUser.getClassinUid() == null) {
                throw new BusinessException("教师[" + teacher.getName() + "]的ClassIn账号自动创建失败，请检查教师的手机号或邮箱是否填写");
            }
            log.info("教师[{}]在机构[{}]下ClassIn账号自动创建成功，ClassIn UID: {}", teacher.getName(), institutionId, classinUser
                .getClassinUid());
        }

        Long teacherClassinUid = Long.parseLong(classinUser.getClassinUid());

        // 4. 先创建ClassIn单元（如果不存在）
        Long unitId = createClassinUnit(req);

        // 5. 调用ClassIn API创建课堂活动
        ClassinCreateClassResp classResp = createClassinClass(req, unitId, teacherClassinUid);

        // 6. 保存课堂信息到数据库
        LessonDO entity = BeanUtil.copyProperties(req, LessonDO.class);

        // duration 已经通过 BeanUtil.copyProperties 从 req 复制过来了

        // 设置教师信息
        entity.setTeacherId(req.getTeacherId());
        entity.setTeacherUid(teacherClassinUid);
        entity.setTeacherName(teacher.getName());

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

        // 设置默认状态（启用）
        entity.setStatus(RecordStatusEnum.ENABLED.getValue());

        // 保存到数据库
        super.save(entity);
        log.info("课堂创建成功，ID：{}", entity.getId());

        return entity.getId();
    }

    /**
     * 重写更新方法，增加对接ClassIn更新课堂活动功能
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(LessonReq req, Long id) {
        log.info("开始更新课堂，ID：{}，请求参数：{}", id, req);

        // 1. 查询原课堂信息
        LessonDO oldLesson = baseMapper.selectById(id);
        if (oldLesson == null) {
            throw new BusinessException("课堂不存在，ID：" + id);
        }

        // 2. 获取课程信息以获取机构ID
        CourseDO course = courseMapper.selectById(req.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在，课程ID：" + req.getCourseId());
        }
        Long institutionId = course.getInstitutionId();
        if (institutionId == null) {
            throw new BusinessException("课程未关联机构，课程ID：" + req.getCourseId());
        }

        // 3. 获取教师信息
        TeacherDO teacher = teacherMapper.selectById(req.getTeacherId());
        if (teacher == null) {
            throw new BusinessException("教师不存在，教师ID：" + req.getTeacherId());
        }

        // 4. 获取或自动创建教师的ClassIn用户信息
        ClassinUserDO classinUser = classinUserService.getByMemberIdAndUserTypeAndInstitution(req
            .getTeacherId(), ClassinConstants.USER_TYPE_TEACHER, institutionId);

        // 如果教师在该机构下没有ClassIn账号，自动创建
        if (classinUser == null || classinUser.getClassinUid() == null) {
            log.info("教师[{}]在机构[{}]下没有ClassIn账号，开始自动创建", teacher.getName(), institutionId);
            classinUser = classinHelper.registerTeacherIfAbsent(req.getTeacherId(), teacher, institutionId);

            // 如果自动创建失败，抛出异常
            if (classinUser == null || classinUser.getClassinUid() == null) {
                throw new BusinessException("教师[" + teacher.getName() + "]的ClassIn账号自动创建失败，请检查教师的手机号或邮箱是否填写");
            }
            log.info("教师[{}]在机构[{}]下ClassIn账号自动创建成功，ClassIn UID: {}", teacher.getName(), institutionId, classinUser
                .getClassinUid());
        }

        Long teacherClassinUid = Long.parseLong(classinUser.getClassinUid());

        // 5. 同步更新到ClassIn（如果课堂已关联ClassIn活动）
        if (oldLesson.getActivityUid() != null && oldLesson.getCourseUid() != null) {
            try {
                ClassinUpdateClassResp updateResp = updateClassinClass(req, oldLesson, teacherClassinUid);
                log.info("ClassIn课堂活动更新成功，课堂ID：{}，活动ID：{}", id, oldLesson.getActivityUid());
            } catch (Exception e) {
                log.error("更新ClassIn课堂活动失败，课堂ID：{}，活动ID：{}，错误信息：{}", id, oldLesson.getActivityUid(), e.getMessage(), e);
                // 不抛出异常，允许本地更新继续进行，但记录错误日志
            }
        } else {
            log.info("课堂未关联ClassIn活动，跳过ClassIn同步，课堂ID：{}", id);
        }

        // 6. 更新本地数据库记录
        LessonDO entity = BeanUtil.copyProperties(req, LessonDO.class);
        entity.setId(id);

        // 设置教师信息
        entity.setTeacherId(req.getTeacherId());
        entity.setTeacherUid(teacherClassinUid);
        entity.setTeacherName(teacher.getName());

        // 保留原有的ClassIn活动信息（不覆盖）
        entity.setActivityUid(oldLesson.getActivityUid());
        entity.setClassUid(oldLesson.getClassUid());
        entity.setUnitUid(oldLesson.getUnitUid());
        entity.setLiveUrl(oldLesson.getLiveUrl());
        entity.setRtmpUrl(oldLesson.getRtmpUrl());
        entity.setHlsUrl(oldLesson.getHlsUrl());
        entity.setFlvUrl(oldLesson.getFlvUrl());

        // 更新数据库记录
        baseMapper.updateById(entity);
        log.info("课堂更新成功，ID：{}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        // 软删除：将状态设置为禁用而不是物理删除
        log.info("开始软删除课堂，IDs：{}", ids);

        // 查询课堂信息
        List<LessonDO> lessonList = super.listByIds(ids);

        for (LessonDO lesson : lessonList) {
            // 如果有ClassIn活动ID，则调用ClassIn API删除活动
            if (lesson.getActivityUid() != null && lesson.getCourseUid() != null && lesson.getCourseId() != null) {
                try {
                    // 获取课程信息以获取机构ID
                    CourseDO course = courseMapper.selectById(lesson.getCourseId());
                    if (course == null || course.getInstitutionId() == null) {
                        log.warn("课程不存在或未关联机构，跳过删除ClassIn活动，课程ID：{}", lesson.getCourseId());
                        continue;
                    }

                    // 调用ClassIn API删除活动
                    classinClient.deleteActivity(lesson.getCourseUid(), lesson.getActivityUid(), course
                        .getInstitutionId());
                    log.info("ClassIn活动删除成功，课堂ID：{}，活动ID：{}", lesson.getId(), lesson.getActivityUid());
                } catch (Exception e) {
                    // 删除失败不影响后续操作，只记录日志
                    log.error("删除ClassIn活动失败，课堂ID：{}，活动ID：{}，错误信息：{}", lesson.getId(), lesson.getActivityUid(), e
                        .getMessage());
                }
            }

            // 软删除：更新状态为禁用
            lesson.setStatus(RecordStatusEnum.DISABLED.getValue());
            baseMapper.updateById(lesson);
        }

        log.info("课堂软删除完成，IDs：{}", ids);
    }

    /**
     * 创建ClassIn单元
     */
    private Long createClassinUnit(LessonReq req) {
        ClassinCreateUnitReq unitReq = ClassinCreateUnitReq.builder()
            .courseId(req.getCourseUid())
            .name("无单元主题")
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
    private ClassinCreateClassResp createClassinClass(LessonReq req, Long unitId, Long teacherClassinUid) {
        // 转换时间格式（LocalDateTime -> Unix timestamp in seconds）
        // 使用东八区时区（Asia/Shanghai），因为前端传入的是本地时间
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        long startTimeSeconds = req.getStartTime().atZone(zoneId).toEpochSecond();
        // 根据 startTime 和 duration 计算 endTime
        long endTimeSeconds = req.getStartTime().plusMinutes(req.getDuration()).atZone(zoneId).toEpochSecond();

        // 解析云盘文件夹ID：从 req.materialId → edu_material.pid → edu_material.cloud_id
        String cloudFolderId = null;
        if (req.getMaterialId() != null) {
            MaterialDO material = materialMapper.selectById(req.getMaterialId());
            if (material != null && material.getPid() != null && material.getPid() != 0) {
                MaterialDO parentMaterial = materialMapper.selectById(material.getPid());
                if (parentMaterial != null && cn.hutool.core.util.StrUtil.isNotBlank(parentMaterial.getCloudId())) {
                    cloudFolderId = parentMaterial.getCloudId();
                    log.info("创建课堂关联教材[{}]的父级云盘文件夹ID：{}", req.getMaterialId(), cloudFolderId);
                }
            }
        }

        ClassinCreateClassReq classReq = ClassinCreateClassReq.builder()
            .courseId(req.getCourseUid())
            .unitId(unitId)
            .name(req.getName())
            .teacherUid(teacherClassinUid)  // 使用从ClassinUser获取的ClassIn用户ID
            .startTime(startTimeSeconds)
            .endTime(endTimeSeconds)
            .recordType(ClassinConstants.RECORD_TYPE_CLASSROOM) // 录制教室
            .recordState(req.getRecordState() != null ? req.getRecordState() : ClassinConstants.RECORD_STATE_DISABLED)
            .liveState(req.getLiveState() != null ? req.getLiveState() : ClassinConstants.LIVE_STATE_DISABLED)
            .openState(req.getOpenState() != null ? req.getOpenState() : ClassinConstants.OPEN_STATE_PRIVATE)
            .cameraHide(ClassinConstants.CAMERA_SHOW) // 显示坐席区
            .seatNum(req.getSeatNum() != null ? req.getSeatNum() : 0) // 设置上台人数，默认不限制（ClassinClient会自动+1包含老师）
            .cloudFolderId(cloudFolderId) // 云盘文件夹ID（教材父节点的cloud_id）
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

    /**
     * 更新ClassIn课堂活动
     */
    private ClassinUpdateClassResp updateClassinClass(LessonReq req, LessonDO oldLesson, Long teacherClassinUid) {
        // 转换时间格式（LocalDateTime -> Unix timestamp in seconds）
        // 使用东八区时区（Asia/Shanghai），因为前端传入的是本地时间
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        long startTimeSeconds = req.getStartTime().atZone(zoneId).toEpochSecond();
        // 根据 startTime 和 duration 计算 endTime
        long endTimeSeconds = req.getStartTime().plusMinutes(req.getDuration()).atZone(zoneId).toEpochSecond();

        // 记录调试信息
        log.info("准备更新ClassIn课堂活动 - courseId: {}, activityId: {}, name: {}, teacherUid: {}, startTime: {}, endTime: {}", oldLesson
            .getCourseUid(), oldLesson.getActivityUid(), req
                .getName(), teacherClassinUid, startTimeSeconds, endTimeSeconds);

        // 解析云盘文件夹ID：从 edu_lesson.material_id → edu_material.pid → edu_material.cloud_id
        String cloudFolderId = null;
        if (oldLesson.getMaterialId() != null) {
            MaterialDO material = materialMapper.selectById(oldLesson.getMaterialId());
            if (material != null && material.getPid() != null && material.getPid() != 0) {
                MaterialDO parentMaterial = materialMapper.selectById(material.getPid());
                if (parentMaterial != null && cn.hutool.core.util.StrUtil.isNotBlank(parentMaterial.getCloudId())) {
                    cloudFolderId = parentMaterial.getCloudId();
                    log.info("课堂[{}]关联教材[{}]的父级云盘文件夹ID：{}", oldLesson.getId(), oldLesson
                        .getMaterialId(), cloudFolderId);
                }
            }
        }

        ClassinUpdateClassReq updateReq = ClassinUpdateClassReq.builder()
            .courseId(oldLesson.getCourseUid())           // 课程ID（必填）
            .activityId(oldLesson.getActivityUid())       // 活动ID（必填）
            .name(req.getName())                          // 课堂名称
            .teacherUid(teacherClassinUid)                // 教师UID
            .startTime(startTimeSeconds)                  // 开始时间
            .endTime(endTimeSeconds)                      // 结束时间
            .seatNum(req.getSeatNum())                    // 座位数（教学形式）
            .recordState(req.getRecordState())            // 录制状态
            .liveState(oldLesson.getLiveState())                // 直播状态
            .openState(oldLesson.getOpenState())                // 开放状态
            .recordType(ClassinConstants.RECORD_TYPE_CLASSROOM) // 录制类型：云端录制
            .cloudFolderId(cloudFolderId)                 // 云盘文件夹ID（教材父节点的cloud_id）
            .build();

        try {
            ClassinUpdateClassResp updateResp = classinClient.updateClass(updateReq);
            log.info("ClassIn课堂活动更新成功，活动ID：{}，课堂名称：{}", updateResp.getActivityId(), updateResp.getName());
            return updateResp;
        } catch (Exception e) {
            log.error("更新ClassIn课堂活动失败", e);
            throw new BusinessException("更新ClassIn课堂活动失败：" + e.getMessage());
        }
    }

    @Override
    public List<LessonResp> listByCourseId(Long courseId) {
        // 查询指定班级的所有课节（过滤掉已删除的课节）
        LambdaQueryWrapper<LessonDO> wrapper = Wrappers.lambdaQuery(LessonDO.class)
            .eq(LessonDO::getCourseId, courseId)
            .ne(LessonDO::getStatus, RecordStatusEnum.DISABLED.getValue()) // 过滤掉已删除的课节
            .orderByDesc(LessonDO::getStartTime); // 按开始时间倒序排列（最新的在前）
        List<LessonDO> list = baseMapper.selectList(wrapper);

        if (list.isEmpty()) {
            return new ArrayList<>();
        }

        // 转换为响应对象
        List<LessonResp> respList = new ArrayList<>();
        for (LessonDO lesson : list) {
            LessonResp resp = BeanUtil.copyProperties(lesson, LessonResp.class);
            respList.add(resp);
        }
        return respList;
    }

    /**
     * 重写查询构造器，添加通用的状态过滤（过滤掉已删除的课节）
     */
    @Override
    protected QueryWrapper<LessonDO> buildQueryWrapper(LessonQuery query) {
        // 先保存courseStatus值，然后清空避免被父类处理
        String courseStatus = query.getCourseStatus();
        query.setCourseStatus(null);

        QueryWrapper<LessonDO> queryWrapper = super.buildQueryWrapper(query);

        // 恢复courseStatus值
        query.setCourseStatus(courseStatus);

        // 添加通用的状态过滤条件：排除已删除的课节
        queryWrapper.ne("status", RecordStatusEnum.DISABLED.getValue());

        // 根据课程状态过滤
        if (courseStatus != null && !courseStatus.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            if ("started".equals(courseStatus)) {
                // 已开课：开始时间 <= 当前时间，按开课时间倒序（最近的在前）
                queryWrapper.le("start_time", now);
                queryWrapper.orderByDesc("start_time");
            } else if ("not_started".equals(courseStatus)) {
                // 未开课：开始时间 > 当前时间，按开课时间正序（最近的在前）
                queryWrapper.gt("start_time", now);
                queryWrapper.orderByAsc("start_time");
            }
        } else {
            // 如果没有指定状态，保持默认排序
            queryWrapper.orderByDesc("id");
        }

        return queryWrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createBatch(BatchLessonReq req) {
        log.info("开始批量创建课节，请求参数：{}", req);

        // 验证课节时间列表不为空
        if (req.getLessonTimes() == null || req.getLessonTimes().isEmpty()) {
            throw new BusinessException("课节时间列表不能为空");
        }

        // 获取课程信息以设置courseUid（移到循环外，只查询一次）
        CourseDO course = courseMapper.selectById(req.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在，课程ID：" + req.getCourseId());
        }

        // 根据课程的 agent_code 设置默认值
        if ("lizhe".equals(course.getAgentCode())) {
            // 如果是 lizhe 代理，设置默认值
            if (req.getSeatNum() == null || req.getSeatNum() == 1) {
                req.setSeatNum(12); // 教学形式：1对12
            }
            if (req.getDuration() == null) {
                req.setDuration(180L); // 课堂时长：3小时 = 180分钟
            }
            if (req.getRecordState() == null) {
                req.setRecordState(1); // 是否录制：是
            }
            log.info("检测到课程属于 lizhe 代理，已应用默认配置：seatNum={}, duration={}分钟, recordState={}", req.getSeatNum(), req
                .getDuration(), req.getRecordState());
        }

        // 批量创建课节
        for (BatchLessonReq.LessonTimeReq lessonTime : req.getLessonTimes()) {
            // 构建单个课节请求参数
            LessonReq lessonReq = new LessonReq();
            lessonReq.setCourseId(req.getCourseId());
            lessonReq.setName(req.getNamePrefix() + lessonTime.getNameSuffix());
            lessonReq.setTeacherId(req.getTeacherId());
            lessonReq.setStartTime(lessonTime.getStartTime());
            lessonReq.setDuration(req.getDuration());
            lessonReq.setSeatNum(req.getSeatNum());
            lessonReq.setRecordState(req.getRecordState());
            lessonReq.setLiveState(req.getLiveState());
            lessonReq.setOpenState(req.getOpenState());
            lessonReq.setCourseUid(course.getCourseUid());

            try {
                // 调用单个创建方法
                Long lessonId = this.create(lessonReq);
                log.info("课节创建成功，课节ID：{}，课节名称：{}", lessonId, lessonReq.getName());
            } catch (Exception e) {
                log.error("课节创建失败，课节名称：{}，错误信息：{}", lessonReq.getName(), e.getMessage(), e);
                throw new BusinessException("课节创建失败：" + lessonReq.getName() + "，错误信息：" + e.getMessage());
            }
        }

        log.info("批量创建课节完成，共创建 {} 个课节", req.getLessonTimes().size());
    }

}