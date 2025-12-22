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
     * 
     */
    @Schema(description = "")
    private String startDate;

    /**
     * 
     */
    @Schema(description = "")
    private String startTime;

    /**
     * 所属学生姓名
     */
    @Schema(description = "所属学生姓名")
    private String studentName;

    /**
     * 预约手机号
     */
    @Schema(description = "预约手机号")
    private String phone;

    /**
     * 预约会员卡ID
     */
    @Schema(description = "预约会员卡ID")
    private Long cardId;

    /**
     * 预约会员卡标题
     */
    @Schema(description = "预约会员卡标题")
    private String cardTitle;

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
     * 预约教材名字
     */
    @Schema(description = "预约教材名字")
    private String materialName;

    /**
     * 预约备注
     */
    @Schema(description = "预约备注")
    private String remark;

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