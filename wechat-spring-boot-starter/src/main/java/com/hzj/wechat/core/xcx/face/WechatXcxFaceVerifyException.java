package com.hzj.wechat.core.xcx.face;

/**
 * 微信人脸核身接口调用异常。
 */
public class WechatXcxFaceVerifyException extends RuntimeException {

    /**
     * 微信错误码。
     */
    private final Integer errcode;

    /**
     * HTTP 状态码。
     */
    private final Integer httpStatus;

    /**
     * 创建人脸核身接口异常。
     *
     * @param message 异常信息
     */
    public WechatXcxFaceVerifyException(String message) {
        this(message, null, null, null);
    }

    /**
     * 创建人脸核身接口异常。
     *
     * @param message 异常信息
     * @param cause   原始异常
     */
    public WechatXcxFaceVerifyException(String message, Throwable cause) {
        this(message, cause, null, null);
    }

    /**
     * 创建人脸核身接口异常。
     *
     * @param message    异常信息
     * @param errcode    微信错误码
     * @param httpStatus HTTP 状态码
     */
    public WechatXcxFaceVerifyException(String message, Integer errcode, Integer httpStatus) {
        this(message, null, errcode, httpStatus);
    }

    private WechatXcxFaceVerifyException(String message, Throwable cause, Integer errcode, Integer httpStatus) {
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
