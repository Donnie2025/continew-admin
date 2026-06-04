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
 * 学生管理实体
 *
 * @author don
 * @since 2025/04/20 01:32
 */
@Data
@TableName("edu_student")
public class StudentDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 学生姓名
     */
    private String name;

    /**
     * 所属代理商编码
     */
    private String agentCode;

    /**
     * 性别（male-男 female-女）
     */
    private String gender;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 微信unionid
     */
    private String unionid;

    /**
     * 微信昵称
     */
    private String nickname;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否允许录课（0：不允许；1：允许）
     */
    private Integer enableRecording;

    /**
     * 状态（1：启用；2：禁用）
     */
    private Integer status;

    /**
     * 所属机构ID
     */
    private Long institutionId;
}
