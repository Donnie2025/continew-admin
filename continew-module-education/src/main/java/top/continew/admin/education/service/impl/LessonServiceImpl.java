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
import top.continew.admin.education.client.ClassinClient;
import top.continew.admin.education.constant.ClassinConstants;
import top.continew.admin.education.mapper.CourseMapper;
import top.continew.admin.education.mapper.LessonMapper;
import top.continew.admin.education.mapper.TeacherMapper;
import top.continew.admin.education.model.entity.ClassinUserDO;
import top.continew.admin.education.model.entity.CourseDO;
import top.continew.admin.education.model.entity.LessonDO;
import top.continew.admin.education.model.entity.TeacherDO;
import top.continew.admin.education.model.query.LessonQuery;
import top.continew.admin.education.model.req.LessonReq;
import top.continew.admin.education.model.req.classin.ClassinCreateClassReq;
import top.continew.admin.education.model.req.classin.ClassinCreateUnitReq;
import top.continew.admin.education.model.resp.LessonDetailResp;
import top.continew.admin.education.model.resp.LessonResp;
import top.continew.admin.education.model.resp.classin.ClassinCreateClassResp;
import top.continew.admin.education.model.resp.classin.ClassinCreateUnitResp;
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

        // 3. 获取教师的ClassIn用户信息
        ClassinUserDO classinUser = classinUserService.getByMemberIdAndUserTypeAndInstitution(req
            .getTeacherId(), ClassinConstants.USER_TYPE_TEACHER, institutionId);
        if (classinUser == null || classinUser.getClassinUid() == null) {
            throw new BusinessException("未找到教师的ClassIn用户信息，请确保教师已关联ClassIn账号");
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

        // 设置默认状态（1：启用；2：禁用；3：结课）
        entity.setStatus(1); // 1-启用

        // 保存到数据库
        super.save(entity);
        log.info("课堂创建成功，ID：{}", entity.getId());

        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        // 逻辑删除：将状态设置为2（禁用）而不是物理删除
        log.info("开始逻辑删除课堂，IDs：{}", ids);

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
                    classinClient.deleteActivity(lesson.getCourseUid(), lesson.getActivityUid(), course.getInstitutionId());
                    log.info("ClassIn活动删除成功，课堂ID：{}，活动ID：{}", lesson.getId(), lesson.getActivityUid());
                } catch (Exception e) {
                    // 删除失败不影响后续操作，只记录日志
                    log.error("删除ClassIn活动失败，课堂ID：{}，活动ID：{}，错误信息：{}", lesson.getId(), lesson.getActivityUid(), e
                        .getMessage());
                }
            }
            
            // 逻辑删除：更新状态为2（禁用）
            lesson.setStatus(2);
            baseMapper.updateById(lesson);
        }

        log.info("课堂逻辑删除完成，IDs：{}", ids);
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
    private ClassinCreateClassResp createClassinClass(LessonReq req, Long unitId, Long teacherClassinUid) {
        // 转换时间格式（LocalDateTime -> Unix timestamp in seconds）
        // 使用东八区时区（Asia/Shanghai），因为前端传入的是本地时间
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        long startTimeSeconds = req.getStartTime().atZone(zoneId).toEpochSecond();
        // 根据 startTime 和 duration 计算 endTime
        long endTimeSeconds = req.getStartTime().plusMinutes(req.getDuration()).atZone(zoneId).toEpochSecond();

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

    @Override
    public List<LessonResp> listByCourseId(Long courseId) {
        // 查询指定班级的所有课节（过滤掉已删除的课节：status!=2）
        LambdaQueryWrapper<LessonDO> wrapper = Wrappers.lambdaQuery(LessonDO.class)
            .eq(LessonDO::getCourseId, courseId)
            .ne(LessonDO::getStatus, 2) // 过滤掉已删除的课节（status=2）
            .orderByAsc(LessonDO::getStartTime); // 按开始时间正序排列
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
     * 重写查询构造器，添加通用的状态过滤（过滤掉已删除的课节：status!=2）
     */
    @Override
    protected QueryWrapper<LessonDO> buildQueryWrapper(LessonQuery query) {
        QueryWrapper<LessonDO> queryWrapper = super.buildQueryWrapper(query);
        // 添加通用的状态过滤条件：排除已删除的课节（status=2）
        queryWrapper.ne("status", 2);
        return queryWrapper;
    }

}