package com.hzj.wechat.core.payment.service;

/**
 * 微信虚拟支付接口调用异常。
 */
public class WechatVirtualPaymentException extends RuntimeException {

    /**
     * 微信虚拟支付业务错误码。
     */
    private final Integer errcode;

    /**
     * HTTP 状态码。
     */
    private final Integer httpStatus;

    /**
     * 创建虚拟支付接口异常。
     *
     * @param message 异常信息
     */
    public WechatVirtualPaymentException(String message) {
        this(message, null, null, null);
    }

    /**
     * 创建虚拟支付接口异常。
     *
     * @param message 异常信息
     * @param cause 原始异常
     */
    public WechatVirtualPaymentException(String message, Throwable cause) {
        this(message, cause, null, null);
    }

    /**
     * 创建虚拟支付接口异常。
     *
     * @param message 异常信息
     * @param errcode 微信错误码
     * @param httpStatus HTTP 状态码
     */
    public WechatVirtualPaymentException(String message, Integer errcode, Integer httpStatus) {
        this(message, null, errcode, httpStatus);
    }

    private WechatVirtualPaymentException(String message, Throwable cause, Integer errcode, Integer httpStatus) {
        super(message, cause);
        this.errcode = errcode;
        this.httpStatus = httpStatus;
    }

    /**
     * 获取微信错误码。
     *
     * @return 微信错误码
     */
    public Integer getErrcode() {
        return errcode;
    }

    /**
     * 获取 HTTP 状态码。
     *
     * @return HTTP 状态码
     */
    public Integer getHttpStatus() {
        return httpStatus;
    }
}
