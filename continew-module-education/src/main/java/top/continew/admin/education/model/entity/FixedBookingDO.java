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

/**
 * 固定课预约实体
 *
 * @author Charles7c
 * @since 2024/12/28 17:00
 */
@Data
@TableName("edu_fixed_booking")
public class FixedBookingDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 固定课ID
     */
    private Long fixedId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学生姓名（冗余字段）
     */
    private String studentName;

    /**
     * 学生手机号（冗余字段）
     */
    private String studentPhone;

    /**
     * 教师ID（冗余字段）
     */
    private Long teacherId;

    /**
     * 教师姓名（冗余字段）
     */
    private String teacherName;

    /**
     * 星期几：1-周一，2-周二，3-周三，4-周四，5-周五，6-周六，7-周日
     */
    private Integer weekDay;

    /**
     * 开始时间（HH:MM格式）
     */
    private String startTime;

    /**
     * 课程时长（分钟）
     */
    private Integer durationMinutes;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

}
