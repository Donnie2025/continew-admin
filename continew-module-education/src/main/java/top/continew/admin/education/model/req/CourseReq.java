package top.continew.admin.education.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;
import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 班级创建或修改参数
 *
 * @author don
 * @since 2025/06/21 23:25
 */
@Data
@Schema(description = "班级创建或修改参数")
public class CourseReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教室名称
     */
    @Schema(description = "教室名称")
    @NotBlank(message = "教室名称不能为空")
    @Length(max = 100, message = "教室名称长度不能超过 {max} 个字符")
    private String name;

    /**
     * 班主任手机号
     */
    @Schema(description = "班主任手机号")
    private String mainTeacherPhone;

    /**
     * 教室设置ID
     */
    @Schema(description = "教室设置ID")
    private Long courseSettingId;

    /**
     * 所属机构ID
     */
    @Schema(description = "所属机构ID")
    private Long institutionId;
}