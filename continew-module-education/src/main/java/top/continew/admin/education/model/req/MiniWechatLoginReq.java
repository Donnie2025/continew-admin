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
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 小程序微信登录请求参数
 *
 * @author don
 * @since 2025/11/19
 */
@Data
@Schema(description = "小程序微信登录请求参数")
public class MiniWechatLoginReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 微信临时登录凭证code
     */
    @Schema(description = "微信临时登录凭证code", example = "081Kgf0w3jNsYX2gXb2w3d3W4X4Kgf0X")
    @NotBlank(message = "微信登录凭证不能为空")
    private String code;

    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private UserInfo userInfo;

    /**
     * 手机号信息
     */
    @Schema(description = "手机号信息")
    private PhoneInfo phoneInfo;

    /**
     * 用户信息内部类
     */
    @Data
    @Schema(description = "用户信息")
    public static class UserInfo {

        /**
         * 用户昵称
         */
        @Schema(description = "用户昵称", example = "微信用户")
        private String nickName;

        /**
         * 用户头像
         */
        @Schema(description = "用户头像", example = "https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLL1byctY955FriaEx5wKCkzEUlLVLMkxlk/132")
        private String avatarUrl;

        /**
         * 用户性别
         */
        @Schema(description = "用户性别", example = "1")
        private Integer gender;

        /**
         * 用户所在国家
         */
        @Schema(description = "用户所在国家", example = "China")
        private String country;

        /**
         * 用户所在省份
         */
        @Schema(description = "用户所在省份", example = "Guangdong")
        private String province;

        /**
         * 用户所在城市
         */
        @Schema(description = "用户所在城市", example = "Guangzhou")
        private String city;
    }

    /**
     * 手机号信息内部类
     */
    @Data
    @Schema(description = "手机号信息")
    public static class PhoneInfo {

        /**
         * 加密数据
         */
        @Schema(description = "加密数据")
        private String encryptedData;

        /**
         * 初始向量
         */
        @Schema(description = "初始向量")
        private String iv;
    }
}
