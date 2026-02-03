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
 * 教材课节实体
 *
 * @author continew-org
 * @since 2024-12-29
 */
@Data
@TableName("edu_material_lesson")
public class MaterialLessonDO extends BaseDO {

    /**
     * 教材ID
     */
    private Long materialId;

    /**
     * 教材名称（冗余字段，格式：name + level）
     */
    private String materialName;

    /**
     * 课节名字
     */
    private String lessonName;

    /**
     * 课节链接
     */
    private String lessonUrl;

    /**
     * 状态（1:启用 0:禁用）
     */
    private Boolean status;
}
