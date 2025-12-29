package top.continew.admin.education.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;
import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 课节创建或修改参数
 *
 * @author don
 * @since 2025/12/29 21:22
 */
@Data
@Schema(description = "课节创建或修改参数")
public class MaterialLessonReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教材ID
     */
    @Schema(description = "教材ID")
    @NotNull(message = "教材ID不能为空")
    private Long materialId;

    /**
     * 教材名称（冗余字段，格式：name + level）
     */
    @Schema(description = "教材名称（冗余字段，格式：name + level）")
    @NotBlank(message = "教材名称（冗余字段，格式：name + level）不能为空")
    @Length(max = 150, message = "教材名称（冗余字段，格式：name + level）长度不能超过 {max} 个字符")
    private String materialName;

    /**
     * 课节名字
     */
    @Schema(description = "课节名字")
    @NotBlank(message = "课节名字不能为空")
    @Length(max = 100, message = "课节名字长度不能超过 {max} 个字符")
    private String lessonName;

    /**
     * 状态（1:启用 0:禁用）
     */
    @Schema(description = "状态（1:启用 0:禁用）")
    @NotNull(message = "状态（1:启用 0:禁用）不能为空")
    private Integer status;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    @NotNull(message = "创建人不能为空")
    private Long createUser;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @NotNull(message = "创建时间不能为空")
    private LocalDateTime createTime;
}