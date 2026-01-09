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

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.time.*;
import java.math.BigDecimal;

/**
 * 会员绑卡实体
 *
 * @author don
 * @since 2025/05/10 22:11
 */
@Data
@TableName("edu_stu_card")
public class StuCardDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 学生ID
     */
    private Long stuId;

    /**
     * 学生姓名
     */
    private String stuName;

    /**
     * 会员卡ID
     */
    private Long cardId;

    /**
     * 会员卡名称
     */
    @TableField("card_title")
    private String cardName;

    /**
     * 会员卡类型（TL:次卡有限期 TU:次卡无限期 BL:储蓄卡有限期 BU:储蓄卡无限期）
     */
    private String cardType;

    /**
     * 余额（统一用于所有卡类型）
     */
    @TableField("balance")
    private BigDecimal balance;

    /**
     * 激活日期
     */
    private LocalDate activateDate;

    /**
     * 购买价格
     */
    private BigDecimal purchasePrice;

    /**
     * 备注
     */
    private String remark;

    /**
     * 到期日期
     */
    private LocalDate expireDate;

    /**
     * 状态（1：启用；0：禁用）
     */
    private Integer status;

    /**
     * 卡状态（1：启用，学生端可见；0：禁用，学生端不可见，后台管理系统可见）
     */
    private Integer cardStatus;
}
