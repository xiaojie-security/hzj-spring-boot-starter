package com.hzj.wechat.properties;

import lombok.Data;

/**
 * 微信商户基础配置属性。
 */
@Data
public class WechatMerchantProperties {

    /** 微信支付商户号。 */
    private String mchid;

    /** 微信应用唯一标识。 */
    private String appid;

    /** 微信应用密钥。 */
    private String appSecret;

    /** 商户 API 证书私钥文件路径。 */
    private String privateKeyPath;

    /** 商户 API 证书序列号。 */
    private String certificateSerialNo;

    /** 微信支付公钥文件路径。 */
    private String wechatPayPublicKeyPath;

    /** 微信支付公钥 ID。 */
    private String wechatPayPublicKeyId;

    /** 微信支付 APIv3 密钥。 */
    private String apiV3Secret;

    /** 微信支付异步通知地址。 */
    private String paymentNotifyUrl;
}
