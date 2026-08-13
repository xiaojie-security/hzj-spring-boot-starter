package com.hzj.amap.core.webapi.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 高德 IP 定位响应。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AMapIpLocationResponse extends AMapWebApiResponse {

    /** 省份名称。 */
    private String province;
    /** 城市名称。 */
    private String city;
    /** 城市编码。 */
    private String citycode;
    /** 行政区编码。 */
    private String adcode;
    /** 所属区域边界。 */
    private String rectangle;
}
