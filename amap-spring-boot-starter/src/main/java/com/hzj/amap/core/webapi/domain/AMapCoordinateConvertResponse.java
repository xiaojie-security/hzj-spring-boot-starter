package com.hzj.amap.core.webapi.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 高德坐标转换响应。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AMapCoordinateConvertResponse extends AMapWebApiResponse {

    /** 转换后的坐标集合，多个坐标使用分号分隔。 */
    private String locations;
}
