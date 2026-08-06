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

package top.continew.admin.education.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 学生信息更新请求
 *
 * @author don
 * @since 2025/06/14
 */
@Data
@Schema(description = "学生信息更新请求")
public class StudentUpdateReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "学生姓名（昵称）", example = "张三")
    @Size(max = 50, message = "昵称长度不能超过 {max} 个字符")
    private String nickname;

    @Schema(description = "头像地址", example = "https://example.com/avatar.jpg")
    private String avatar;
}
