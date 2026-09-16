package com.hzj.wechat.provider.wechat.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 微信支付启动期静态配置。
 *
 * <p>商户身份、证书和 API 密钥用于签名及回调解密，应用启动后不支持动态刷新。</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WechatPaymentStaticConfig {

    /** 微信支付商户号。 */
    private String mchid;

    /** 微信应用唯一标识。 */
    private String appid;

    /** 微信应用密钥。 */
    private String appSecret;

    /** 商户 API 证书私钥内容。 */
    private PrivateKey privateKey;

    /** 商户 API 证书序列号。 */
    private String certificateSerialNo;

    /** 微信支付公钥内容。 */
    private PublicKey wechatPayPublicKey;

    /** 微信支付公钥 ID。 */
    private String wechatPayPublicKeyId;

    /** 微信支付 APIv3 密钥。 */
    private String apiV3Secret;
}
