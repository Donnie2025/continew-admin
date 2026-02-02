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
     * 课节ID
     */
    private Long lessonId;

    /**
     * 开课日期（格式：YYYYMMDD）
     */
    private String slotDate;

    /**
     * 开课时间（格式：HH:MM）
     */
    private String slotTime;

    /**
     * 所属学生ID
     */
    private Long studentId;

    /**
     * 所属学生姓名
     */
    private String studentName;

    /**
     * 学生手机号
     */
    private String studentPhone;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 教师姓名
     */
    private String teacherName;

    /**
     * 学生会员卡ID（关联edu_stu_card表）
     */
    private Long stuCardId;

    /**
     * 预约会员卡名称
     */
    private String cardName;

    /**
     * 预约教材ID
     */
    private Long materialId;

    /**
     * 预约教材名字
     */
    private String materialName;

    /**
     * 预约教材编码
     */
    private String materialCode;

    /**
     * 预约教材级别
     */
    private String materialLevel;

    /**
     * 预约课节名字
     */
    private String lessonName;

    /**
     * 预约课节链接
     */
    private String lessonUrl;

    /**
     * 预约备注
     */
    private String remark;

    /**
     * 状态（1：启用；0：禁用）
     */
    private Integer status;
}
