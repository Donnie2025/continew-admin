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
 * 会员绑卡详情信息
 *
 * @author don
 * @since 2025/05/10 22:11
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "会员绑卡详情信息")
public class StuCardDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 会员卡类型（1：次卡有限期；2：次卡无限期；3：储蓄卡有限期；4：储蓄卡无限期）
     */
    @Schema(description = "会员卡类型（1：次卡有限期；2：次卡无限期；3：储蓄卡有限期；4：储蓄卡无限期）")
    @ExcelProperty(value = "会员卡类型（1：次卡有限期；2：次卡无限期；3：储蓄卡有限期；4：储蓄卡无限期）")
    private Integer cardType;

    /**
     * 剩余次数/余额
     */
    @Schema(description = "剩余次数/余额")
    @ExcelProperty(value = "剩余次数/余额")
    private BigDecimal balance;

    /**
     * 到期日期
     */
    @Schema(description = "到期日期")
    @ExcelProperty(value = "到期日期")
    private LocalDate expireDate;

    /**
     * 状态（1：启用；0：禁用）
     */
    @Schema(description = "状态（1：启用；0：禁用）")
    @ExcelProperty(value = "状态（1：启用；0：禁用）")
    private Integer status;

    /**
     * 卡状态（1：启用，学生端可见；0：禁用，学生端不可见，后台管理系统可见）
     */
    @Schema(description = "卡状态（1：启用，学生端可见；0：禁用，学生端不可见，后台管理系统可见）")
    @ExcelProperty(value = "卡状态（1：启用，学生端可见；0：禁用，学生端不可见，后台管理系统可见）")
    private Integer cardStatus;
}