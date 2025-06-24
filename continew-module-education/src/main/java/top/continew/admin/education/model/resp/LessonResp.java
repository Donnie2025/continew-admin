package top.continew.admin.education.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;
import java.io.Serial;
import java.time.*;

/**
 * 课堂信息
 *
 * @author don
 * @since 2025/06/24 23:39
 */
@Data
@Schema(description = "课堂信息")
public class LessonResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 课程ID
     */
    @Schema(description = "课程ID")
    private Long courseId;

    /**
     * ClassIn 课程ID
     */
    @Schema(description = "ClassIn 课程ID")
    private Long courseUid;

    /**
     * ClassIn 活动ID
     */
    @Schema(description = "ClassIn 活动ID")
    private Long activityUid;

    /**
     * ClassIn 课堂ID
     */
    @Schema(description = "ClassIn 课堂ID")
    private Long classUid;

    /**
     * 单元ID
     */
    @Schema(description = "单元ID")
    private Long unitUid;

    /**
     * 课堂活动名称
     */
    @Schema(description = "课堂活动名称")
    private String name;

    /**
     * 主讲教师UID
     */
    @Schema(description = "主讲教师UID")
    private Long teacherUid;

    /**
     * 活动开始时间
     */
    @Schema(description = "活动开始时间")
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    @Schema(description = "活动结束时间")
    private LocalDateTime endTime;

    /**
     * 上台人数
     */
    @Schema(description = "上台人数")
    private Integer seatNum;

    /**
     * 录制状态
     */
    @Schema(description = "录制状态")
    private Integer recordState;

    /**
     * 直播状态
     */
    @Schema(description = "直播状态")
    private Integer liveState;

    /**
     * 公开状态
     */
    @Schema(description = "公开状态")
    private Integer openState;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;
}