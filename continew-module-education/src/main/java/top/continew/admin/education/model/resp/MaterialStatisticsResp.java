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
import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * 教材统计信息响应
 *
 * @author don
 * @since 2024-12-29
 */
@Data
@Schema(description = "教材统计信息响应")
public class MaterialStatisticsResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教材总数
     */
    @Schema(description = "教材总数")
    private Long totalCount;

    /**
     * 启用教材数
     */
    @Schema(description = "启用教材数")
    private Long enabledCount;

    /**
     * 禁用教材数
     */
    @Schema(description = "禁用教材数")
    private Long disabledCount;

    /**
     * 前端展示教材数
     */
    @Schema(description = "前端展示教材数")
    private Long displayCount;

    /**
     * 按节点类型统计（CATEGORY/BOOK/LEVEL/UNIT/LESSON）
     */
    @Schema(description = "按节点类型统计（CATEGORY/BOOK/LEVEL/UNIT/LESSON）")
    private Map<String, Long> typeStats;

    /**
     * 最近创建的教材数（7天内）
     */
    @Schema(description = "最近创建的教材数（7天内）")
    private Long recentCount;
}
