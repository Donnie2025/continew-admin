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

package top.continew.admin.education.client;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.continew.admin.education.config.ClassinProperties;
import top.continew.admin.education.constant.ClassinConstants;
import top.continew.admin.education.model.req.ClassinUserReq;
import top.continew.admin.education.model.req.classin.ClassinCourseAddReq;
import top.continew.admin.education.model.req.classin.ClassinCreateClassReq;
import top.continew.admin.education.model.req.classin.ClassinCreateUnitReq;
import top.continew.admin.education.model.req.classin.ClassinUpdateClassReq;
import top.continew.admin.education.model.resp.classin.ClassinBaseResp;
import top.continew.admin.education.model.resp.classin.ClassinCreateClassResp;
import top.continew.admin.education.model.resp.classin.ClassinCreateUnitResp;
import top.continew.admin.education.model.resp.classin.ClassinUpdateClassResp;
import top.continew.admin.education.utils.ClassinUtils;
import top.continew.starter.core.exception.BusinessException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * ClassIn API 客户端
 *
 * @author donnie, KAI
 * @since 2025/04/12 20:49
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClassinClient {

    private final ClassinProperties properties;

    @PostConstruct
    public void init() {
        log.info("开始初始化ClassIn客户端配置...");
        
        ClassinProperties.ApiConfig api = properties.getApi();
        ClassinProperties.AppConfig activeApp = properties.getActiveAppConfig();
        
        log.info("当前激活应用: {}", properties.getActive());
        log.info("当前配置信息: url={}, register={}, addSchoolStudent={}, addTeacher={}, appId={}", 
            api.getUrl(), api.getRegister(), api.getAddSchoolStudent(), api.getAddTeacher(), activeApp.getAppId());

        // 校验必要的配置参数
        if (StrUtil.isBlank(api.getUrl())) {
            throw new IllegalStateException("ClassIn API URL不能为空，请检查配置文件中的classin.api.url配置项");
        }
        if (StrUtil.isBlank(activeApp.getAppId())) {
            throw new IllegalStateException("ClassIn AppID不能为空，请检查配置文件中的classin应用配置项");
        }
        if (StrUtil.isBlank(activeApp.getAppSecret())) {
            throw new IllegalStateException("ClassIn AppSecret不能为空，请检查配置文件中的classin应用配置项");
        }
        if (StrUtil.isBlank(api.getRegister())) {
            throw new IllegalStateException("ClassIn注册接口路径不能为空，请检查配置文件中的classin.api.register配置项");
        }
        if (StrUtil.isBlank(api.getAddSchoolStudent())) {
            throw new IllegalStateException("ClassIn添加学生接口路径不能为空，请检查配置文件中的classin.api.addSchoolStudent配置项");
        }
        if (StrUtil.isBlank(api.getAddTeacher())) {
            throw new IllegalStateException("ClassIn添加教师接口路径不能为空，请检查配置文件中的classin.api.addTeacher配置项");
        }
        if (StrUtil.isBlank(api.getAddCourse())) {
            throw new IllegalStateException("ClassIn新增课程接口路径不能为空，请检查配置文件中的classin.api.addCourse配置项");
        }
        if (StrUtil.isBlank(api.getCreateClass())) {
            throw new IllegalStateException("ClassIn创建课堂活动接口路径不能为空，请检查配置文件中的classin.api.createClass配置项");
        }
        if (StrUtil.isBlank(api.getUpdateClass())) {
            throw new IllegalStateException("ClassIn编辑课堂活动接口路径不能为空，请检查配置文件中的classin.api.updateClass配置项");
        }
        if (StrUtil.isBlank(api.getCreateUnit())) {
            throw new IllegalStateException("ClassIn创建单元接口路径不能为空，请检查配置文件中的classin.api.createUnit配置项");
        }
        if (StrUtil.isBlank(api.getDeleteActivity())) {
            throw new IllegalStateException("ClassIn删除活动接口路径不能为空，请检查配置文件中的classin.api.deleteActivity配置项");
        }

        log.info("ClassIn客户端初始化完成");
    }

    /**
     * 调用ClassIn注册接口
     */
    public String registerClassin(ClassinUserReq req) {
        // 校验注册参数
        ClassinUtils.validateRegisterParams(req);

        // 构建请求参数
        JSONObject params = ClassinUtils.buildCommonParams(properties);
        // 手机号和邮箱二选一，且需要按照格式要求处理
        if (StrUtil.isNotBlank(req.getTelephone())) {
            params.set("telephone", req.getTelephone());
        } else if (StrUtil.isNotBlank(req.getEmail())) {
            params.set("email", req.getEmail());
        }
        params.set("password", req.getPassword());

        // 根据用户类型决定是否加为机构成员
        int addToSchoolMember = 0; // 默认不加为机构成员
        String userType = req.getUserType();

        if (ClassinConstants.USER_TYPE_STUDENT.equals(userType)) {
            // 学生类型，加为机构学生
            addToSchoolMember = 1;
        } else if (ClassinConstants.USER_TYPE_TEACHER.equals(userType)) {
            // 教师类型，加为机构老师
            addToSchoolMember = 2;
        }

        params.set("addToSchoolMember", addToSchoolMember);

        if (StrUtil.isNotBlank(req.getNickname())) {
            params.set("nickname", StrUtil.maxLength(req.getNickname(), 24));
        }

        String apiUrl = properties.getApi().getUrl() + properties.getApi().getRegister();
        ClassinBaseResp<String> resp = ClassinUtils.executePost(apiUrl, params, String.class);

        // 特殊处理：用户已注册也视为成功
        if (resp.getErrorInfo().getErrno() == 135 || resp.getErrorInfo().getErrno() == 461) {
            log.info("用户已在ClassIn注册，直接获取用户ID");
            return resp.getData();
        }

        if (resp.getErrorInfo().getErrno() != 1) {
            throw new BusinessException(String.format("注册失败（错误码：%d）：%s", resp.getErrorInfo().getErrno(), resp
                .getErrorInfo()
                .getError()));
        }
        return resp.getData();
    }

    /**
     * 调用 ClassIn 新增课程接口
     */
    public Long addCourse(ClassinCourseAddReq req) {
        // 1. 构建请求参数
        JSONObject params = ClassinUtils.buildCommonParams(properties);
        params.set("courseName", req.getCourseName());
        if (StrUtil.isNotBlank(req.getMainTeacherUid())) {
            params.set("mainTeacherUid", req.getMainTeacherUid());
        }
        if (StrUtil.isNotBlank(req.getCourseUniqueIdentity())) {
            params.set("courseUniqueIdentity", req.getCourseUniqueIdentity());
        }
        if (req.getClassroomSettingId() != null) {
            params.set("classroomSettingId", req.getClassroomSettingId());
        }

        // 2. 调用接口
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getAddCourse();
        ClassinBaseResp<Long> resp = ClassinUtils.executePost(apiUrl, params, Long.class);

        // 特殊处理：课程已存在也视为成功
        if (resp.getErrorInfo().getErrno() == 398) {
            return resp.getData();
        }

        if (resp.getErrorInfo().getErrno() != 1) {
            throw new BusinessException(String.format("新增课程失败（错误码：%d）：%s", resp.getErrorInfo().getErrno(), resp
                .getErrorInfo()
                .getError()));
        }
        return resp.getData();
    }

    /**
     * 调用 ClassIn 创建课堂活动接口
     */
    public ClassinCreateClassResp createClass(ClassinCreateClassReq req) {
        // 1. 构建请求体参数
        JSONObject bodyParams = new JSONObject();

        // 添加必填参数
        bodyParams.set("courseId", req.getCourseId());
        bodyParams.set("unitId", req.getUnitId());
        bodyParams.set("name", req.getName());
        bodyParams.set("teacherUid", req.getTeacherUid());
        bodyParams.set("startTime", req.getStartTime());
        bodyParams.set("endTime", req.getEndTime());
        bodyParams.set("recordType", req.getRecordType());
        bodyParams.set("recordState", req.getRecordState());
        bodyParams.set("liveState", req.getLiveState());
        bodyParams.set("openState", req.getOpenState());

        // 添加非必填参数
        if (req.getCameraHide() != null) {
            bodyParams.set("cameraHide", req.getCameraHide());
        }
        if (StrUtil.isNotBlank(req.getRecordCover())) {
            bodyParams.set("recordCover", req.getRecordCover());
        }
        if (StrUtil.isNotBlank(req.getLiveCover())) {
            bodyParams.set("liveCover", req.getLiveCover());
        }
        if (StrUtil.isNotBlank(req.getLiveIntro())) {
            bodyParams.set("liveIntro", req.getLiveIntro());
        }
        if (StrUtil.isNotBlank(req.getTeacherAssistantUids())) {
            bodyParams.set("teacherAssistantUids", req.getTeacherAssistantUids());
        }
        if (req.getSendNotification() != null) {
            bodyParams.set("sendNotification", req.getSendNotification());
        }
        if (StrUtil.isNotBlank(req.getTemporaryStudents())) {
            bodyParams.set("temporaryStudents", req.getTemporaryStudents());
        }
        if (req.getHandsUpEnable() != null) {
            bodyParams.set("handsUpEnable", req.getHandsUpEnable());
        }
        if (req.getSeatNum() != null) {
            bodyParams.set("seatNum", req.getSeatNum());
        }

        // 2. 构建Header参数（API v2方式）
        Map<String, String> headers = ClassinUtils.buildHeaderParams(properties, bodyParams);

        // 3. 调用接口
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getCreateClass();
        JSONObject result = ClassinUtils.executePostRequestV2(apiUrl, headers, bodyParams, "创建课堂活动");

        // 4. 解析响应数据
        JSONObject data = result.getJSONObject("data");
        ClassinCreateClassResp resp = new ClassinCreateClassResp();
        resp.setActivityId(data.getLong("activityId"));
        resp.setClassId(data.getLong("classId"));
        resp.setName(data.getStr("name"));
        resp.setLiveUrl(data.getStr("live_url"));

        // 解析拉流地址信息
        if (data.containsKey("live_info")) {
            JSONObject liveInfo = data.getJSONObject("live_info");
            ClassinCreateClassResp.LiveInfo info = new ClassinCreateClassResp.LiveInfo();
            info.setRTMP(liveInfo.getStr("RTMP"));
            info.setHLS(liveInfo.getStr("HLS"));
            info.setFLV(liveInfo.getStr("FLV"));
            resp.setLiveInfo(info);
        }

        return resp;
    }

    public String addStudent(ClassinUserReq req) {
        // 构建请求参数
        JSONObject params = ClassinUtils.buildCommonParams(properties);
        params.set("password", req.getPassword());
        params.set("nickname", req.getNickname());

        // 调用ClassIn添加学生接口
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getAddSchoolStudent();
        JSONObject result = ClassinUtils.executePostRequest(apiUrl, params, "添加学生");

        return result.getJSONObject("data").getStr("data");
    }

    public String addTeacher(ClassinUserReq req) {
        // 构建请求参数
        JSONObject params = ClassinUtils.buildCommonParams(properties);
        params.set("password", req.getPassword());
        params.set("nickname", req.getNickname());
        if (StrUtil.isNotBlank(req.getEmail())) {
            params.set("email", req.getEmail());
        }

        // 调用ClassIn添加教师接口
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getAddTeacher();
        JSONObject result = ClassinUtils.executePostRequest(apiUrl, params, "添加教师");

        return result.getJSONObject("data").getStr("data");
    }

    /**
     * 调用 ClassIn 编辑课堂活动接口
     */
    public ClassinUpdateClassResp updateClass(ClassinUpdateClassReq req) {
        // 1. 验证必填参数
        if (req.getCourseId() == null) {
            throw new BusinessException("课程ID不能为空");
        }
        if (req.getActivityId() == null) {
            throw new BusinessException("课堂活动ID不能为空");
        }

        // 2. 构建请求参数
        JSONObject params = ClassinUtils.buildCommonParams(properties);

        // 添加必填参数
        params.set("courseId", req.getCourseId());
        params.set("activityId", req.getActivityId());

        // 添加非必填参数，只传递需要修改的参数
        if (req.getUnitId() != null) {
            params.set("unitId", req.getUnitId());
        }
        if (StrUtil.isNotBlank(req.getName())) {
            params.set("name", req.getName());
        }
        if (req.getTeacherUid() != null) {
            params.set("teacherUid", req.getTeacherUid());
        }
        if (req.getStartTime() != null) {
            params.set("startTime", req.getStartTime());
        }
        if (req.getEndTime() != null) {
            params.set("endTime", req.getEndTime());
        }
        if (req.getPublishFlag() != null) {
            params.set("publishFlag", req.getPublishFlag());
        }

        // 录制、直播等相关设置，这些参数必须一起设置
        boolean hasRecordParams = req.getRecordType() != null || req.getRecordState() != null || req
            .getLiveState() != null || req.getOpenState() != null;

        if (hasRecordParams) {
            // 校验是否所有参数都已设置
            if (req.getRecordType() == null || req.getRecordState() == null || req.getLiveState() == null || req
                .getOpenState() == null) {
                throw new BusinessException("recordType、recordState、liveState、openState必须同时设置");
            }

            params.set("recordType", req.getRecordType());
            params.set("recordState", req.getRecordState());
            params.set("liveState", req.getLiveState());
            params.set("openState", req.getOpenState());
        }

        if (req.getCameraHide() != null) {
            params.set("cameraHide", req.getCameraHide());
        }
        if (StrUtil.isNotBlank(req.getRecordCover())) {
            params.set("recordCover", req.getRecordCover());
        }
        if (StrUtil.isNotBlank(req.getLiveCover())) {
            params.set("liveCover", req.getLiveCover());
        }
        if (StrUtil.isNotBlank(req.getLiveIntro())) {
            params.set("liveIntro", req.getLiveIntro());
        }
        if (StrUtil.isNotBlank(req.getTeacherAssistantUids())) {
            params.set("teacherAssistantUids", req.getTeacherAssistantUids());
        }
        if (req.getStageNum() != null) {
            params.set("stageNum", req.getStageNum());
        }
        if (req.getSeatNum() != null) {
            params.set("seatNum", req.getSeatNum());
        }
        if (req.getSubject() != null) {
            params.set("subject", req.getSubject());
        }
        if (req.getEnableTwoCamera() != null) {
            params.set("enableTwoCamera", req.getEnableTwoCamera());
        }

        // 3. 调用接口
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getUpdateClass();
        JSONObject result = ClassinUtils.executePostRequest(apiUrl, params, "编辑课堂活动");

        // 4. 解析响应数据
        JSONObject data = result.getJSONObject("data");
        ClassinUpdateClassResp resp = new ClassinUpdateClassResp();
        resp.setActivityId(data.getLong("activityId"));
        resp.setName(data.getStr("name"));

        return resp;
    }

    /**
     * 调用 ClassIn 创建单元接口
     */
    public ClassinCreateUnitResp createUnit(ClassinCreateUnitReq req) {
        // 1. 校验必填参数
        if (req.getCourseId() == null) {
            throw new BusinessException("课程ID不能为空");
        }
        if (StrUtil.isBlank(req.getName())) {
            throw new BusinessException("单元名称不能为空");
        }
        if (req.getPublishFlag() == null) {
            throw new BusinessException("发布标识不能为空");
        }

        // 2. 构建请求体参数
        JSONObject bodyParams = new JSONObject();
        bodyParams.set("courseId", req.getCourseId());
        bodyParams.set("name", req.getName());
        bodyParams.set("publishFlag", req.getPublishFlag());

        // 非必填参数
        if (StrUtil.isNotBlank(req.getContent())) {
            bodyParams.set("content", req.getContent());
        }

        // 3. 构建Header参数（API v2方式）
        Map<String, String> headers = ClassinUtils.buildHeaderParams(properties, bodyParams);

        // 4. 调用接口，设置可接受的错误码29208（单元已存在）
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getCreateUnit();
        List<Integer> acceptableErrorCodes = Collections.singletonList(29208); // 单元已存在
        JSONObject result = ClassinUtils
            .executePostRequestV2(apiUrl, headers, bodyParams, "创建单元", acceptableErrorCodes);

        // 5. 解析响应数据
        JSONObject data = result.getJSONObject("data");
        ClassinCreateUnitResp resp = new ClassinCreateUnitResp();
        resp.setName(data.getStr("name"));
        resp.setUnitId(data.getLong("unitId"));

        return resp;
    }

    /**
     * 调用 ClassIn 删除活动接口
     */
    //    public ClassinDeleteActivityResp deleteActivity(ClassinDeleteActivityReq req) {
    //        // 1. 校验必填参数
    //        if (req.getCourseId() == null) {
    //            throw new BusinessException("课程ID不能为空");
    //        }
    //        if (req.getActivityId() == null) {
    //            throw new BusinessException("活动ID不能为空");
    //        }
    //
    //        // 2. 构建请求体参数
    //        JSONObject bodyParams = new JSONObject();
    //        bodyParams.set("courseId", req.getCourseId());
    //        bodyParams.set("activityId", req.getActivityId());
    //
    //        // 3. 构建Header参数（API v2方式）
    //        Map<String, String> headers = ClassinUtils.buildHeaderParams(properties, bodyParams);
    //
    //        // 4. 调用接口
    //        String apiUrl = properties.getUrl() + properties.getDeleteActivity();
    //        JSONObject result = ClassinUtils.executePostRequestV2(apiUrl, headers, bodyParams, "删除活动");
    //
    //        // 5. 解析响应数据
    //        JSONObject data = result.getJSONObject("data");
    //        ClassinDeleteActivityResp resp = new ClassinDeleteActivityResp();
    //        resp.setActivityId(data.getLong("activityId"));
    //        resp.setName(data.getStr("name"));
    //
    //        return resp;
    //    }
}