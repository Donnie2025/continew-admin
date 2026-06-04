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
 * 课堂查询条件
 *
 * @author don
 * @since 2025/06/24 23:39
 */
@Data
@Schema(description = "课堂查询条件")
public class LessonQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 课程ID
     */
    @Schema(description = "课程ID")
    @Query(type = QueryType.EQ)
    private Long courseId;

    /**
     * ClassIn 课程ID
     */
    @Schema(description = "ClassIn 课程ID")
    @Query(type = QueryType.EQ)
    private Long courseUid;

    /**
     * 所属代理机构编码
     */
    @Schema(description = "所属代理机构编码")
    @Query(type = QueryType.EQ)
    private String agentCode;

    /**
     * 课堂活动名称
     */
    @Schema(description = "课堂活动名称")
    @Query(type = QueryType.LIKE)
    private String name;

    /**
     * 主讲教师UID
     */
    @Schema(description = "主讲教师UID")
    @Query(type = QueryType.EQ)
    private Long teacherUid;

    /**
     * 活动开始时间
     */
    @Schema(description = "活动开始时间")
    @Query(type = QueryType.EQ)
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    @Schema(description = "活动结束时间")
    @Query(type = QueryType.EQ)
    private LocalDateTime endTime;

    /**
     * 课程状态（started:已开课, not_started:未开课）
     * 注意：这是虚拟字段，不映射到数据库
     */
    @Schema(description = "课程状态")
    private String courseStatus;
}