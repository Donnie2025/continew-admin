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

package top.continew.admin.education.model.req;

import jakarta.validation.constraints.*;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 会员绑卡创建或修改参数
 *
 * @author don
 * @since 2025/05/10 22:11
 */
@Data
@Schema(description = "会员绑卡创建或修改参数")
public class StuCardReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 学生ID
     */
    @Schema(description = "学生ID")
    @NotNull(message = "学生ID不能为空")
    private Long stuId;

    /**
     * 会员卡ID
     */
    @Schema(description = "会员卡ID")
    @NotNull(message = "会员卡ID不能为空")
    private Long cardId;

    /**
     * 会员卡类型（1：次卡有限期；2：次卡无限期；3：储蓄卡有限期；4：储蓄卡无限期）
     */
    @Schema(description = "会员卡类型（1：次卡有限期；2：次卡无限期；3：储蓄卡有限期；4：储蓄卡无限期）")
    @NotNull(message = "会员卡类型（1：次卡有限期；2：次卡无限期；3：储蓄卡有限期；4：储蓄卡无限期）不能为空")
    private Integer cardType;

    /**
     * 卡状态（1：启用，学生端可见；0：禁用，学生端不可见，后台管理系统可见）
     */
    @Schema(description = "卡状态（1：启用，学生端可见；0：禁用，学生端不可见，后台管理系统可见）")
    @NotNull(message = "卡状态（1：启用，学生端可见；0：禁用，学生端不可见，后台管理系统可见）不能为空")
    private Integer cardStatus;
}