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
 * 班级实体
 *
 * @author don
 * @since 2025/06/21 23:25
 */
@Data
@TableName("edu_course")
public class CourseDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教室名称
     */
    private String name;

    /**
     * 所属代理机构编码
     */
    private String agentCode;

    /**
     * 班主任ID
     */
    private Long mainTeacherId;

    /**
     * Classin班主任ID
     */
    private String mainTeacherUid;

    /**
     * 机构课程唯一标识
     */
    private String courseUnique;

    /**
     * classin教室ID
     */
    private Long courseUid;

    /**
     * 教室设置ID
     */
    private Long courseSettingId;

    /**
     * 状态（1：启用；2：禁用）
     */
    private Integer status;

    /**
     * 是否展示（1：展示；0：不展示）
     */
    private Boolean isShow;

    /**
     * 所属机构ID
     */
    private Long institutionId;

    /**
     * 关联教材ID
     */
    private Long materialId;

    /**
     * 关联教材名称
     */
    private String materialName;

    /**
     * 备注
     */
    private String remark;
}
