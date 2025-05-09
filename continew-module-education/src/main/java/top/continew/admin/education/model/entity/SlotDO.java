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
 * 课程管理实体
 *
 * @author don
 * @since 2025/04/25 23:24
 */
@Data
@TableName("edu_slot")
public class SlotDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属教师ID
     */
    private Long teacherId;

    /**
     * 教师名字
     */
    private String teacherName;

    /**
     * 开课日期（格式：YYYYMMDD）
     */
    private String startDate;

    /**
     * 开课时间（格式：HH:MM）
     */
    private String startTime;

    /**
     * 星期几（1：周一；2：周二；3：周三；4：周四；5：周五；6：周六；7：周日）
     */
    private Integer weekday;

    /**
     * 课程时长（单位为分钟）
     */
    private Integer duration;

    /**
     * 是否在线教室（0：否；1：是）
     */
    private Boolean isOnline;

    /**
     * 状态（1：启用；0：禁用）
     */
    private Integer status;

    /**
     * 所属机构ID
     */
    private Long institutionId;
}
