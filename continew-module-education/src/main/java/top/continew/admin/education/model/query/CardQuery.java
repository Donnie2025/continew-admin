package top.continew.admin.education.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;
import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.math.BigDecimal;

/**
 * 会员卡管理查询条件
 *
 * @author don
 * @since 2025/05/10 00:06
 */
@Data
@Schema(description = "会员卡管理查询条件")
public class CardQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会员卡名称
     */
    @Schema(description = "会员卡名称")
    @Query(type = QueryType.EQ)
    private String name;
}