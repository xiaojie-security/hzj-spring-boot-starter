package com.hzj.amap.core.webapi;

/**
 * 高德 Web 服务 API 调用异常。
 */
public class AMapWebApiException extends RuntimeException {

    /**
     * HTTP 状态码。
     */
    private final Integer httpStatus;

    /**
     * 高德业务响应码。
     */
    private final String infoCode;

    /**
     * 创建高德 Web 服务 API 调用异常。
     *
     * @param message 异常信息
     * @param httpStatus HTTP 状态码
     * @param infoCode 高德业务响应码
     */
    public AMapWebApiException(String message, Integer httpStatus, String infoCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.infoCode = infoCode;
    }

    /**
     * 获取 HTTP 状态码。
     *
     * @return HTTP 状态码
     */
    public Integer getHttpStatus() {
        return httpStatus;
    }

    /**
     * 获取高德业务响应码。
     *
     * @return 高德业务响应码
     */
    public String getInfoCode() {
        return infoCode;
    }
}
