package com.hzj.amap.core.webapi.domain;

import lombok.Data;

/**
 * 高德 Web 服务 API 响应。
 */
@Data
public class AMapWebApiResponse {

    /**
     * HTTP 状态码。
     */
    private int httpStatus;

    /**
     * 高德业务状态，1 表示成功。
     */
    private String status;

    /**
     * 高德业务响应码。
     */
    private String infoCode;

    /**
     * 高德业务说明。
     */
    private String info;

}
