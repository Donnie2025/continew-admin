package top.continew.admin.education.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;
import java.io.Serial;
import java.time.*;
import java.math.BigDecimal;

/**
 * 订单详情信息
 *
 * @author don
 * @since 2025/05/10 22:11
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "订单详情信息")
public class TransactionDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 学生会员卡绑定表ID
     */
    @Schema(description = "学生会员卡绑定表ID")
    @ExcelProperty(value = "学生会员卡绑定表ID")
    private Long stuCardId;

    /**
     * 学生ID
     */
    @Schema(description = "学生ID")
    @ExcelProperty(value = "学生ID")
    private Long stuId;

    /**
     * 学生姓名
     */
    @Schema(description = "学生姓名")
    @ExcelProperty(value = "学生姓名")
    private String stuName;

    /**
     * 会员卡ID
     */
    @Schema(description = "会员卡ID")
    @ExcelProperty(value = "会员卡ID")
    private Long cardId;

    /**
     * 会员卡名称
     */
    @Schema(description = "会员卡名称")
    @ExcelProperty(value = "会员卡名称")
    private String cardName;

    /**
     * 变动类型（credit:充值, debit:扣费, freeze:冻结, activate:激活, cancel:取消约课, bind:首次绑卡, book_debit:约课扣费）
     */
    @Schema(description = "变动类型（credit:充值, debit:扣费, freeze:冻结, activate:激活, cancel:取消约课, bind:首次绑卡, book_debit:约课扣费）")
    @ExcelProperty(value = "变动类型（credit:充值, debit:扣费, freeze:冻结, activate:激活, cancel:取消约课, bind:首次绑卡, book_debit:约课扣费）")
    private String type;

    /**
     * 支出金额（扣款）
     */
    @Schema(description = "支出金额（扣款）")
    @ExcelProperty(value = "支出金额（扣款）")
    private BigDecimal debitAmount;

    /**
     * 收入金额（充值/收入）
     */
    @Schema(description = "收入金额（充值/收入）")
    @ExcelProperty(value = "收入金额（充值/收入）")
    private BigDecimal creditAmount;

    /**
     * 减少有效期天数
     */
    @Schema(description = "减少有效期天数")
    @ExcelProperty(value = "减少有效期天数")
    private Integer debitDays;

    /**
     * 增加有效期天数
     */
    @Schema(description = "增加有效期天数")
    @ExcelProperty(value = "增加有效期天数")
    private Integer creditDays;

    /**
     * 变动前余额/次数
     */
    @Schema(description = "变动前余额/次数")
    @ExcelProperty(value = "变动前余额/次数")
    private BigDecimal beforeAmount;

    /**
     * 变动后余额/次数
     */
    @Schema(description = "变动后余额/次数")
    @ExcelProperty(value = "变动后余额/次数")
    private BigDecimal afterAmount;

    /**
     * 实收金额
     */
    @Schema(description = "实收金额")
    @ExcelProperty(value = "实收金额")
    private BigDecimal actualAmount;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 操作人ID
     */
    @Schema(description = "操作人ID")
    @ExcelProperty(value = "操作人ID")
    private Long operatorId;

    /**
     * 操作人姓名
     */
    @Schema(description = "操作人姓名")
    @ExcelProperty(value = "操作人姓名")
    private String operatorName;
}