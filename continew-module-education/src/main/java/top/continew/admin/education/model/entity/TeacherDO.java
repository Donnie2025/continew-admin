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

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 教师实体
 *
 * @author donnie
 * @since 2025/04/04 18:33
 */
@Data
@TableName("edu_teacher")
public class TeacherDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教师姓名
     */
    private String name;

    /**
     * 评分
     */
    private Integer score;

    /**
     * 标签
     */
    private String tags;

    /**
     * 性别（male-男 female-女）
     */
    private String gender;

    /**
     * 是否展示
     */
    private Integer isShow;

    /**
     * 是否固定
     */
    private Integer isFixed;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 音频地址
     */
    private String audioUrl;

    /**
     * 视频地址
     */
    private String videoUrl;

    /**
     * 简介
     */
    private String briefIntro;

    /**
     * 描述
     */
    private String description;

    /**
     * 所属组
     */
    private String groupName;

    /**
     * 是否在教师端展示工资（0：不展示；1：展示）
     */
    private Integer showSalary;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 单价
     */
    private Integer rate;

    /**
     * 收款人姓名
     */
    private String recvName;
}