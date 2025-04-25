package top.continew.admin.education.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;
import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 课程管理查询条件
 *
 * @author don
 * @since 2025/04/25 23:24
 */
@Data
@Schema(description = "课程管理查询条件")
public class SlotQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教师名字
     */
    @Schema(description = "教师名字")
    @Query(type = QueryType.EQ)
    private String teacherName;

    /**
     * 开课日期（格式：YYYYMMDD）
     */
    @Schema(description = "开课日期（格式：YYYYMMDD）")
    @Query(type = QueryType.EQ)
    private String startDate;
}