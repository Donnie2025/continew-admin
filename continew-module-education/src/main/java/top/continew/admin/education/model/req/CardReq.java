package top.continew.admin.education.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.validator.constraints.Length;
import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.math.BigDecimal;

/**
 * 会员卡管理创建或修改参数
 *
 * @author don
 * @since 2025/05/10 00:06
 */
@Data
@Schema(description = "会员卡管理创建或修改参数")
public class CardReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会员卡名称
     */
    @Schema(description = "会员卡名称")
    @NotBlank(message = "会员卡名称不能为空")
    @Length(max = 100, message = "会员卡名称长度不能超过 {max} 个字符")
    private String name;

    /**
     * 会员卡类型
     */
    @Schema(description = "会员卡类型")
    @NotNull(message = "会员卡类型不能为空")
    private Integer type;

    /**
     * 可用次数
     */
    @Schema(description = "可用次数")
    private Integer availableCount;

    /**
     * 有效天数
     */
    @Schema(description = "有效天数")
    private Integer availableDay;

    /**
     * 可用余额
     */
    @Schema(description = "可用余额")
    private BigDecimal availableBalance;

    /**
     * 代理售卖价格
     */
    @Schema(description = "代理售卖价格")
    private BigDecimal price;

    /**
     * 是否仅代理可售
     */
    @Schema(description = "是否仅代理可售")
    @NotNull(message = "是否仅代理可售不能为空")
    private Integer isAgentOnly;

    /**
     * 是否支持线上购卡（1：支持；0：不支持）
     */
    @Schema(description = "是否支持线上购卡（1：支持；0：不支持）")
    @NotNull(message = "是否支持线上购卡（1：支持；0：不支持）不能为空")
    private Integer isOnlineSale;

    /**
     * 是否可续费
     */
    @Schema(description = "是否可续费")
    @NotNull(message = "是否可续费不能为空")
    private Integer isRenewable;

    /**
     * 续费次数
     */
    @Schema(description = "续费次数")
    private Integer renewTimes;

    /**
     * 续费天数
     */
    @Schema(description = "续费天数")
    private Integer renewDays;

    /**
     * 续费价格
     */
    @Schema(description = "续费价格")
    private BigDecimal renewPrice;
    
    /**
     * 排序字段，值越小排序越靠前
     */
    @Schema(description = "排序字段，值越小排序越靠前")
    private Integer sort;
}