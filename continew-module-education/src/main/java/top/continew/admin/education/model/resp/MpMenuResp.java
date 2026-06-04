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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 微信公众号菜单响应（来自微信 API）
 *
 * @author don
 * @since 2025/05/02
 */
@Data
@Schema(description = "微信公众号菜单响应")
public class MpMenuResp {

    /**
     * 普通菜单
     */
    @Schema(description = "普通菜单")
    private Menu menu;

    @Data
    @Schema(description = "菜单")
    public static class Menu {

        @Schema(description = "菜单按钮列表")
        private List<Button> button;

        @Schema(description = "菜单 id")
        private Long menuid;
    }

    @Data
    @Schema(description = "菜单按钮")
    public static class Button {

        @Schema(description = "按钮类型")
        private String type;

        @Schema(description = "按钮名称")
        private String name;

        @Schema(description = "事件 KEY（click 类型）")
        private String key;

        @Schema(description = "跳转链接（view/miniprogram 类型）")
        private String url;

        @Schema(description = "小程序 appid")
        private String appid;

        @Schema(description = "小程序页面路径")
        private String pagepath;

        @Schema(description = "素材 id")
        @JsonProperty("media_id")
        private String mediaId;

        @Schema(description = "子菜单")
        @JsonProperty("sub_button")
        private List<Button> subButton;
    }
}
