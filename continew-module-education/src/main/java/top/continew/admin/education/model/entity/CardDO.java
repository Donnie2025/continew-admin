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

package top.continew.admin.education.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 会员卡管理实体
 *
 * @author don
 * @since 2025/05/10 00:06
 */
@Data
@TableName("edu_card")
public class CardDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会员卡标题
     */
    private String title;

    /**
     * 副标题
     */
    private String subTitle;

    /**
     * 会员卡描述
     */
    private String description;

    /**
     * 会员卡类型（TL:次卡有限期 TU:次卡无限期 BL:储蓄卡有限期 BU:储蓄卡无限期）
     * 缩写 英文全称 中文
     * TL Times Limited 次卡有限期
     * TU Times Unlimited 次卡无限期
     * BL Balance Limited 储蓄卡有限期
     * BU Balance Unlimited 储蓄卡无限期
     */
    private String type;

    /**
     * 初始次数
     */
    private Integer initTimes;

    /**
     * 初始有效天数
     */
    private Integer initDays;

    /**
     * 初始余额
     */
    private BigDecimal initBalance;

    /**
     * 代理售卖价格
     */
    private BigDecimal price;

    /**
     * 排序字段，值越小排序越靠前
     */
    private Integer sort;

    /**
     * 所属机构ID
     */
    private Long institutionId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private Integer status;
}
