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

package top.continew.admin.education.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import top.continew.starter.core.enums.BaseEnum;

/**
 * 教材分类枚举
 *
 * @author continew-org
 * @since 2024-12-29
 */
@Getter
@RequiredArgsConstructor
public enum MaterialCategoryEnum implements BaseEnum<String> {

    /**
     * 少儿启蒙
     */
    CHILDREN("CHILDREN", "少儿启蒙"),

    /**
     * 青少年
     */
    TEENAGER("TEENAGER", "青少年"),

    /**
     * 成人教材
     */
    ADULT("ADULT", "成人教材"),

    /**
     * 综合教材
     */
    COMPREHENSIVE("COMPREHENSIVE", "综合教材"),

    /**
     * 阅读绘本
     */
    READING("READING", "阅读绘本"),

    /**
     * 自然拼读
     */
    PHONICS("PHONICS", "自然拼读"),

    /**
     * 考试教材
     */
    EXAM("EXAM", "考试教材"),

    /**
     * 语法
     */
    GRAMMAR("GRAMMAR", "语法");

    private final String value;
    private final String description;
}
