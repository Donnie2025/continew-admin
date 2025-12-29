package top.continew.admin.education.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;
import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 课节查询条件
 *
 * @author don
 * @since 2025/12/29 21:22
 */
@Data
@Schema(description = "课节查询条件")
public class MaterialLessonQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教材ID
     */
    @Schema(description = "教材ID")
    @Query(type = QueryType.EQ)
    private Long materialId;

    /**
     * 教材名称（冗余字段，格式：name + level）
     */
    @Schema(description = "教材名称（冗余字段，格式：name + level）")
    @Query(type = QueryType.EQ)
    private String materialName;

    /**
     * 课节名字
     */
    @Schema(description = "课节名字")
    @Query(type = QueryType.EQ)
    private String lessonName;

    /**
     * 状态（1:启用 0:禁用）
     */
    @Schema(description = "状态（1:启用 0:禁用）")
    @Query(type = QueryType.EQ)
    private Integer status;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    @Query(type = QueryType.EQ)
    private Long createUser;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @Query(type = QueryType.EQ)
    private LocalDateTime createTime;
}