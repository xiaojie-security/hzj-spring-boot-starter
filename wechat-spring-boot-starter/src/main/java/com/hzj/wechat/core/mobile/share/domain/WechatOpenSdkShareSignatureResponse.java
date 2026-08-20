package com.hzj.wechat.core.mobile.share.domain;

import com.hzj.wechat.core.mobile.share.enums.WechatOpenSdkSignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 微信 OpenSDK 分享签名响应。
 */
@Data
@AllArgsConstructor
public class WechatOpenSdkShareSignatureResponse {

    /** 当前微信开放平台应用 ID。 */
    private String appid;

    /** 客户端应写入 WXMediaMessage.msgSignature 的签名值。 */
    private String msgSignature;

    /** 生成签名使用的算法。 */
    private WechatOpenSdkSignatureAlgorithm signatureAlgorithm;
}
