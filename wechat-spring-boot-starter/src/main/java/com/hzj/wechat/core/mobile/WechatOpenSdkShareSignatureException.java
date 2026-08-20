package com.hzj.wechat.core.mobile;

/**
 * 微信 OpenSDK 分享签名异常。
 */
public class WechatOpenSdkShareSignatureException extends RuntimeException {

    /**
     * 创建分享签名异常。
     *
     * @param message 异常信息
     */
    public WechatOpenSdkShareSignatureException(String message) {
        super(message);
    }

    /**
     * 创建分享签名异常。
     *
     * @param message 异常信息
     * @param cause 原始异常
     */
    public WechatOpenSdkShareSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
