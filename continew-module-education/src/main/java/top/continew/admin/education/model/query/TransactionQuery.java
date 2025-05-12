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
 * 订单查询条件
 *
 * @author don
 * @since 2025/05/10 22:11
 */
@Data
@Schema(description = "订单查询条件")
public class TransactionQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 学生ID
     */
    @Schema(description = "学生ID")
    @Query(type = QueryType.EQ)
    private Long stuId;

    /**
     * 会员卡ID
     */
    @Schema(description = "会员卡ID")
    @Query(type = QueryType.EQ)
    private Long cardId;

    /**
     * 变动类型（credit:充值, debit:扣费, freeze:冻结, activate:激活, cancel:取消约课, bind:首次绑卡, book_debit:约课扣费）
     */
    @Schema(description = "变动类型（credit:充值, debit:扣费, freeze:冻结, activate:激活, cancel:取消约课, bind:首次绑卡, book_debit:约课扣费）")
    @Query(type = QueryType.EQ)
    private String type;
}