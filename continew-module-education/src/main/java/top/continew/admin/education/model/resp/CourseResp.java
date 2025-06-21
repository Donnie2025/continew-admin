package top.continew.admin.education.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;
import java.io.Serial;
import java.time.*;

/**
 * 班级信息
 *
 * @author don
 * @since 2025/06/21 23:25
 */
@Data
@Schema(description = "班级信息")
public class CourseResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教室名称
     */
    @Schema(description = "教室名称")
    private String name;

    /**
     * 班主任ID
     */
    @Schema(description = "班主任ID")
    private Long mainTeacherId;

    /**
     * Classin班主任ID
     */
    @Schema(description = "Classin班主任ID")
    private String mainTeacherUid;

    /**
     * classin教室ID
     */
    @Schema(description = "classin教室ID")
    private Long courseUid;

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