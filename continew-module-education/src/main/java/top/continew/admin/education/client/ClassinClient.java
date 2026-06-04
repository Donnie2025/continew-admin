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
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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
import top.continew.admin.education.model.resp.classin.ClassinCloudListResp;
import top.continew.admin.education.model.resp.classin.ClassinCreateClassResp;
import top.continew.admin.education.model.resp.classin.ClassinCreateUnitResp;
import top.continew.admin.education.model.resp.classin.ClassinUpdateClassResp;
import top.continew.admin.education.model.resp.InstitutionResp;
import top.continew.admin.education.service.InstitutionService;
import top.continew.admin.education.utils.ClassinUtils;
import top.continew.starter.core.exception.BusinessException;
import top.continew.starter.core.validation.CheckUtils;

import org.springframework.jdbc.core.JdbcTemplate;

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
    private final InstitutionService institutionService;
    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        log.info("开始初始化ClassIn客户端配置...");

        ClassinProperties.ApiConfig api = properties.getApi();

        log.info("ClassIn配置已启用，将从数据库 edu_institution 表读取激活机构的 AppId 和 AppSecret");
        log.info("当前配置信息: url={}, register={}, addSchoolStudent={}, addTeacher={}", api.getUrl(), api.getRegister(), api
            .getAddSchoolStudent(), api.getAddTeacher());

        // 校验必要的配置参数
        if (StrUtil.isBlank(api.getUrl())) {
            throw new IllegalStateException("ClassIn API URL不能为空，请检查配置文件中的classin.api.url配置项");
        }
        if (StrUtil.isBlank(api.getRegister())) {
            throw new IllegalStateException("ClassIn注册接口路径不能为空，请检查配置文件中的classin.api.register配置项");
        }
        if (StrUtil.isBlank(api.getAddSchoolStudent())) {
            throw new IllegalStateException("ClassIn添加学生接口路径不能为空，请检查配置文件中的classin.api.addSchoolStudent配置项");
        }
        if (StrUtil.isBlank(api.getEditSchoolStudent())) {
            throw new IllegalStateException("ClassIn编辑学生接口路径不能为空，请检查配置文件中的classin.api.editSchoolStudent配置项");
        }
        if (StrUtil.isBlank(api.getAddTeacher())) {
            throw new IllegalStateException("ClassIn添加教师接口路径不能为空，请检查配置文件中的classin.api.addTeacher配置项");
        }
        if (StrUtil.isBlank(api.getAddCourse())) {
            throw new IllegalStateException("ClassIn新增课程接口路径不能为空，请检查配置文件中的classin.api.addCourse配置项");
        }
        if (StrUtil.isBlank(api.getEditCourse())) {
            throw new IllegalStateException("ClassIn编辑课程接口路径不能为空，请检查配置文件中的classin.api.editCourse配置项");
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
        if (StrUtil.isBlank(api.getRemoveCourseStudent())) {
            throw new IllegalStateException("ClassIn删除课程学生接口路径不能为空，请检查配置文件中的classin.api.removeCourseStudent配置项");
        }

        log.info("ClassIn客户端初始化完成");
    }

    /**
     * 检查 ClassIn 对接开关是否开启
     * 读取字典 classin_config 中 label=classin_enabled 的值，1=开启，0=关闭
     */
    public boolean isClassinEnabled() {
        try {
            String sql = "SELECT di.`value` FROM sys_dict_item di" + " INNER JOIN sys_dict d ON di.dict_id = d.id" + " WHERE d.code = 'classin_config' AND di.label = 'classin_enabled'";
            List<String> values = jdbcTemplate.queryForList(sql, String.class);
            if (values == null || values.isEmpty()) {
                return true;
            }
            return "1".equals(values.get(0));
        } catch (Exception e) {
            log.warn("读取ClassIn开关配置失败，默认开启: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 获取激活机构的配置（从数据库读取）
     *
     * @return 包含appId和appSecret的数组，[0]为appId，[1]为appSecret
     */
    private String[] getActiveAppConfig() {
        InstitutionResp activeInstitution = institutionService.getActiveInstitution();
        if (activeInstitution == null) {
            throw new BusinessException("未找到激活的机构配置，请在数据库 edu_institution 表中设置 is_active=1 的机构");
        }

        if (StrUtil.isBlank(activeInstitution.getSid()) || StrUtil.isBlank(activeInstitution.getSecret())) {
            throw new BusinessException("激活机构的 SID（AppId）或 SECRET（AppSecret）为空，请检查数据库配置");
        }

        log.debug("使用数据库中的激活机构配置: {} (SID: {})", activeInstitution.getName(), activeInstitution.getSid());
        return new String[] {activeInstitution.getSid(), activeInstitution.getSecret()};
    }

    /**
     * 调用ClassIn注册接口
     *
     * @param req           注册请求
     * @param institutionId 机构ID
     * @return ClassIn用户UID
     */
    public String registerClassin(ClassinUserReq req, Long institutionId) {
        if (!isClassinEnabled()) {
            log.info("ClassIn对接已关闭，跳过注册操作");
            return null;
        }
        // 校验注册参数
        ClassinUtils.validateRegisterParams(req);

        // 根据机构ID获取配置
        InstitutionResp institution = institutionService.getById(institutionId);
        CheckUtils.throwIfNull(institution, "机构不存在，机构ID: {}", institutionId);

        String appId = institution.getSid();
        String appSecret = institution.getSecret();
        if (StrUtil.isBlank(appId) || StrUtil.isBlank(appSecret)) {
            throw new BusinessException(StrUtil.format("机构[{}]的 SID（AppId）或 SECRET（AppSecret）为空，请检查数据库配置", institution
                .getName()));
        }

        log.debug("使用机构[{}]的配置进行注册: SID={}", institution.getName(), appId);

        // 构建请求参数
        JSONObject params = ClassinUtils.buildCommonParams(appId, appSecret);
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
            log.info("用户已在ClassIn注册，直接获取用户ID: {}", resp.getData());
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
     * 调用ClassIn编辑学生接口
     * 
     * 官方API文档: https://docs.eeo.cn/api/zh-hans/user/editSchoolStudent.html
     * 
     * @param classinUid    ClassIn用户UID (studentUid)
     * @param studentName   新的学生姓名 (studentName)
     * @param institutionId 机构ID
     */
    public void editSchoolStudent(String classinUid, String studentName, Long institutionId) {
        if (!isClassinEnabled()) {
            log.info("ClassIn对接已关闭，跳过编辑学生操作");
            return;
        }
        // 参数校验
        if (StrUtil.isBlank(classinUid)) {
            throw new BusinessException("ClassIn用户UID不能为空");
        }
        if (StrUtil.isBlank(studentName)) {
            throw new BusinessException("学生姓名不能为空");
        }
        if (studentName.length() > 24) {
            throw new BusinessException("学生姓名不能超过24个字符");
        }
        if (institutionId == null) {
            throw new BusinessException("机构ID不能为空");
        }

        // 获取机构配置
        InstitutionResp institution = institutionService.getById(institutionId);
        CheckUtils.throwIfNull(institution, "机构不存在，机构ID: {}", institutionId);

        String appId = institution.getSid();
        String appSecret = institution.getSecret();
        if (StrUtil.isBlank(appId) || StrUtil.isBlank(appSecret)) {
            throw new BusinessException(StrUtil.format("机构[{}]的 SID（AppId）或 SECRET（AppSecret）为空，请检查数据库配置", institution
                .getName()));
        }

        log.info("开始调用ClassIn编辑学生接口: classinUid={}, studentName={}, institutionId={}", classinUid, studentName, institutionId);

        // 根据官方API文档构建请求参数
        JSONObject params = ClassinUtils.buildCommonParams(appId, appSecret);
        params.set("studentUid", classinUid);     // 官方参数名称
        params.set("studentName", studentName);  // 官方参数名称

        // 调用接口 - 使用官方API路径
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getEditSchoolStudent();
        ClassinBaseResp<Object> resp = ClassinUtils.executePost(apiUrl, params, Object.class);

        // 根据官方文档处理响应
        if (resp.getErrorInfo().getErrno() != 1) {
            String errorMsg;
            switch (resp.getErrorInfo().getErrno()) {
                case 100:
                    errorMsg = "参数不全或错误";
                    break;
                case 102:
                    errorMsg = "无权限（安全验证没通过）";
                    break;
                case 104:
                    errorMsg = "操作失败（未知错误）";
                    break;
                case 228:
                    errorMsg = "机构下无此学生";
                    break;
                default:
                    errorMsg = resp.getErrorInfo().getError();
            }
            throw new BusinessException(StrUtil.format("ClassIn编辑学生失败: {}", errorMsg));
        }

        log.info("ClassIn编辑学生成功: classinUid={}, studentName={}", classinUid, studentName);
    }

    /**
     * 调用 ClassIn 新增课程接口
     */
    public Long addCourse(ClassinCourseAddReq req) {
        if (!isClassinEnabled()) {
            log.info("ClassIn对接已关闭，跳过新增课程操作");
            return null;
        }
        // 获取激活机构配置
        String[] appConfig = getActiveAppConfig();

        // 1. 构建请求参数
        JSONObject params = ClassinUtils.buildCommonParams(appConfig[0], appConfig[1]);
        params.set("courseName", req.getCourseName());
        params.set("allowAddFriend", 0);
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
     * 调用 ClassIn 编辑课程接口
     */
    public void editCourse(ClassinCourseAddReq req, Long courseId) {
        if (!isClassinEnabled()) {
            log.info("ClassIn对接已关闭，跳过编辑课程操作");
            return;
        }
        // 1. 验证必填参数
        if (courseId == null) {
            throw new BusinessException("课程ID不能为空");
        }

        // 获取激活机构配置
        String[] appConfig = getActiveAppConfig();

        // 2. 构建请求参数
        JSONObject params = ClassinUtils.buildCommonParams(appConfig[0], appConfig[1]);
        params.set("courseId", courseId);

        // 添加需要修改的参数
        if (StrUtil.isNotBlank(req.getCourseName())) {
            params.set("courseName", req.getCourseName());
        }
        if (StrUtil.isNotBlank(req.getMainTeacherUid())) {
            params.set("mainTeacherUid", req.getMainTeacherUid());
        }
        if (req.getClassroomSettingId() != null) {
            params.set("classroomSettingId", req.getClassroomSettingId());
        }

        // 3. 调用接口
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getEditCourse();
        ClassinBaseResp<Object> resp = ClassinUtils.executePost(apiUrl, params, Object.class);

        if (resp.getErrorInfo().getErrno() != 1) {
            throw new BusinessException(String.format("编辑课程失败（错误码：%d）：%s", resp.getErrorInfo().getErrno(), resp
                .getErrorInfo()
                .getError()));
        }
    }

    /**
     * 调用 ClassIn 创建课堂活动接口
     */
    public ClassinCreateClassResp createClass(ClassinCreateClassReq req) {
        if (!isClassinEnabled()) {
            log.info("ClassIn对接已关闭，跳过创建课堂活动操作");
            return null;
        }
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
            // ClassIn的seatNum是总上台人数（包括老师），所以需要+1
            bodyParams.set("seatNum", req.getSeatNum() + 1);
        }
        if (StrUtil.isNotBlank(req.getCloudFolderId())) {
            try {
                bodyParams.set("cloudFolderId", Long.parseLong(req.getCloudFolderId()));
            } catch (NumberFormatException e) {
                log.warn("cloudFolderId非数字格式，跳过：{}", req.getCloudFolderId());
            }
        }

        // 获取激活机构配置
        String[] appConfig = getActiveAppConfig();

        // 2. 构建Header参数（API v2方式）
        Map<String, String> headers = ClassinUtils.buildHeaderParams(appConfig[0], appConfig[1], bodyParams);

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
        if (!isClassinEnabled()) {
            log.info("ClassIn对接已关闭，跳过添加学生操作");
            return null;
        }
        // 获取激活机构配置
        String[] appConfig = getActiveAppConfig();

        // 构建请求参数
        JSONObject params = ClassinUtils.buildCommonParams(appConfig[0], appConfig[1]);
        params.set("password", req.getPassword());
        params.set("nickname", req.getNickname());

        // 调用ClassIn添加学生接口
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getAddSchoolStudent();
        JSONObject result = ClassinUtils.executePostRequest(apiUrl, params, "添加学生");

        return result.getJSONObject("data").getStr("data");
    }

    public String addTeacher(ClassinUserReq req) {
        if (!isClassinEnabled()) {
            log.info("ClassIn对接已关闭，跳过添加教师操作");
            return null;
        }
        // 获取激活机构配置
        String[] appConfig = getActiveAppConfig();

        // 构建请求参数
        JSONObject params = ClassinUtils.buildCommonParams(appConfig[0], appConfig[1]);
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
     * 添加课程教师（API v2）
     *
     * @param courseId      课程ID
     * @param teacherUids   教师UID列表
     * @param institutionId 机构ID
     */
    public void addCourseTeacher(Long courseId, List<String> teacherUids, Long institutionId) {
        if (!isClassinEnabled()) {
            log.info("ClassIn对接已关闭，跳过添加课程教师操作");
            return;
        }
        // 根据机构ID获取机构配置
        InstitutionResp institution = institutionService.getById(institutionId);
        if (institution == null) {
            throw new BusinessException("机构不存在，ID: " + institutionId);
        }

        if (StrUtil.isBlank(institution.getSid()) || StrUtil.isBlank(institution.getSecret())) {
            throw new BusinessException("机构[" + institution.getName() + "]的 SID 或 SECRET 配置不全");
        }

        String appId = institution.getSid();
        String appSecret = institution.getSecret();

        // 构建请求体参数
        JSONObject bodyParams = new JSONObject();
        bodyParams.set("courseId", courseId);
        bodyParams.set("teacherUids", teacherUids);

        // 构建Header参数（API v2签名方式）
        Map<String, String> headers = ClassinUtils.buildHeaderParams(appId, appSecret, bodyParams);

        // 调用ClassIn添加课程教师接口
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getAddCourseTeacher();
        ClassinUtils.executePostRequestV2(apiUrl, headers, bodyParams, "添加课程教师");
    }

    /**
     * 移除课程教师
     *
     * @param courseId      课程ID
     * @param teacherUid    教师UID
     * @param institutionId 机构ID
     */
    public void removeCourseTeacher(Long courseId, String teacherUid, Long institutionId) {
        if (!isClassinEnabled()) {
            log.info("ClassIn对接已关闭，跳过移除课程教师操作");
            return;
        }
        // 根据机构ID获取机构配置
        InstitutionResp institution = institutionService.getById(institutionId);
        if (institution == null) {
            throw new BusinessException("机构不存在，ID: " + institutionId);
        }

        if (StrUtil.isBlank(institution.getSid()) || StrUtil.isBlank(institution.getSecret())) {
            throw new BusinessException("机构[" + institution.getName() + "]的 SID 或 SECRET 配置不全");
        }

        String appId = institution.getSid();
        String appSecret = institution.getSecret();

        // 构建请求参数
        JSONObject params = ClassinUtils.buildCommonParams(appId, appSecret);
        params.set("courseId", courseId);
        params.set("teacherUid", teacherUid);

        // 调用ClassIn移除课程教师接口
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getRemoveCourseTeacher();
        ClassinUtils.executePostRequest(apiUrl, params, "移除课程教师");
    }

    /**
     * 添加课程学生（单个）
     *
     * @param courseId      课程ID
     * @param studentUid    学生UID
     * @param institutionId 机构ID
     * @param identity      身份（1为学生，2为旁听）
     */
    public void addCourseStudent(Long courseId, String studentUid, Long institutionId, Integer identity) {
        if (!isClassinEnabled()) {
            log.info("ClassIn对接已关闭，跳过添加课程学生操作");
            return;
        }
        // 根据机构ID获取机构配置
        InstitutionResp institution = institutionService.getById(institutionId);
        if (institution == null) {
            throw new BusinessException("机构不存在，ID: " + institutionId);
        }

        if (StrUtil.isBlank(institution.getSid()) || StrUtil.isBlank(institution.getSecret())) {
            throw new BusinessException("机构[" + institution.getName() + "]的 SID 或 SECRET 配置不全");
        }

        String appId = institution.getSid();
        String appSecret = institution.getSecret();

        // 构建请求参数
        JSONObject params = ClassinUtils.buildCommonParams(appId, appSecret);
        params.set("courseId", courseId);
        params.set("studentUid", studentUid);
        params.set("identity", identity != null ? identity : 1); // 默认为学生

        // 调用ClassIn添加课程学生接口
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getAddCourseStudent();
        ClassinUtils.executePostRequest(apiUrl, params, "添加课程学生");
    }

    /**
     * 批量添加课程学生
     *
     * @param courseId      课程ID
     * @param studentUids   学生UID列表
     * @param institutionId 机构ID
     * @param identity      身份（1为学生，2为旁听）
     */
    public void addCourseStudentMultiple(Long courseId,
                                         List<String> studentUids,
                                         Long institutionId,
                                         Integer identity) {
        if (!isClassinEnabled()) {
            log.info("ClassIn对接已关闭，跳过批量添加课程学生操作");
            return;
        }
        // 根据机构ID获取机构配置
        InstitutionResp institution = institutionService.getById(institutionId);
        if (institution == null) {
            throw new BusinessException("机构不存在，ID: " + institutionId);
        }

        if (StrUtil.isBlank(institution.getSid()) || StrUtil.isBlank(institution.getSecret())) {
            throw new BusinessException("机构[" + institution.getName() + "]的 SID 或 SECRET 配置不全");
        }

        String appId = institution.getSid();
        String appSecret = institution.getSecret();

        // 构建studentJson数组：[{"uid":"xxx"},{"uid":"yyy"}]
        cn.hutool.json.JSONArray studentJsonArray = new cn.hutool.json.JSONArray();
        for (String studentUid : studentUids) {
            JSONObject studentObj = new JSONObject();
            studentObj.set("uid", studentUid);
            studentJsonArray.add(studentObj);
        }

        // 构建请求参数
        JSONObject params = ClassinUtils.buildCommonParams(appId, appSecret);
        params.set("courseId", courseId);
        params.set("studentJson", studentJsonArray.toString()); // JSON数组字符串
        params.set("identity", identity != null ? identity : 1); // 默认为学生

        // 调用ClassIn批量添加课程学生接口
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getAddCourseStudentMultiple();
        ClassinUtils.executePostRequest(apiUrl, params, "批量添加课程学生");
    }

    /**
     * 从课程中删除学生（单个）
     * 
     * 根据官方API文档: https://root_url/partner/api/course.api.php?action=delCourseStudent
     * 
     * @param courseId      课程ID
     * @param studentUid    需要删除的学生UID
     * @param institutionId 机构ID
     */
    public void removeCourseStudent(Long courseId, String studentUid, Long institutionId) {
        if (!isClassinEnabled()) {
            log.info("ClassIn对接已关闭，跳过移除课程学生操作");
            return;
        }
        // 参数校验
        if (courseId == null) {
            throw new BusinessException("课程ID不能为空");
        }
        if (StrUtil.isBlank(studentUid)) {
            throw new BusinessException("学生UID不能为空");
        }
        if (institutionId == null) {
            throw new BusinessException("机构ID不能为空");
        }

        // 根据机构ID获取机构配置
        InstitutionResp institution = institutionService.getById(institutionId);
        if (institution == null) {
            throw new BusinessException("机构不存在，ID: " + institutionId);
        }

        if (StrUtil.isBlank(institution.getSid()) || StrUtil.isBlank(institution.getSecret())) {
            throw new BusinessException("机构[" + institution.getName() + "]的 SID 或 SECRET 配置不全");
        }

        String appId = institution.getSid();
        String appSecret = institution.getSecret();

        log.info("开始从ClassIn课程删除学生: courseId={}, studentUid={}, institutionId={}", courseId, studentUid, institutionId);

        // 根据官方API文档构建请求参数
        JSONObject params = ClassinUtils.buildCommonParams(appId, appSecret);
        params.set("courseId", courseId);           // 课程ID
        params.set("identity", 1);                  // 学生身份（1为学生，2为旁听）
        params.set("studentUid", studentUid);       // 需要删除的学生UID

        // 调用ClassIn删除课程学生接口
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getRemoveCourseStudent();

        try {
            ClassinUtils.executePostRequest(apiUrl, params, "删除课程学生");
            log.info("成功从ClassIn课程删除学生: courseId={}, studentUid={}", courseId, studentUid);
        } catch (Exception e) {
            // 根据官方错误码进行特殊处理
            String errorMsg = e.getMessage();
            if (errorMsg.contains("162")) {
                log.warn("课程下无此成员，可能已被删除: courseId={}, studentUid={}", courseId, studentUid);
                return; // 学生不存在视为删除成功
            }
            throw new BusinessException("从ClassIn课程删除学生失败: " + errorMsg);
        }
    }

    /**
     * 调用 ClassIn 编辑课堂活动接口（API v2）
     */
    public ClassinUpdateClassResp updateClass(ClassinUpdateClassReq req) {
        if (!isClassinEnabled()) {
            log.info("ClassIn对接已关闭，跳过编辑课堂活动操作");
            return null;
        }
        // 1. 验证必填参数
        if (req.getCourseId() == null) {
            throw new BusinessException("课程ID不能为空");
        }
        if (req.getActivityId() == null) {
            throw new BusinessException("课堂活动ID不能为空");
        }

        // 2. 构建请求体参数
        JSONObject bodyParams = new JSONObject();

        // 添加必填参数
        bodyParams.set("courseId", req.getCourseId());
        bodyParams.set("activityId", req.getActivityId());

        // 添加非必填参数，只传递需要修改的参数
        if (req.getUnitId() != null) {
            bodyParams.set("unitId", req.getUnitId());
        }
        if (StrUtil.isNotBlank(req.getName())) {
            bodyParams.set("name", req.getName());
        }
        if (req.getTeacherUid() != null) {
            bodyParams.set("teacherUid", req.getTeacherUid());
        }
        if (req.getStartTime() != null) {
            bodyParams.set("startTime", req.getStartTime());
        }
        if (req.getEndTime() != null) {
            bodyParams.set("endTime", req.getEndTime());
        }
        if (req.getPublishFlag() != null) {
            bodyParams.set("publishFlag", req.getPublishFlag());
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

            bodyParams.set("recordType", req.getRecordType());
            bodyParams.set("recordState", req.getRecordState());
            bodyParams.set("liveState", req.getLiveState());
            bodyParams.set("openState", req.getOpenState());
        }

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
        if (req.getStageNum() != null) {
            bodyParams.set("stageNum", req.getStageNum());
        }
        if (req.getSeatNum() != null) {
            // ClassIn的seatNum是总上台人数（包括老师），所以需要+1
            bodyParams.set("seatNum", req.getSeatNum() + 1);
        }
        if (req.getSubject() != null) {
            bodyParams.set("subject", req.getSubject());
        }
        if (req.getEnableTwoCamera() != null) {
            bodyParams.set("enableTwoCamera", req.getEnableTwoCamera());
        }
        if (StrUtil.isNotBlank(req.getCloudFolderId())) {
            try {
                bodyParams.set("cloudFolderId", Long.parseLong(req.getCloudFolderId()));
            } catch (NumberFormatException e) {
                log.warn("cloudFolderId非数字格式，跳过：{}", req.getCloudFolderId());
            }
        }

        // 获取激活机构配置
        String[] appConfig = getActiveAppConfig();

        // 3. 构建Header参数（API v2方式）
        Map<String, String> headers = ClassinUtils.buildHeaderParams(appConfig[0], appConfig[1], bodyParams);

        // 4. 调用接口
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getUpdateClass();
        JSONObject result = ClassinUtils.executePostRequestV2(apiUrl, headers, bodyParams, "编辑课堂活动");

        // 5. 解析响应数据
        JSONObject data = result.getJSONObject("data");
        ClassinUpdateClassResp resp = new ClassinUpdateClassResp();
        resp.setActivityId(data.getLong("activityId"));
        resp.setName(data.getStr("name"));

        return resp;
    }

    /**
     * 删除 ClassIn 活动（课节）
     *
     * @param courseId      课程ID
     * @param activityId    活动ID
     * @param institutionId 机构ID
     */
    public void deleteActivity(Long courseId, Long activityId, Long institutionId) {
        if (!isClassinEnabled()) {
            log.info("ClassIn对接已关闭，跳过删除活动操作");
            return;
        }
        log.info("开始删除ClassIn活动，课程ID: {}, 活动ID: {}, 机构ID: {}", courseId, activityId, institutionId);

        // 1. 获取机构配置
        InstitutionResp institution = institutionService.getById(institutionId);
        CheckUtils.throwIfNull(institution, StrUtil.format("机构不存在，机构ID: {}", institutionId));

        String appId = institution.getSid();
        String appSecret = institution.getSecret();

        if (StrUtil.isBlank(appId) || StrUtil.isBlank(appSecret)) {
            throw new BusinessException(StrUtil.format("机构[{}]的ClassIn配置不完整", institution.getName()));
        }

        // 2. 构建请求体参数
        JSONObject bodyParams = new JSONObject();
        bodyParams.set("courseId", courseId);
        bodyParams.set("activityId", activityId);

        // 3. 构建Header参数
        Map<String, String> headers = ClassinUtils.buildHeaderParams(appId, appSecret, bodyParams);

        // 4. 调用接口（使用 API v2 格式）
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getDeleteActivity();
        ClassinUtils.executePostRequestV2(apiUrl, headers, bodyParams, "删除活动");

        log.info("成功删除ClassIn活动，课程ID: {}, 活动ID: {}", courseId, activityId);
    }

    /**
     * 调用 ClassIn 创建单元接口
     */
    public ClassinCreateUnitResp createUnit(ClassinCreateUnitReq req) {
        if (!isClassinEnabled()) {
            log.info("ClassIn对接已关闭，跳过创建单元操作");
            return null;
        }
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

        // 获取激活机构配置
        String[] appConfig = getActiveAppConfig();

        // 3. 构建Header参数（API v2方式）
        Map<String, String> headers = ClassinUtils.buildHeaderParams(appConfig[0], appConfig[1], bodyParams);

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
    //        ...
    //    }

    // ============================================================
    // 云盘相关接口
    // 文档：https://docs.eeo.cn/api/zh-hans/cloud/
    // 所有云盘接口使用 v1 鉴权（SID + safeKey + timeStamp 表单参数）
    // ============================================================

    /**
     * 获取机构云盘顶级文件夹ID
     * <p>
     * API: GET https://root_url/partner/api/cloud.api.php?action=getTopFolderId
     *
     * @return 顶级文件夹ID（字符串）
     */
    public String getCloudTopFolderId() {
        String[] appConfig = getActiveAppConfig();
        JSONObject params = ClassinUtils.buildCommonParams(appConfig[0], appConfig[1]);
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getCloudGetTopFolderId();
        ClassinBaseResp<String> resp = ClassinUtils.executePost(apiUrl, params, String.class);
        if (resp.getErrorInfo().getErrno() != 1) {
            throw new BusinessException(String.format("获取云盘顶级文件夹ID失败（错误码：%d）：%s", resp.getErrorInfo().getErrno(), resp
                .getErrorInfo()
                .getError()));
        }
        return resp.getData();
    }

    /**
     * 获取指定文件夹下的文件及文件夹列表
     * <p>
     * API: POST https://root_url/partner/api/cloud.api.php?action=getCloudList
     *
     * @param folderId 文件夹ID（传 null 或空字符串则获取根目录）
     * @return 云盘内容列表
     */
    public ClassinCloudListResp getCloudList(String folderId) {
        String[] appConfig = getActiveAppConfig();
        JSONObject params = ClassinUtils.buildCommonParams(appConfig[0], appConfig[1]);
        if (StrUtil.isNotBlank(folderId)) {
            params.set("folderId", folderId);
        }
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getCloudGetList();
        try {
            log.debug("调用 ClassIn 获取云盘列表接口: url={}, folderId={}", apiUrl, folderId);
            HttpResponse response = HttpRequest.post(apiUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .form(params)
                .timeout(10000)
                .execute();
            String body = response.body();
            if (StrUtil.isBlank(body)) {
                throw new BusinessException("获取云盘列表失败：接口响应为空");
            }
            JSONObject result = JSONUtil.parseObj(body);
            JSONObject errorInfo = result.getJSONObject("error_info");
            if (errorInfo != null && errorInfo.getInt("errno") != null && errorInfo.getInt("errno") != 1) {
                throw new BusinessException(String.format("获取云盘列表失败（错误码：%d）：%s", errorInfo.getInt("errno"), errorInfo
                    .getStr("error")));
            }
            ClassinCloudListResp listResp = new ClassinCloudListResp();
            JSONArray folderArray = result.getJSONArray("folder_list");
            JSONArray fileArray = result.getJSONArray("file_list");
            if (folderArray != null) {
                listResp.setFolderList(folderArray.toList(ClassinCloudListResp.FolderItem.class));
            }
            if (fileArray != null) {
                listResp.setFileList(fileArray.toList(ClassinCloudListResp.FileItem.class));
            }
            return listResp;
        } catch (Exception e) {
            log.error("获取云盘列表异常: {}", e.getMessage(), e);
            if (e instanceof BusinessException) {
                throw (BusinessException)e;
            }
            throw new BusinessException("获取云盘列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取机构云盘所有文件夹列表
     * <p>
     * API: POST https://root_url/partner/api/cloud.api.php?action=getFolderList
     *
     * @return 文件夹列表
     */
    public List<ClassinCloudListResp.FolderItem> getCloudFolderList() {
        String[] appConfig = getActiveAppConfig();
        JSONObject params = ClassinUtils.buildCommonParams(appConfig[0], appConfig[1]);
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getCloudGetFolderList();
        try {
            log.info("调用 ClassIn getFolderList 接口: url={}", apiUrl);
            HttpResponse response = HttpRequest.post(apiUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .form(params)
                .timeout(10000)
                .execute();
            String body = response.body();
            log.info("ClassIn getFolderList 原始响应: {}", body);
            if (StrUtil.isBlank(body)) {
                throw new BusinessException("获取云盘文件夹列表失败：接口响应为空");
            }
            JSONObject result = JSONUtil.parseObj(body);
            JSONObject errorInfo = result.getJSONObject("error_info");
            if (errorInfo != null && errorInfo.getInt("errno") != null && errorInfo.getInt("errno") != 1) {
                throw new BusinessException(String.format("获取云盘文件夹列表失败（错误码：%d）：%s", errorInfo.getInt("errno"), errorInfo
                    .getStr("error")));
            }
            // 尝试多种响应格式，安全获取 data 字段
            Object dataRaw = result.get("data");
            if (dataRaw instanceof JSONArray) {
                // 格式: data 直接是数组
                JSONArray dataArray = (JSONArray)dataRaw;
                if (!dataArray.isEmpty()) {
                    return dataArray.toList(ClassinCloudListResp.FolderItem.class);
                }
            } else if (dataRaw instanceof JSONObject) {
                // 格式: data 是对象，内有 folder_list
                JSONObject dataObj = (JSONObject)dataRaw;
                JSONArray nestedArray = dataObj.getJSONArray("folder_list");
                if (nestedArray != null && !nestedArray.isEmpty()) {
                    return nestedArray.toList(ClassinCloudListResp.FolderItem.class);
                }
            }
            // 格式: 顶级 folder_list 数组（同 getCloudList）
            JSONArray folderArray = result.getJSONArray("folder_list");
            if (folderArray != null && !folderArray.isEmpty()) {
                return folderArray.toList(ClassinCloudListResp.FolderItem.class);
            }
            log.warn("ClassIn getFolderList 未找到文件夹数据，完整响应: {}", body);
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            log.error("获取云盘文件夹列表异常: {}", e.getMessage(), e);
            if (e instanceof BusinessException) {
                throw (BusinessException)e;
            }
            throw new BusinessException("获取云盘文件夹列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取机构云盘所有文件夹列表（原始响应，用于调试）
     *
     * @return 原始响应字符串
     */
    public String getCloudFolderListRaw() {
        String[] appConfig = getActiveAppConfig();
        JSONObject params = ClassinUtils.buildCommonParams(appConfig[0], appConfig[1]);
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getCloudGetFolderList();
        HttpResponse response = HttpRequest.post(apiUrl)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .form(params)
            .timeout(10000)
            .execute();
        return response.body();
    }

    /**
     * 在指定目录下创建文件夹
     * <p>
     * API: POST https://root_url/partner/api/cloud.api.php?action=createFolder
     *
     * @param parentFolderId 父文件夹ID
     * @param folderName     新文件夹名称
     * @return 新文件夹ID
     */
    public String createCloudFolder(String parentFolderId, String folderName) {
        if (StrUtil.isBlank(parentFolderId)) {
            throw new BusinessException("父文件夹ID不能为空");
        }
        if (StrUtil.isBlank(folderName)) {
            throw new BusinessException("文件夹名称不能为空");
        }
        String[] appConfig = getActiveAppConfig();
        JSONObject params = ClassinUtils.buildCommonParams(appConfig[0], appConfig[1]);
        params.set("folderId", parentFolderId);
        params.set("folderName", folderName);
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getCloudCreateFolder();
        ClassinBaseResp<String> resp = ClassinUtils.executePost(apiUrl, params, String.class);
        if (resp.getErrorInfo().getErrno() != 1) {
            throw new BusinessException(String.format("创建文件夹失败（错误码：%d）：%s", resp.getErrorInfo().getErrno(), resp
                .getErrorInfo()
                .getError()));
        }
        return resp.getData();
    }

    /**
     * 删除云盘文件夹
     * <p>
     * API: POST https://root_url/partner/api/cloud.api.php?action=delFolder
     *
     * @param folderId 要删除的文件夹ID
     */
    public void deleteCloudFolder(String folderId) {
        if (StrUtil.isBlank(folderId)) {
            throw new BusinessException("文件夹ID不能为空");
        }
        String[] appConfig = getActiveAppConfig();
        JSONObject params = ClassinUtils.buildCommonParams(appConfig[0], appConfig[1]);
        params.set("folderId", folderId);
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getCloudDelFolder();
        ClassinBaseResp<Object> resp = ClassinUtils.executePost(apiUrl, params, Object.class);
        if (resp.getErrorInfo().getErrno() != 1) {
            throw new BusinessException(String.format("删除文件夹失败（错误码：%d）：%s", resp.getErrorInfo().getErrno(), resp
                .getErrorInfo()
                .getError()));
        }
    }

    /**
     * 删除云盘文件
     * <p>
     * API: POST https://root_url/partner/api/cloud.api.php?action=delFile
     *
     * @param fileId 要删除的文件ID
     */
    public void deleteCloudFile(String fileId) {
        if (StrUtil.isBlank(fileId)) {
            throw new BusinessException("文件ID不能为空");
        }
        String[] appConfig = getActiveAppConfig();
        JSONObject params = ClassinUtils.buildCommonParams(appConfig[0], appConfig[1]);
        params.set("fileId", fileId);
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getCloudDelFile();
        ClassinBaseResp<Object> resp = ClassinUtils.executePost(apiUrl, params, Object.class);
        if (resp.getErrorInfo().getErrno() != 1) {
            throw new BusinessException(String.format("删除文件失败（错误码：%d）：%s", resp.getErrorInfo().getErrno(), resp
                .getErrorInfo()
                .getError()));
        }
    }

    /**
     * 重命名云盘文件
     * <p>
     * API: POST https://root_url/partner/api/cloud.api.php?action=renameFile
     *
     * @param fileId   文件ID
     * @param fileName 新文件名
     */
    public void renameCloudFile(String fileId, String fileName) {
        if (StrUtil.isBlank(fileId)) {
            throw new BusinessException("文件ID不能为空");
        }
        if (StrUtil.isBlank(fileName)) {
            throw new BusinessException("文件名不能为空");
        }
        String[] appConfig = getActiveAppConfig();
        JSONObject params = ClassinUtils.buildCommonParams(appConfig[0], appConfig[1]);
        params.set("fileId", fileId);
        params.set("fileName", fileName);
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getCloudRenameFile();
        ClassinBaseResp<Object> resp = ClassinUtils.executePost(apiUrl, params, Object.class);
        if (resp.getErrorInfo().getErrno() != 1) {
            throw new BusinessException(String.format("重命名文件失败（错误码：%d）：%s", resp.getErrorInfo().getErrno(), resp
                .getErrorInfo()
                .getError()));
        }
    }

    /**
     * 重命名云盘文件夹
     * <p>
     * API: POST https://root_url/partner/api/cloud.api.php?action=renameFolder
     *
     * @param folderId   文件夹ID
     * @param folderName 新文件夹名称
     */
    public void renameCloudFolder(String folderId, String folderName) {
        if (StrUtil.isBlank(folderId)) {
            throw new BusinessException("文件夹ID不能为空");
        }
        if (StrUtil.isBlank(folderName)) {
            throw new BusinessException("文件夹名称不能为空");
        }
        String[] appConfig = getActiveAppConfig();
        JSONObject params = ClassinUtils.buildCommonParams(appConfig[0], appConfig[1]);
        params.set("folderId", folderId);
        params.set("folderName", folderName);
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getCloudRenameFolder();
        ClassinBaseResp<Object> resp = ClassinUtils.executePost(apiUrl, params, Object.class);
        if (resp.getErrorInfo().getErrno() != 1) {
            throw new BusinessException(String.format("重命名文件夹失败（错误码：%d）：%s", resp.getErrorInfo().getErrno(), resp
                .getErrorInfo()
                .getError()));
        }
    }

    /**
     * 上传文件到云盘
     * <p>
     * API: POST https://root_url/partner/api/cloud.api.php?action=uploadFile
     * Content-Type: multipart/form-data
     * 参数 Filedata 为文件内容，folderId 为可选目标文件夹
     *
     * @param folderId 目标文件夹ID（可为 null，上传到根目录）
     * @param fileData 文件内容字节数组
     * @param fileName 文件名（包含扩展名）
     * @return 上传后的文件ID
     */
    public String uploadCloudFile(String folderId, byte[] fileData, String fileName) {
        if (fileData == null || fileData.length == 0) {
            throw new BusinessException("文件内容不能为空");
        }
        if (StrUtil.isBlank(fileName)) {
            throw new BusinessException("文件名不能为空");
        }
        String[] appConfig = getActiveAppConfig();
        JSONObject commonParams = ClassinUtils.buildCommonParams(appConfig[0], appConfig[1]);
        String apiUrl = properties.getApi().getUrl() + properties.getApi().getCloudUploadFile();
        try {
            log.debug("上传文件到 ClassIn 云盘: url={}, fileName={}, folderId={}, size={}", apiUrl, fileName, folderId, fileData.length);
            HttpRequest request = HttpRequest.post(apiUrl).timeout(60000);
            request.form("SID", commonParams.getStr("SID"));
            request.form("safeKey", commonParams.getStr("safeKey"));
            request.form("timeStamp", commonParams.getStr("timeStamp"));
            if (StrUtil.isNotBlank(folderId)) {
                request.form("folderId", folderId);
            }
            request.form("Filedata", fileData, fileName);
            HttpResponse response = request.execute();
            String body = response.body();
            if (StrUtil.isBlank(body)) {
                throw new BusinessException("上传文件失败：接口响应为空");
            }
            log.debug("ClassIn 上传文件响应: {}", body);
            JSONObject result = JSONUtil.parseObj(body);
            JSONObject errorInfo = result.getJSONObject("error_info");
            if (errorInfo != null && errorInfo.getInt("errno") != null && errorInfo.getInt("errno") != 1) {
                throw new BusinessException(String.format("上传文件失败（错误码：%d）：%s", errorInfo.getInt("errno"), errorInfo
                    .getStr("error")));
            }
            String fileId = result.getStr("data");
            log.info("ClassIn 云盘文件上传成功: fileName={}, fileId={}", fileName, fileId);
            return fileId;
        } catch (Exception e) {
            log.error("上传文件到 ClassIn 云盘异常: {}", e.getMessage(), e);
            if (e instanceof BusinessException) {
                throw (BusinessException)e;
            }
            throw new BusinessException("上传文件失败：" + e.getMessage());
        }
    }
}