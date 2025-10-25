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

package top.continew.admin.education.model.req.classin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ClassIn 新增课程请求
 *
 * @author KAI
 * @since 2024/07/04 17:00
 */
@Data
public class ClassinCourseAddReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 课程名称
     */
    @JsonProperty("courseName")
    private String courseName;

    /**
     * 班主任 UID
     */
    @JsonProperty("mainTeacherUid")
    private String mainTeacherUid;

    /**
     * 唯一标识
     */
    @JsonProperty("courseUniqueIdentity")
    private String courseUniqueIdentity;

    /**
     * 教室设置ID
     */
    @JsonProperty("classroomSettingId")
    private Long classroomSettingId;
}