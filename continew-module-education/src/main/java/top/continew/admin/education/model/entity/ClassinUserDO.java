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
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

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

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 成员类型（STUDENT, TEACHER）
     */
    private String userType;

    /**
     * Classin唯一映射关系
     */
    private String classinUid;

    /**
     * 关联学生/教师ID
     */
    private Long memberId;

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
     * 关联Classin机构ID
     */
    private Long classinInstitutionId;

    /**
     * 状态（1：启用；2：禁用）
     */
    private Integer status;
}