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

package top.continew.admin.education.model.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;

import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;
import java.time.*;

/**
 * 预约实体
 *
 * @author don
 * @since 2025/05/23 23:25
 */
@Data
@TableName("edu_booking")
public class BookingDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属课程ID
     */
    private Long slotId;

    /**
     * 
     */
    private String startDate;

    /**
     * 
     */
    private String startTime;

    /**
     * 所属学生ID
     */
    private Long studentId;

    /**
     * 所属学生姓名
     */
    private String studentName;

    /**
     * 预约手机号
     */
    private String phone;

    /**
     * 预约会员卡ID
     */
    private Long cardId;

    /**
     * 预约会员卡名称
     */
    private String cardName;

    /**
     * 操作人名字
     */
    private String operatorName;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;

    /**
     * 预约教材ID
     */
    private Long materialId;

    /**
     * 预约教材名字
     */
    private String materialName;

    /**
     * 预约课节名字
     */
    private String lessonName;

    /**
     * 预约教材链接
     */
    private String materialUrl;

    /**
     * 预约备注
     */
    private String remark;

    /**
     * 状态（1：启用；0：禁用）
     */
    private Integer status;
}
