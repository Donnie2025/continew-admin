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

/**
 * 用户类型枚举
 *
 * @author don
 * @since 2025/1/4
 */
@Getter
@RequiredArgsConstructor
public enum UserType {

    /**
     * 学生
     */
    STUDENT("student", "学生"),

    /**
     * 教师
     */
    TEACHER("teacher", "教师");

    /**
     * 类型值
     */
    private final String value;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据值获取用户类型枚举
     *
     * @param value 类型值
     * @return 用户类型枚举
     */
    public static UserType fromValue(String value) {
        for (UserType userType : values()) {
            if (userType.getValue().equals(value)) {
                return userType;
            }
        }
        throw new IllegalArgumentException("无效的用户类型: " + value);
    }

    /**
     * 验证用户类型是否有效
     *
     * @param value 类型值
     * @return 是否有效
     */
    public static boolean isValid(String value) {
        for (UserType userType : values()) {
            if (userType.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
