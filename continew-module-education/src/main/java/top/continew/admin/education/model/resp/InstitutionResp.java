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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.continew.admin.common.model.resp.BaseResp;

import java.io.Serial;

/**
 * 机构信息
 *
 * @author don
 * @since 2025/11/07
 */
@Data
@Schema(description = "机构信息")
public class InstitutionResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 机构编码
     */
    @Schema(description = "机构编码")
    private String code;

    /**
     * 机构名称
     */
    @Schema(description = "机构名称")
    private String name;

    /**
     * 机构SID（ClassIn AppId）
     */
    @Schema(description = "机构SID（ClassIn AppId）")
    private String sid;

    /**
     * 机构SECRET（ClassIn AppSecret）
     */
    @Schema(description = "机构SECRET（ClassIn AppSecret）")
    private String secret;

    /**
     * 是否激活（0：否；1：是）
     */
    @Schema(description = "是否激活（0：否；1：是）")
    private Integer isActive;
}
