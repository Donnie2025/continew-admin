package top.continew.admin.education.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.admin.common.model.resp.BaseResp;
import java.io.Serial;
import java.time.*;
import java.math.BigDecimal;

/**
 * 会员卡管理信息
 *
 * @author don
 * @since 2025/05/10 00:06
 */
@Data
@Schema(description = "会员卡管理信息")
public class CardResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会员卡名称
     */
    @Schema(description = "会员卡名称")
    private String name;

    /**
     * 会员卡类型
     */
    @Schema(description = "会员卡类型")
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
    private Integer isAgentOnly;

    /**
     * 是否支持线上购卡（1：支持；0：不支持）
     */
    @Schema(description = "是否支持线上购卡（1：支持；0：不支持）")
    private Integer isOnlineSale;

    /**
     * 是否可续费
     */
    @Schema(description = "是否可续费")
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

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;
}