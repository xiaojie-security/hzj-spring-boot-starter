package com.hzj.wechat.properties;

import com.hzj.wechat.core.mobile.enums.WechatOpenSdkSignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信 OpenSDK 分享配置属性。
 */
@Data
@ConfigurationProperties(prefix = "wechat.mobile.share")
public class WechatOpenSdkShareProperties {

    /** 是否启用微信 OpenSDK 分享能力。 */
    private boolean enable = true;

    /** 微信开放平台移动应用 AppID。 */
    private String appid;

    /** OpenSDK 分享签名算法。 */
    private WechatOpenSdkSignatureAlgorithm signatureAlgorithm = WechatOpenSdkSignatureAlgorithm.RSA_WITH_SHA256;

    /** OpenSDK 分享签名私钥文件路径。 */
    private String privateKeyPath;
}
