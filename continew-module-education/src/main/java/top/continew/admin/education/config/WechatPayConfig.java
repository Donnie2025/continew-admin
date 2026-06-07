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

package top.continew.admin.education.config;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAPublicKeyConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;

/**
 * 微信支付配置
 *
 * @author don
 * @since 2026/06/05
 */
@Configuration
@ConfigurationProperties(prefix = "wechat.pay")
@Data
public class WechatPayConfig {

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户API密钥（APIv3密钥）
     */
    private String apiV3Key;

    /**
     * 商户证书序列号
     */
    private String merchantSerialNumber;

    /**
     * 商户私钥路径
     */
    private String privateKeyPath;

    /**
     * 微信支付公钥路径
     */
    private String publicKeyPath;

    /**
     * 微信支付公钥ID
     */
    private String publicKeyId;

    /**
     * 支付回调通知地址
     */
    private String notifyUrl;

    /**
     * 创建RSA公钥配置
     */
    @Bean
    public Config rsaAutoCertificateConfig() throws FileNotFoundException {
        String privateKeyRealPath = privateKeyPath;
        if (privateKeyPath.startsWith("classpath:")) {
            privateKeyRealPath = ResourceUtils.getURL(privateKeyPath).getPath();
        }
        String publicKeyRealPath = publicKeyPath;
        if (publicKeyPath.startsWith("classpath:")) {
            publicKeyRealPath = ResourceUtils.getURL(publicKeyPath).getPath();
        }
        return new RSAPublicKeyConfig.Builder().merchantId(mchId)
            .privateKeyFromPath(privateKeyRealPath)
            .merchantSerialNumber(merchantSerialNumber)
            .publicKeyFromPath(publicKeyRealPath)
            .publicKeyId(publicKeyId)
            .apiV3Key(apiV3Key)
            .build();
    }

    /**
     * 创建JSAPI支付服务
     */
    @Bean
    public JsapiServiceExtension jsapiService(Config config) {
        return new JsapiServiceExtension.Builder().config(config).build();
    }
}
