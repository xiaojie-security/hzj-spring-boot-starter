package com.hzj.wechat.core.ad;

/**
 * 微信广告数据接口调用异常。
 */
public class WechatAdDataException extends RuntimeException {

    /**
     * 微信广告接口业务错误码。
     */
    private final Integer ret;

    /**
     * HTTP 状态码。
     */
    private final Integer httpStatus;

    /**
     * 创建广告数据接口异常。
     *
     * @param message 异常信息
     */
    public WechatAdDataException(String message) {
        this(message, null, null, null);
    }

    /**
     * 创建广告数据接口异常。
     *
     * @param message 异常信息
     * @param cause 原始异常
     */
    public WechatAdDataException(String message, Throwable cause) {
        this(message, cause, null, null);
    }

    /**
     * 创建广告数据接口异常。
     *
     * @param message 异常信息
     * @param ret 微信广告接口业务错误码
     * @param httpStatus HTTP 状态码
     */
    public WechatAdDataException(String message, Integer ret, Integer httpStatus) {
        this(message, null, ret, httpStatus);
    }

    private WechatAdDataException(String message, Throwable cause, Integer ret, Integer httpStatus) {
        super(message, cause);
        this.ret = ret;
        this.httpStatus = httpStatus;
    }

    /**
     * 获取微信广告接口业务错误码。
     *
     * @return 业务错误码
     */
    public Integer getRet() {
        return ret;
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
