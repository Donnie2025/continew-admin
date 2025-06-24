package top.continew.admin.education.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;
import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 课堂查询条件
 *
 * @author don
 * @since 2025/06/24 23:39
 */
@Data
@Schema(description = "课堂查询条件")
public class LessonQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 课程ID
     */
    @Schema(description = "课程ID")
    @Query(type = QueryType.EQ)
    private Long courseId;

    /**
     * ClassIn 课程ID
     */
    @Schema(description = "ClassIn 课程ID")
    @Query(type = QueryType.EQ)
    private Long courseUid;

    /**
     * 课堂活动名称
     */
    @Schema(description = "课堂活动名称")
    @Query(type = QueryType.EQ)
    private String name;

    /**
     * 主讲教师UID
     */
    @Schema(description = "主讲教师UID")
    @Query(type = QueryType.EQ)
    private Long teacherUid;

    /**
     * 活动开始时间
     */
    @Schema(description = "活动开始时间")
    @Query(type = QueryType.EQ)
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    @Schema(description = "活动结束时间")
    @Query(type = QueryType.EQ)
    private LocalDateTime endTime;
}