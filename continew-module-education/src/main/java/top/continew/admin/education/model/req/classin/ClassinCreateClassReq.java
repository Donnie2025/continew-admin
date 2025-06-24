package top.continew.admin.education.model.req.classin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassIn 创建课堂活动请求对象
 *
 * @author don
 * @since 2025/06/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassinCreateClassReq {

    /**
     * 班级（课程）ID
     * 必填
     */
    private Long courseId;

    /**
     * 单元ID
     * 必填
     */
    private Long unitId;

    /**
     * 课堂活动名称
     * 必填，长度不超过50字
     */
    private String name;

    /**
     * 主讲教师UID
     * 必填
     */
    private Long teacherUid;

    /**
     * 活动开始时间
     * 必填，Unix Epoch 时间戳（秒单位）
     */
    private Long startTime;

    /**
     * 活动结束时间
     * 必填，Unix Epoch 时间戳（秒单位）
     */
    private Long endTime;

    /**
     * 录制类型
     * 必填（与recordState、liveState、openState一组完整参数）
     * 0-云端录制，1-本地录制
     */
    private Integer recordType;

    /**
     * 录制状态
     * 必填（与recordType、liveState、openState一组完整参数）
     * 0-不录制，1-录制
     */
    private Integer recordState;

    /**
     * 直播状态
     * 必填（与recordType、recordState、openState一组完整参数）
     * 0-不开启网页直播，1-开启网页直播
     */
    private Integer liveState;

    /**
     * 公开状态
     * 必填（与recordType、recordState、liveState一组完整参数）
     * 0-不公开，1-公开
     */
    private Integer openState;

    /**
     * 是否隐藏坐席区
     * 非必填
     * 0-显示坐席区(默认)，1-隐藏坐席区
     */
    private Integer cameraHide;

    /**
     * 录课封面
     * 非必填
     * 图片url或者是图片的base64内容，长度不能超过512000
     */
    private String recordCover;

    /**
     * 直播封面
     * 非必填
     * 图片url或者是图片的base64内容，长度不能超过512000
     */
    private String liveCover;

    /**
     * 直播介绍
     * 非必填
     * 长度不能超过2000
     */
    private String liveIntro;

    /**
     * 联席教师UID列表
     * 非必填
     * 格式：1234,4321,5678
     */
    private String teacherAssistantUids;

    /**
     * 是否向上课学生通知该课堂创建
     * 非必填
     * 0-不通知(默认)，1-通知
     */
    private Integer sendNotification;

    /**
     * 临时学生列表
     * 非必填
     * 字符串，可传入临时学生的uid，用","分隔，例如：1,2,3,4，超过100个将不处理
     */
    private String temporaryStudents;

    /**
     * 是否允许临时学生举手
     * 非必填
     * 0-不允许(默认)，1-允许
     */
    private Integer handsUpEnable;
} 