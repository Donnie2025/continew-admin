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
 * 教师查询条件
 *
 * @author donnie
 * @since 2025/04/04 18:33
 */
@Data
@Schema(description = "教师查询条件")
public class TeacherQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教师姓名
     */
    @Schema(description = "教师姓名")
    @Query(type = QueryType.EQ)
    private String name;

    /**
     * 教师工号
     */
    @Schema(description = "教师工号")
    @Query(type = QueryType.EQ)
    private String teacherNo;

    /**
     * 是否展示
     */
    @Schema(description = "是否展示")
    @Query(type = QueryType.EQ)
    private Integer isShow;

    /**
     * 排序
     */
    @Schema(description = "排序")
    @Query(type = QueryType.EQ)
    private Integer sort;
}