package com.hzj.amap.core.webapi.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 高德地理编码响应。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AMapGeoResponse extends AMapWebApiResponse {

    /** 地理编码结果数量。 */
    private Integer count;

    /** 地理编码结果。 */
    private List<AMapGeocode> geocodes;

    /**
     * 地理编码信息。
     */
    @Data
    public static class AMapGeocode {
        /** 格式化地址。 */
        private String formattedAddress;
        /** 国家。 */
        private String country;
        /** 省份。 */
        private String province;
        /** 城市。 */
        private String city;
        /** 城市编码。 */
        private String citycode;
        /** 行政区。 */
        private String district;
        /** 行政区编码。 */
        private String adcode;
        /** 乡镇街道。 */
        private String township;
        /** 社区。 */
        private String neighborhood;
        /** 建筑物。 */
        private String building;
        /** 门牌号。 */
        private String number;
        /** 街道。 */
        private String street;
        /** 坐标，经度在前、纬度在后。 */
        private String location;
        /** 匹配等级。 */
        private String level;
    }
}
