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

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 批量预约请求参数
 *
 * @author continew-org
 * @since 2025-01-02
 */
@Data
@Schema(description = "批量预约请求参数")
public class BatchBookingReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教师ID
     */
    @Schema(description = "教师ID")
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;

    /**
     * 教材ID
     */
    @Schema(description = "教材ID")
    @NotNull(message = "教材ID不能为空")
    private Long materialId;

    /**
     * 课程ID（对应edu_material_lesson表的ID，单节课时使用）
     */
    @Schema(description = "课程ID")
    private Long lessonId;

    /**
     * 课时账户ID（关联edu_account表）
     */
    @Schema(description = "课时账户ID")
    private Long accountId;

    /**
     * 时间段列表
     */
    @Schema(description = "时间段列表")
    @NotEmpty(message = "时间段列表不能为空")
    private List<SlotInfo> slots;

    /**
     * 课程类型
     */
    @Schema(description = "课程类型")
    private String courseType;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String note;

    /**
     * 总价格
     */
    @Schema(description = "总价格")
    private BigDecimal totalPrice;

    /**
     * 时间段数量
     */
    @Schema(description = "时间段数量")
    private Integer slotCount;

    /**
     * 支付方式
     */
    @Schema(description = "支付方式")
    private String paymentMethod;

    /**
     * 时间段信息
     */
    @Data
    @Schema(description = "时间段信息")
    public static class SlotInfo implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 时间段ID
         */
        @Schema(description = "时间段ID")
        @NotNull(message = "时间段ID不能为空")
        private String slotId;

        /**
         * 日期
         */
        @Schema(description = "日期")
        @NotNull(message = "日期不能为空")
        private String date;

        /**
         * 开始时间
         */
        @Schema(description = "开始时间")
        @NotNull(message = "开始时间不能为空")
        private String startTime;

        /**
         * 课程ID（对应edu_material_lesson表的ID，每个时间段必须严格对应前端选择的课程）
         */
        @Schema(description = "课程ID")
        private Long lessonId;
    }
}
