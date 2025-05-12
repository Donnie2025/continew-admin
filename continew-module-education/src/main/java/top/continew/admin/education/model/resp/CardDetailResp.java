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
 * 会员卡管理详情信息
 *
 * @author don
 * @since 2025/05/10 00:06
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "会员卡管理详情信息")
public class CardDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会员卡名称
     */
    @Schema(description = "会员卡名称")
    @ExcelProperty(value = "会员卡名称")
    private String name;

    /**
     * 会员卡类型
     */
    @Schema(description = "会员卡类型")
    @ExcelProperty(value = "会员卡类型")
    private Integer type;

    /**
     * 可用次数
     */
    @Schema(description = "可用次数")
    @ExcelProperty(value = "可用次数")
    private Integer availableCount;

    /**
     * 有效天数
     */
    @Schema(description = "有效天数")
    @ExcelProperty(value = "有效天数")
    private Integer availableDay;

    /**
     * 可用余额
     */
    @Schema(description = "可用余额")
    @ExcelProperty(value = "可用余额")
    private BigDecimal availableBalance;

    /**
     * 代理售卖价格
     */
    @Schema(description = "代理售卖价格")
    @ExcelProperty(value = "代理售卖价格")
    private BigDecimal price;

    /**
     * 是否仅代理可售
     */
    @Schema(description = "是否仅代理可售")
    @ExcelProperty(value = "是否仅代理可售")
    private Integer isAgentOnly;

    /**
     * 是否支持线上购卡（1：支持；0：不支持）
     */
    @Schema(description = "是否支持线上购卡（1：支持；0：不支持）")
    @ExcelProperty(value = "是否支持线上购卡（1：支持；0：不支持）")
    private Integer isOnlineSale;

    /**
     * 是否可续费
     */
    @Schema(description = "是否可续费")
    @ExcelProperty(value = "是否可续费")
    private Integer isRenewable;

    /**
     * 续费次数
     */
    @Schema(description = "续费次数")
    @ExcelProperty(value = "续费次数")
    private Integer renewTimes;

    /**
     * 续费天数
     */
    @Schema(description = "续费天数")
    @ExcelProperty(value = "续费天数")
    private Integer renewDays;

    /**
     * 续费价格
     */
    @Schema(description = "续费价格")
    @ExcelProperty(value = "续费价格")
    private BigDecimal renewPrice;

    /**
     * 状态
     */
    @Schema(description = "状态")
    @ExcelProperty(value = "状态")
    private Integer status;
}