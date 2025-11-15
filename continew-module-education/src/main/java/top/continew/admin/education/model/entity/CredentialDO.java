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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户凭证实体
 *
 * @author don
 * @since 2025/11/14
 */
@Data
@TableName("edu_credential")
public class CredentialDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（关联edu_teacher.id或edu_student.id）
     */
    private Long userId;

    /**
     * 用户类型（teacher-教师, student-学生）
     */
    private String userType;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 登录密码（BCrypt加密）
     */
    private String password;

    /**
     * 凭证类型（phone，email等）
     */
    private String credentialType;

    /**
     * 是否启用（0-禁用 1-启用）
     */
    private Boolean isActive;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 密码最后更新时间
     */
    private LocalDateTime passwordUpdatedTime;

    /**
     * 密码错误次数
     */
    private Integer errorCount;

    /**
     * 最后错误时间
     */
    private LocalDateTime lastErrorTime;

    /**
     * 冻结到期时间
     */
    private LocalDateTime freezeUntil;

    /**
     * 是否冻结（0-否 1-是）
     */
    private Boolean isFrozen;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;
}
