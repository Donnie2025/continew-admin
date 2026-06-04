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
 * 班级查询条件
 *
 * @author don
 * @since 2025/06/21 23:25
 */
@Data
@Schema(description = "班级查询条件")
public class CourseQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教室名称
     */
    @Schema(description = "教室名称")
    @Query(type = QueryType.LIKE)
    private String name;

    /**
     * 所属代理机构编码
     */
    @Schema(description = "所属代理机构编码")
    @Query(type = QueryType.EQ)
    private String agentCode;

    /**
     * 班主任ID
     */
    @Schema(description = "班主任ID")
    @Query(type = QueryType.EQ)
    private Long mainTeacherId;

    /**
     * 所属机构ID
     */
    @Schema(description = "所属机构ID")
    @Query(type = QueryType.EQ)
    private Long institutionId;
}