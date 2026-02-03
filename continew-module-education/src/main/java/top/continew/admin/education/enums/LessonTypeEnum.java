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
 * 课节类型枚举
 *
 * @author continew-org
 * @since 2024-12-29
 */
@Getter
@RequiredArgsConstructor
public enum LessonTypeEnum implements BaseEnum<String> {

    /**
     * 课节
     */
    LESSON("LESSON", "课节"),

    /**
     * 测试
     */
    TEST("TEST", "测试"),

    /**
     * 复习
     */
    REVIEW("REVIEW", "复习");

    private final String value;
    private final String description;
}
