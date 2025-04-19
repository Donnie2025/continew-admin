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

import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;
import java.time.*;

/**
 * Classin用户信息
 *
 * @author donnie
 * @since 2025/04/12 20:49
 */
@Data
@Schema(description = "Classin用户信息")
public class ClassinUserResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickname;

    /**
     * 成员类型（0：不是成员；1：学生；2：老师）
     */
    @Schema(description = "成员类型（0：不是成员；1：学生；2：老师）")
    private Integer userType;

    /**
     * Classin唯一映射关系
     */
    @Schema(description = "Classin唯一映射关系")
    private String uid;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String telephone;

    /**
     * 关联学生ID
     */
    @Schema(description = "关联学生ID")
    private Long studentId;

    /**
     * 关联教师ID
     */
    @Schema(description = "关联教师ID")
    private Long teacherId;
}