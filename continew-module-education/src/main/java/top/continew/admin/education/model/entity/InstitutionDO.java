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
 * 机构实体
 *
 * @author don
 * @since 2025/11/07
 */
@Data
@TableName("edu_institution")
public class InstitutionDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构编码
     */
    private String code;

    /**
     * 机构名称
     */
    private String name;

    /**
     * 机构SID（ClassIn AppId）
     */
    private String sid;

    /**
     * 机构SECRET（ClassIn AppSecret）
     */
    private String secret;

    /**
     * 是否激活（0：否；1：是），只能有一个机构处于激活状态
     */
    private Integer isActive;

    /**
     * 状态（1：启用；2：禁用）
     */
    private Integer status;
}
