package top.continew.admin.education.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;
import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 班级查询条件
 *
 * @author don
 * @since 2025/06/21 23:25
 */
@Data
@Schema(description = "班级查询条件")
public class CourseQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教室名称
     */
    @Schema(description = "教室名称")
    @Query(type = QueryType.EQ)
    private String name;

    /**
     * 班主任ID
     */
    @Schema(description = "班主任ID")
    @Query(type = QueryType.EQ)
    private Long mainTeacherId;

    /**
     * 所属机构ID
     */
    @Schema(description = "所属机构ID")
    @Query(type = QueryType.EQ)
    private Long institutionId;
}