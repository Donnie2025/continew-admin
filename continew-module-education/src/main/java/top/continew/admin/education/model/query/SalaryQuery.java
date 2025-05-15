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
 * 薪资查询条件
 *
 * @author don
 * @since 2025/05/13 22:43
 */
@Data
@Schema(description = "薪资查询条件")
public class SalaryQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教师姓名
     */
    @Schema(description = "教师姓名")
    @Query(type = QueryType.EQ)
    private String teacherName;

    /**
     * 起始日期
     */
    @Schema(description = "起始日期")
    @Query(type = QueryType.GE)
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @Schema(description = "结束日期")
    @Query(type = QueryType.LE)
    private LocalDate endDate;

    /**
     * 状态（0：未结算；1：已结算）
     */
    @Schema(description = "状态（0：未结算；1：已结算）")
    @Query(type = QueryType.EQ)
    private Integer status;

    /**
     * 所属组
     */
    @Schema(description = "所属组")
    @Query(type = QueryType.EQ)
    private String groupName;
}