package top.continew.admin.education.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;
import java.io.Serial;
import java.time.*;

/**
 * 课程管理信息
 *
 * @author don
 * @since 2025/04/25 23:24
 */
@Data
@Schema(description = "课程管理信息")
public class SlotResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属教师ID
     */
    @Schema(description = "所属教师ID")
    private Long teacherId;

    /**
     * 教师名字
     */
    @Schema(description = "教师名字")
    private String teacherName;

    /**
     * 开课日期（格式：YYYYMMDD）
     */
    @Schema(description = "开课日期（格式：YYYYMMDD）")
    private String startDate;

    /**
     * 开课时间（格式：HH:MM）
     */
    @Schema(description = "开课时间（格式：HH:MM）")
    private String startTime;

    /**
     * 星期几（1：周一；2：周二；3：周三；4：周四；5：周五；6：周六；7：周日）
     */
    @Schema(description = "星期几（1：周一；2：周二；3：周三；4：周四；5：周五；6：周六；7：周日）")
    private Boolean weekday;

    /**
     * 课程时长（单位为分钟）
     */
    @Schema(description = "课程时长（单位为分钟）")
    private Integer duration;

    /**
     * 是否在线教室（0：否；1：是）
     */
    @Schema(description = "是否在线教室（0：否；1：是）")
    private Boolean isOnline;

    /**
     * 状态（1：启用；0：禁用）
     */
    @Schema(description = "状态（1：启用；0：禁用）")
    private Integer status;

    /**
     * 所属机构ID
     */
    @Schema(description = "所属机构ID")
    private Long institutionId;
}