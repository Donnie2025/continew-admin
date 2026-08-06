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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 固定课预约响应信息
 *
 * @author Charles7c
 * @since 2024/12/28 20:00
 */
@Data
@Schema(description = "固定课预约响应信息")
public class FixedBookingResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "ID")
    private Long id;

    /**
     * 固定课ID
     */
    @Schema(description = "固定课ID")
    private Long fixedId;

    /**
     * 教师姓名
     */
    @Schema(description = "教师姓名")
    private String teacherName;

    /**
     * 星期几：1-周一，2-周二，3-周三，4-周四，5-周五，6-周六，7-周日
     */
    @Schema(description = "星期几")
    private Integer weekDay;

    /**
     * 开始时间（HH:MM格式）
     */
    @Schema(description = "开始时间")
    private String startTime;

    /**
     * 操作人
     */
    @Schema(description = "操作人")
    private String createUserString;

    /**
     * 操作时间
     */
    @Schema(description = "操作时间")
    private LocalDateTime createTime;

    /**
     * 状态：0-已取消，1-已预约
     */
    @Schema(description = "状态")
    private Integer status;
}
