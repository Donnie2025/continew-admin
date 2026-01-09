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
 * 教材详情信息
 *
 * @author don
 * @since 2025/12/29 21:22
 */
@Data
@ExcelIgnoreUnannotated
@Schema(description = "教材详情信息")
public class MaterialDetailResp extends BaseDetailResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 教材编码
     */
    @Schema(description = "教材编码")
    @ExcelProperty(value = "教材编码")
    private String code;

    /**
     * 教材名字
     */
    @Schema(description = "教材名字")
    @ExcelProperty(value = "教材名字")
    private String name;

    /**
     * 级别（K1:幼儿园小班 K2:幼儿园中班 K3:幼儿园大班 G1-G12:1-12年级 ADULT:成人）
     */
    @Schema(description = "级别（K1:幼儿园小班 K2:幼儿园中班 K3:幼儿园大班 G1-G12:1-12年级 ADULT:成人）")
    @ExcelProperty(value = "级别（K1:幼儿园小班 K2:幼儿园中班 K3:幼儿园大班 G1-G12:1-12年级 ADULT:成人）")
    private String level;

    /**
     * 分类（CHILDREN:少儿启蒙 TEENAGER:青少年 ADULT:成人教材 COMPREHENSIVE:综合教材 READING:阅读绘本 PHONICS:自然拼读 EXAM:考试教材 GRAMMAR:语法）
     */
    @Schema(description = "分类（CHILDREN:少儿启蒙 TEENAGER:青少年 ADULT:成人教材 COMPREHENSIVE:综合教材 READING:阅读绘本 PHONICS:自然拼读 EXAM:考试教材 GRAMMAR:语法）")
    @ExcelProperty(value = "分类（CHILDREN:少儿启蒙 TEENAGER:青少年 ADULT:成人教材 COMPREHENSIVE:综合教材 READING:阅读绘本 PHONICS:自然拼读 EXAM:考试教材 GRAMMAR:语法）")
    private String category;

    /**
     * 封面图片
     */
    @Schema(description = "封面图片")
    @ExcelProperty(value = "封面图片")
    private String coverImg;

    /**
     * 教材描述
     */
    @Schema(description = "教材描述")
    @ExcelProperty(value = "教材描述")
    private String desc;

    /**
     * 是否前端展示（1:展示 0:不展示）
     */
    @Schema(description = "是否前端展示（1:展示 0:不展示）")
    @ExcelProperty(value = "是否前端展示（1:展示 0:不展示）")
    private Boolean isShow;

    /**
     * 排序
     */
    @Schema(description = "排序")
    @ExcelProperty(value = "排序")
    private Integer sort;

    /**
     * 状态（1:启用 0:禁用）
     */
    @Schema(description = "状态（1:启用 0:禁用）")
    @ExcelProperty(value = "状态（1:启用 0:禁用）")
    private Integer status;
}