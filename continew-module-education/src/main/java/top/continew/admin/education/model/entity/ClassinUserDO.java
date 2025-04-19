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
 * Classin用户实体
 *
 * @author donnie
 * @since 2025/04/12 20:49
 */
@Data
@TableName("classin_user")
public class ClassinUserDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 成员类型（0：不是成员；1：学生；2：老师）
     */
    private Integer userType;

    /**
     * Classin唯一映射关系
     */
    private String uid;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String telephone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 关联学生ID
     */
    private Long studentId;

    /**
     * 关联教师ID
     */
    private Long teacherId;

    /**
     * 关联Classin机构ID
     */
    private Long classinInstitutionId;

    /**
     * 状态（1：启用；2：禁用）
     */
    private Integer status;
}