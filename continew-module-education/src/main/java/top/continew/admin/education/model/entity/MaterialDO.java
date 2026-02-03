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

/**
 * 教材主表实体
 *
 * @author continew-org
 * @since 2024-12-29
 */
@Data
@TableName("edu_material")
public class MaterialDO extends BaseDO {

    /**
     * 教材编码
     */
    private String code;

    /**
     * 教材名字
     */
    private String name;

    /**
     * 级别（K1:幼儿园小班 K2:幼儿园中班 K3:幼儿园大班 G1-G12:1-12年级 ADULT:成人）
     */
    private String level;

    /**
     * 分类（CHILDREN:少儿启蒙 TEENAGER:青少年 ADULT:成人教材 COMPREHENSIVE:综合教材 READING:阅读绘本 PHONICS:自然拼读 EXAM:考试教材 GRAMMAR:语法）
     */
    private String category;

    /**
     * 封面图片
     */
    private String coverImg;

    /**
     * 教材描述
     */
    private String description;

    /**
     * 是否前端展示（1:展示 0:不展示）
     */
    private Boolean isShow;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（1:启用 0:禁用）
     */
    private Boolean status;
}
