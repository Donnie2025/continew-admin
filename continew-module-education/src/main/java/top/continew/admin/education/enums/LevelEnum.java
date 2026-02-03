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
 * 级别枚举
 *
 * @author continew-org
 * @since 2024-12-29
 */
@Getter
@RequiredArgsConstructor
public enum LevelEnum implements BaseEnum<String> {

    /**
     * 幼儿园小班
     */
    K1("K1", "幼儿园小班"),

    /**
     * 幼儿园中班
     */
    K2("K2", "幼儿园中班"),

    /**
     * 幼儿园大班
     */
    K3("K3", "幼儿园大班"),

    /**
     * 一年级
     */
    G1("G1", "一年级"),

    /**
     * 二年级
     */
    G2("G2", "二年级"),

    /**
     * 三年级
     */
    G3("G3", "三年级"),

    /**
     * 四年级
     */
    G4("G4", "四年级"),

    /**
     * 五年级
     */
    G5("G5", "五年级"),

    /**
     * 六年级
     */
    G6("G6", "六年级"),

    /**
     * 七年级
     */
    G7("G7", "七年级"),

    /**
     * 八年级
     */
    G8("G8", "八年级"),

    /**
     * 九年级
     */
    G9("G9", "九年级"),

    /**
     * 十年级
     */
    G10("G10", "十年级"),

    /**
     * 十一年级
     */
    G11("G11", "十一年级"),

    /**
     * 十二年级
     */
    G12("G12", "十二年级"),

    /**
     * 成人
     */
    ADULT("ADULT", "成人");

    private final String value;
    private final String description;
}
