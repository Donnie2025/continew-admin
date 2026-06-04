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

package top.continew.admin.education.model.query;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import top.continew.starter.data.core.annotation.Query;
import top.continew.starter.data.core.enums.QueryType;
import java.io.Serial;
import java.io.Serializable;
import java.time.*;

/**
 * 教材查询条件
 *
 * @author don
 * @since 2025/12/29 21:22
 */
@Data
@Schema(description = "教材查询条件")
public class MaterialQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 父节点ID
     */
    @Schema(description = "父节点ID")
    @Query(type = QueryType.EQ)
    private Long pid;

    /**
     * 节点类型（CATEGORY/BOOK/LEVEL/UNIT/LESSON）
     */
    @Schema(description = "节点类型（CATEGORY/BOOK/LEVEL/UNIT/LESSON）")
    @Query(type = QueryType.EQ)
    private String type;

    /**
     * 节点名称
     */
    @Schema(description = "节点名称")
    @Query(type = QueryType.LIKE)
    private String name;

    /**
     * 编码（BOOK/LEVEL层使用）
     */
    @Schema(description = "编码（BOOK/LEVEL层使用）")
    @Query(type = QueryType.EQ)
    private String code;

    /**
     * ClassIn云盘ID
     */
    @Schema(description = "ClassIn云盘ID")
    @Query(type = QueryType.EQ)
    private String cloudId;

    /**
     * 是否前端展示（1:展示 0:不展示）
     */
    @Schema(description = "是否前端展示（1:展示 0:不展示）")
    @Query(type = QueryType.EQ)
    private Boolean isShow;

    /**
     * 状态（1:启用 0:禁用）
     */
    @Schema(description = "状态（1:启用 0:禁用）")
    @Query(type = QueryType.EQ)
    private Integer status;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    @Query(type = QueryType.EQ)
    private Long createUser;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @Query(type = QueryType.EQ)
    private LocalDateTime createTime;
}