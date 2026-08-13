package com.hzj.amap.core.webapi.domain;

import lombok.Builder;
import lombok.Data;

/**
 * 高德静态地图响应。
 */
@Data
@Builder
public class AMapStaticMapResponse {

    /**
     * HTTP 状态码。
     */
    private int httpStatus;

    /**
     * 响应内容类型。
     */
    private String contentType;

    /**
     * 地图图片字节。
     */
    private byte[] image;
}
