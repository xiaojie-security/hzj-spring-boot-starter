package com.hzj.kuaidi100.core.common;

/**
 * 快递100接口调用异常。
 */
public class Kuaidi100ApiException extends RuntimeException {

    /** HTTP 状态码。 */
    private final Integer httpStatus;

    /** 快递100返回的业务响应文本。 */
    private final String responseBody;

    /**
     * 创建接口调用异常。
     *
     * @param message 异常消息
     * @param httpStatus HTTP状态码
     * @param responseBody 响应文本
     */
    public Kuaidi100ApiException(String message, Integer httpStatus, String responseBody) {
        super(message);
        this.httpStatus = httpStatus;
        this.responseBody = responseBody;
    }

    /**
     * 获取 HTTP 状态码。
     *
     * @return HTTP状态码
     */
    public Integer getHttpStatus() {
        return httpStatus;
    }

    /**
     * 获取响应文本。
     *
     * @return 响应文本
     */
    public String getResponseBody() {
        return responseBody;
    }
}
