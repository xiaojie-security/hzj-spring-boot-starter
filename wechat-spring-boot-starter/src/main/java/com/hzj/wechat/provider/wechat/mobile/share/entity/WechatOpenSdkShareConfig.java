package com.hzj.wechat.provider.wechat.mobile.share.entity;

import com.hzj.wechat.core.mobile.enums.WechatOpenSdkSignatureAlgorithm;
import lombok.Data;

import java.security.PrivateKey;

/**
 * 微信 OpenSDK 分享动态配置。
 */
@Data
public class WechatOpenSdkShareConfig {

    /** 微信开放平台移动应用 AppID。 */
    private String appid;

    /** OpenSDK 分享签名算法。 */
    private WechatOpenSdkSignatureAlgorithm signatureAlgorithm = WechatOpenSdkSignatureAlgorithm.RSA_WITH_SHA256;

    /** OpenSDK 分享签名私钥。 */
    private PrivateKey privateKey;
}
