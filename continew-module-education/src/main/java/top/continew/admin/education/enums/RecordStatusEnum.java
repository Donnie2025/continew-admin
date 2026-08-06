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
 * 记录状态枚举（教育模块通用）
 *
 * @author don
 * @since 2025/06/21
 */
@Getter
@RequiredArgsConstructor
public enum RecordStatusEnum implements BaseEnum<Integer> {

    /**
     * 禁用/已删除
     */
    DISABLED(0, "禁用"),

    /**
     * 启用/正常
     */
    ENABLED(1, "启用"),

    /**
     * 已结课/已完成
     */
    FINISHED(3, "已结课");

    private final Integer value;
    private final String description;
}
