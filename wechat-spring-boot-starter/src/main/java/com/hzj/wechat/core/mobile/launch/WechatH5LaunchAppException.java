package com.hzj.wechat.core.mobile.launch;

/**
 * 微信 H5 拉起 App 异常。
 */
public class WechatH5LaunchAppException extends RuntimeException {

    /**
     * 创建微信 H5 拉起 App 异常。
     *
     * @param message 异常信息
     */
    public WechatH5LaunchAppException(String message) {
        super(message);
    }

    /**
     * 创建微信 H5 拉起 App 异常。
     *
     * @param message 异常信息
     * @param cause 原始异常
     */
    public WechatH5LaunchAppException(String message, Throwable cause) {
        super(message, cause);
    }
}
