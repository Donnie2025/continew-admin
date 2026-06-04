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
 * 教材统一树形表实体（CATEGORY/BOOK/LEVEL/UNIT/LESSON）
 *
 * @author continew-org
 * @since 2024-12-29
 */
@Data
@TableName("edu_material")
public class MaterialDO extends BaseDO {

    /**
     * 父节点ID（0=根节点）
     */
    private Long pid;

    /**
     * 节点类型（CATEGORY/BOOK/LEVEL/UNIT/LESSON）
     */
    private String type;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 编码（BOOK/LEVEL层使用）
     */
    private String code;

    /**
     * 封面图片（BOOK层使用）
     */
    private String coverImg;

    /**
     * 描述
     */
    private String description;

    /**
     * 课节资源链接（LESSON层使用）
     */
    private String lessonUrl;

    /**
     * ClassIn云盘ID（文件夹节点→文件夹ID，LESSON→文件ID）
     */
    private String cloudId;

    /**
     * ClassIn云盘名称
     */
    private String cloudName;

    /**
     * 飞书云空间文件夹token（文件夹节点使用，LESSON节点为NULL）
     */
    private String feishuFolderToken;

    /**
     * 是否前端展示（1:展示 0:不展示）
     */
    private Boolean isShow;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（1:启用 0:禁用）
     */
    private Boolean status;
}
