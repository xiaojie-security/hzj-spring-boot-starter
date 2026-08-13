package com.hzj.wechat.properties;

import com.hzj.wechat.core.qrcode.enums.WechatXcxEnvVersion;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信支付配置属性。
 */
@Data
@ConfigurationProperties(prefix = "wechat.payment")
public class WechatPaymentProperties {

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
    /** 商家转账异步通知地址。 */
    private String transferNotifyUrl;
    /** 免确认收款授权结果通知地址。 */
    private String authorizationNotifyUrl;
    /** 小程序二维码激活版本。 */
    private WechatXcxEnvVersion envVersion;
}
