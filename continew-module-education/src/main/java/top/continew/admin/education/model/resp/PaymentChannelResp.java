/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.continew.admin.education.model.resp;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 支付渠道响应信息
 *
 * @author don
 * @since 2026/06/09
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "支付渠道响应信息")
public class PaymentChannelResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "ID")
    @ExcelProperty(value = "ID")
    private Long id;

    /**
     * 渠道编码
     */
    @Schema(description = "渠道编码（alipay-支付宝 wechat-微信）")
    @ExcelProperty(value = "渠道编码")
    private String channelCode;

    /**
     * 渠道名称
     */
    @Schema(description = "渠道名称")
    @ExcelProperty(value = "渠道名称")
    private String channelName;

    /**
     * 支付类型
     */
    @Schema(description = "支付类型（online-在线支付 qrcode-扫码支付 offline-线下支付）")
    @ExcelProperty(value = "支付类型")
    private String paymentType;

    /**
     * 收款二维码图片地址
     */
    @Schema(description = "收款二维码图片地址")
    @ExcelProperty(value = "收款二维码")
    private String qrcodeImage;

    /**
     * 支付说明
     */
    @Schema(description = "支付说明")
    @ExcelProperty(value = "支付说明")
    private String description;

    /**
     * 排序
     */
    @Schema(description = "排序")
    @ExcelProperty(value = "排序")
    private Integer sort;

    /**
     * 状态
     */
    @Schema(description = "状态（1：启用；2：禁用）")
    @ExcelProperty(value = "状态")
    private Integer status;
}
