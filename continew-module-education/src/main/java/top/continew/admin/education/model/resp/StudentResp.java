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

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import top.continew.admin.common.model.resp.BaseResp;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;

/**
 * 学生管理信息
 *
 * @author don
 * @since 2025/04/20 01:32
 */
@Data
@Schema(description = "学生管理信息")
public class StudentResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 学生姓名
     */
    @Schema(description = "学生姓名")
    private String name;

    /**
     * 所属代理商编码
     */
    @Schema(description = "所属代理商编码")
    private String agentCode;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    private String phone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 注册时间
     */
    @Schema(description = "注册时间")
    private LocalDateTime registerTime;

    /**
     * 头像地址
     */
    @Schema(description = "头像地址")
    private String avatar;

    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 是否允许录课（0：不允许；1：允许）
     */
    @Schema(description = "是否允许录课（0：不允许；1：允许）")
    private Integer enableRecording;

    /**
     * 所属机构ID
     */
    @Schema(description = "所属机构ID")
    private Long institutionId;

    /**
     * 激活状态的会员卡列表
     */
    @Schema(description = "激活状态的会员卡列表")
    private List<CardBriefInfo> activeCards;

    /**
     * 会员卡简要信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "会员卡简要信息")
    public static class CardBriefInfo implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @Schema(description = "会员卡名称")
        private String cardName;

        @Schema(description = "会员卡类型（TL/TU/BL/BU）")
        private String cardType;

        @Schema(description = "余额")
        private BigDecimal balance;

        @Schema(description = "到期日期")
        private LocalDate expireDate;
    }
}