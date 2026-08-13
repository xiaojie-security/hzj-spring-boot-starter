package com.hzj.amap.core.webapi.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 高德逆地理编码响应。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AMapReverseGeoResponse extends AMapWebApiResponse {

    /** 逆地理编码结果。 */
    private AMapRegeocode regeocode;

    /**
     * 逆地理编码信息。
     */
    @Data
    public static class AMapRegeocode {
        /** 格式化地址。 */
        private String formattedAddress;
        /** 地址组成。 */
        private AMapAddressComponent addressComponent;
        /** 兴趣点列表。 */
        private List<AMapPoi> pois;
        /** 道路列表。 */
        private List<AMapRoad> roads;
        /** 道路交叉口列表。 */
        private List<AMapRoadIntersection> roadinters;
        /** 区域列表。 */
        private List<AMapAoi> aois;
    }

    /**
     * 地址组成信息。
     */
    @Data
    public static class AMapAddressComponent {
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
        /** 乡镇。 */
        private String township;
        /** 社区。 */
        private AMapNamedLocation neighborhood;
        /** 建筑物。 */
        private AMapNamedLocation building;
        /** 街道和门牌号。 */
        private AMapStreetNumber streetNumber;
        /** 业务区信息。 */
        private List<AMapBusinessArea> businessAreas;
    }

    /**
     * 具名位置。
     */
    @Data
    public static class AMapNamedLocation {
        /** 名称。 */
        private String name;
        /** 类型。 */
        private String type;
    }

    /**
     * 街道门牌信息。
     */
    @Data
    public static class AMapStreetNumber {
        /** 街道名称。 */
        private String street;
        /** 门牌号。 */
        private String number;
        /** 坐标。 */
        private String location;
        /** 距离。 */
        private String distance;
        /** 方向。 */
        private String direction;
    }

    /**
     * 兴趣点信息。
     */
    @Data
    public static class AMapPoi {
        /** 兴趣点标识。 */
        private String id;
        /** 兴趣点名称。 */
        private String name;
        /** 兴趣点类型。 */
        private String type;
        /** 地址。 */
        private String address;
        /** 坐标。 */
        private String location;
        /** 距离。 */
        private String distance;
        /** 方向。 */
        private String direction;
        /** 电话。 */
        private String tel;
    }

    /**
     * 道路信息。
     */
    @Data
    public static class AMapRoad {
        /** 道路标识。 */
        private String id;
        /** 道路名称。 */
        private String name;
        /** 距离。 */
        private String distance;
        /** 方向。 */
        private String direction;
        /** 坐标。 */
        private String location;
    }

    /**
     * 道路交叉口信息。
     */
    @Data
    public static class AMapRoadIntersection {
        /** 交叉口名称。 */
        private String firstId;
        /** 第二道路标识。 */
        private String secondId;
        /** 距离。 */
        private String distance;
        /** 方向。 */
        private String direction;
        /** 坐标。 */
        private String location;
    }

    /**
     * 区域信息。
     */
    @Data
    public static class AMapAoi {
        /** 区域标识。 */
        private String id;
        /** 区域名称。 */
        private String name;
        /** 区域类型。 */
        private String type;
        /** 区域中心坐标。 */
        private String location;
        /** 距离。 */
        private String distance;
        /** 面积。 */
        private String area;
    }

    /**
     * 商务区信息。
     */
    @Data
    public static class AMapBusinessArea {
        /** 商务区名称。 */
        private String name;
        /** 商务区标识。 */
        private String id;
        /** 商务区坐标。 */
        private String location;
    }
}
