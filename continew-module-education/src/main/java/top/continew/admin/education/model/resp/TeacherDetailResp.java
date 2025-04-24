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

package top.continew.admin.education.model.resp;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import top.continew.admin.common.model.resp.BaseDetailResp;

import java.io.Serial;
import java.time.*;

/**
 * 教师详情信息
 *
 * @author donnie
 * @since 2025/04/04 18:33
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "教师详情信息")
public class TeacherDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教师姓名
     */
    @Schema(description = "教师姓名")
    @ExcelProperty(value = "教师姓名")
    private String name;

    /**
     * 评分
     */
    @Schema(description = "评分")
    @ExcelProperty(value = "评分")
    private Integer score;

    /**
     * 单价
     */
    @Schema(description = "单价")
    @ExcelProperty(value = "单价")
    private Integer rate;

    /**
     * 标签
     */
    @Schema(description = "标签")
    @ExcelProperty(value = "标签")
    private String tags;

    /**
     * 性别（male-男 female-女）
     */
    @Schema(description = "性别（male-男 female-女）")
    @ExcelProperty(value = "性别（male-男 female-女）")
    private String gender;

    /**
     * 是否展示
     */
    @Schema(description = "是否展示")
    @ExcelProperty(value = "是否展示")
    private Integer isShow;

    /**
     * 是否固定
     */
    @Schema(description = "是否固定")
    @ExcelProperty(value = "是否固定")
    private Integer isFixed;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    @ExcelProperty(value = "手机号码")
    private String phone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    @ExcelProperty(value = "邮箱")
    private String email;

    /**
     * 头像地址
     */
    @Schema(description = "头像地址")
    @ExcelProperty(value = "头像地址")
    private String avatar;

    /**
     * 音频地址
     */
    @Schema(description = "音频地址")
    @ExcelProperty(value = "音频地址")
    private String audioUrl;

    /**
     * 视频地址
     */
    @Schema(description = "视频地址")
    @ExcelProperty(value = "视频地址")
    private String videoUrl;

    /**
     * 简介
     */
    @Schema(description = "简介")
    @ExcelProperty(value = "简介")
    private String briefIntro;

    /**
     * 描述
     */
    @Schema(description = "描述")
    @ExcelProperty(value = "描述")
    private String description;

    /**
     * 排序
     */
    @Schema(description = "排序")
    @ExcelProperty(value = "排序")
    private Integer sort;

    /**
     * 状态
     */
    @Schema(description = "状态")
    @ExcelProperty(value = "状态")
    private Integer status;
}