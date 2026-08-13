package com.hzj.amap.core.webapi.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 高德行政区域查询响应。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AMapDistrictResponse extends AMapWebApiResponse {

    /** 行政区域数量。 */
    private Integer count;
    /** 行政区域列表。 */
    private List<AMapDistrict> districts;

    /**
     * 行政区域信息。
     */
    @Data
    public static class AMapDistrict {
        /** 行政区名称。 */
        private String name;
        /** 行政区中心点。 */
        private String center;
        /** 行政区等级。 */
        private String level;
        /** 城市编码。 */
        private String citycode;
        /** 行政区编码。 */
        private String adcode;
        /** 行政区边界。 */
        private String polyline;
        /** 下级行政区。 */
        private List<AMapDistrict> districts;
    }
}
