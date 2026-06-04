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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 微信公众号菜单创建/更新请求
 *
 * @author don
 * @since 2025/05/02
 */
@Data
@Schema(description = "微信公众号菜单请求")
public class MpMenuReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 一级菜单列表（最多 3 个）
     */
    @Schema(description = "一级菜单列表，最多 3 个")
    @NotEmpty(message = "菜单不能为空")
    @Size(max = 3, message = "一级菜单最多 3 个")
    @Valid
    private List<Button> button;

    /**
     * 菜单按钮
     */
    @Data
    @Schema(description = "菜单按钮")
    public static class Button implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 按钮名称
         */
        @Schema(description = "按钮名称，一级菜单不超过4个中文/8个英文，子菜单不超过8个中文/16个英文")
        private String name;

        /**
         * 按钮类型（有子菜单时可不填）
         * click / view / miniprogram / scancode_push / pic_sysphoto 等
         */
        @Schema(description = "按钮类型：click/view/miniprogram/scancode_push/pic_sysphoto/pic_photo_or_album/location_select/media_id/article_id")
        private String type;

        /**
         * click 类型的事件 KEY
         */
        @Schema(description = "click 类型的事件 KEY")
        private String key;

        /**
         * view / miniprogram 类型的跳转链接
         */
        @Schema(description = "view/miniprogram 跳转链接")
        private String url;

        /**
         * miniprogram 类型的小程序 appid
         */
        @Schema(description = "小程序 appid（miniprogram 类型时必填）")
        private String appid;

        /**
         * miniprogram 类型的小程序页面路径
         */
        @Schema(description = "小程序页面路径（miniprogram 类型时必填）")
        private String pagepath;

        /**
         * media_id / article_id 类型的素材 id
         */
        @Schema(description = "素材 id（media_id/article_id 类型时必填）")
        private String mediaId;

        /**
         * 子菜单列表（最多 5 个，有子菜单时 type 可不填）
         */
        @Schema(description = "子菜单列表，最多 5 个")
        @Size(max = 5, message = "子菜单最多 5 个")
        @Valid
        private List<Button> subButton;
    }
}
