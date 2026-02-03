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
import java.time.LocalDateTime;

/**
 * 固定课操作记录实体
 *
 * @author Charles7c
 * @since 2024/12/28 17:00
 */
@Data
@TableName("edu_fixed_log")
public class FixedLogDO extends BaseDO {

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
     * 教师ID
     */
    private Long teacherId;

    /**
     * 教师姓名（冗余字段）
     */
    private String teacherName;

    /**
     * 操作类型：1-预约，2-取消
     */
    private Integer opType;

    /**
     * 操作描述
     */
    private String opDesc;

    /**
     * 操作时间
     */
    private LocalDateTime opTime;

    /**
     * 操作人ID
     */
    private Long opUser;

    /**
     * 操作人姓名（冗余字段）
     */
    private String opUserName;

}
