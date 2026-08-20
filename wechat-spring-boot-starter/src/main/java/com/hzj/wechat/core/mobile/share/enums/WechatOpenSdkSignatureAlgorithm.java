package com.hzj.wechat.core.mobile.share.enums;

/**
 * 微信 OpenSDK 分享签名算法。
 */
public enum WechatOpenSdkSignatureAlgorithm {

    /** RSAwithSHA256，使用 SHA-256、MGF1 SHA-256、salt 长度为 32 的 RSA-PSS。 */
    RSA_WITH_SHA256,

    /** SM2withSM3。 */
    SM2_WITH_SM3
}
