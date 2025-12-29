package top.continew.admin.education.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;
import java.io.Serial;
import java.time.*;

/**
 * 课节信息
 *
 * @author don
 * @since 2025/12/29 21:22
 */
@Data
@Schema(description = "课节信息")
public class MaterialLessonResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教材ID
     */
    @Schema(description = "教材ID")
    private Long materialId;

    /**
     * 教材名称（冗余字段，格式：name + level）
     */
    @Schema(description = "教材名称（冗余字段，格式：name + level）")
    private String materialName;

    /**
     * 课节名字
     */
    @Schema(description = "课节名字")
    private String lessonName;

    /**
     * 课节链接
     */
    @Schema(description = "课节链接")
    private String lessonUrl;

    /**
     * 状态（1:启用 0:禁用）
     */
    @Schema(description = "状态（1:启用 0:禁用）")
    private Integer status;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private Long updateUser;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}