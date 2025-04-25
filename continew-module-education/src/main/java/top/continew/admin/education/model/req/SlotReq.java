package top.continew.admin.education.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;
import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 课程管理创建或修改参数
 *
 * @author don
 * @since 2025/04/25 23:24
 */
@Data
@Schema(description = "课程管理创建或修改参数")
public class SlotReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属教师ID
     */
    @Schema(description = "所属教师ID")
    @NotNull(message = "所属教师ID不能为空")
    private Long teacherId;

    /**
     * 教师名字
     */
    @Schema(description = "教师名字")
    @NotBlank(message = "教师名字不能为空")
    @Length(max = 50, message = "教师名字长度不能超过 {max} 个字符")
    private String teacherName;

    /**
     * 开课日期（格式：YYYYMMDD）
     */
    @Schema(description = "开课日期（格式：YYYYMMDD）")
    @NotBlank(message = "开课日期（格式：YYYYMMDD）不能为空")
    @Length(max = 8, message = "开课日期（格式：YYYYMMDD）长度不能超过 {max} 个字符")
    private String startDate;

    /**
     * 开课时间（格式：HH:MM）
     */
    @Schema(description = "开课时间（格式：HH:MM）")
    @NotBlank(message = "开课时间（格式：HH:MM）不能为空")
    @Length(max = 5, message = "开课时间（格式：HH:MM）长度不能超过 {max} 个字符")
    private String startTime;

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
     * 所属机构ID
     */
    @Schema(description = "所属机构ID")
    private Long institutionId;
}