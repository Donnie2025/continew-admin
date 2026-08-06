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

import top.continew.admin.common.model.resp.BaseResp;
import java.io.Serial;
import java.time.*;

/**
 * 预约信息
 *
 * @author don
 * @since 2025/05/23 23:25
 */
@Data
@Schema(description = "预约信息")
public class BookingResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属课程ID
     */
    @Schema(description = "所属课程ID")
    private Long slotId;

    /**
     * 开课日期（格式：YYYYMMDD）
     */
    @Schema(description = "开课日期")
    private String slotDate;

    /**
     * 开课时间（格式：HH:MM）
     */
    @Schema(description = "开课时间")
    private String slotTime;

    /**
     * 所属学生姓名
     */
    @Schema(description = "所属学生姓名")
    private String studentName;

    /**
     * 学生手机号
     */
    @Schema(description = "学生手机号")
    private String studentPhone;

    /**
     * 学生会员卡ID
     */
    @Schema(description = "学生会员卡ID")
    private Long stuCardId;

    /**
     * 预约会员卡名称
     */
    @Schema(description = "预约会员卡名称")
    private String cardName;

    /**
     * 操作人名字
     */
    @Schema(description = "操作人名字")
    private String operatorName;

    /**
     * 操作时间
     */
    @Schema(description = "操作时间")
    private LocalDateTime operateTime;

    /**
     * 教师姓名
     */
    @Schema(description = "教师姓名")
    private String teacherName;

    /**
     * 预约教材ID
     */
    @Schema(description = "预约教材ID")
    private Long materialId;

    /**
     * 预约教材名字
     */
    @Schema(description = "预约教材名字")
    private String materialName;

    /**
     * 预约教材级别
     */
    @Schema(description = "预约教材级别")
    private String materialLevel;

    /**
     * 课节ID
     */
    @Schema(description = "课节ID")
    private Long lessonId;

    /**
     * 课节名称
     */
    @Schema(description = "课节名称")
    private String lessonName;

    /**
     * 课节URL
     */
    @Schema(description = "课节URL")
    private String lessonUrl;

    /**
     * 预约备注
     */
    @Schema(description = "预约备注")
    private String remark;

    /**
     * 状态（1：启用；0：禁用）
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private Long updateUser;
}