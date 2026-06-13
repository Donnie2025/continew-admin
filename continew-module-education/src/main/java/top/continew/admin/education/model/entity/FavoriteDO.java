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

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.continew.admin.common.model.entity.BaseDO;

import java.io.Serial;

/**
 * 收藏实体
 *
 * @author donnie
 * @since 2025/06/10
 */
@Data
@TableName("edu_favorite")
public class FavoriteDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 资源类型（teacher-教师, material-教材）
     */
    private String resourceType;

    /**
     * 资源ID（教师ID或教材ID）
     */
    private Long resourceId;

    /**
     * 资源名称（教师姓名或教材名称）
     */
    private String resourceName;

    /**
     * 状态（1：有效；0：已取消）
     */
    private Integer status;
}
