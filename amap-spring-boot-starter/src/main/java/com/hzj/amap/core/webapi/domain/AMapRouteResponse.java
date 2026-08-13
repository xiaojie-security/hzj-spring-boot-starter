package com.hzj.amap.core.webapi.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 高德路径规划 2.0 响应。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AMapRouteResponse extends AMapWebApiResponse {

    /** 高德路径规划业务状态码。 */
    private Integer errcode;

    /** 高德路径规划业务说明。 */
    private String errmsg;

    /** 路径规划结果。 */
    private AMapRoute route;

    /**
     * 路径规划信息。
     */
    @Data
    public static class AMapRoute {
        /** 起点坐标。 */
        private String origin;
        /** 终点坐标。 */
        private String destination;
        /** 路径方案。 */
        private List<AMapPath> paths;
        /** 公交路径方案。 */
        private List<AMapTransit> transits;
    }

    /**
     * 单条路径方案。
     */
    @Data
    public static class AMapPath {
        /** 距离，单位米。 */
        private String distance;
        /** 预计耗时，单位秒。 */
        private String duration;
        /** 收费金额。 */
        private String tolls;
        /** 收费路段距离。 */
        private String tollDistance;
        /** 限行尾号。 */
        private String restriction;
        /** 路径坐标串。 */
        private String polyline;
        /** 路段列表。 */
        private List<AMapStep> steps;
    }

    /**
     * 路段信息。
     */
    @Data
    public static class AMapStep {
        /** 行驶指示。 */
        private String instruction;
        /** 道路名称。 */
        private String road;
        /** 行驶方向。 */
        private String orientation;
        /** 路段距离。 */
        private String distance;
        /** 路段耗时。 */
        private String duration;
        /** 路段坐标串。 */
        private String polyline;
        /** 行驶动作。 */
        private String action;
        /** 辅助动作。 */
        private String assistantAction;
    }

    /**
     * 公交路径方案。
     */
    @Data
    public static class AMapTransit {
        /** 总距离，单位米。 */
        private String distance;
        /** 总耗时，单位秒。 */
        private String duration;
        /** 总步行距离，单位米。 */
        private String walkingDistance;
        /** 总票价。 */
        private String cost;
        /** 分段方案。 */
        private List<AMapTransitSegment> segments;
    }

    /**
     * 公交路径分段。
     */
    @Data
    public static class AMapTransitSegment {
        /** 步行信息。 */
        private AMapPath walking;
        /** 公交车或地铁信息。 */
        private AMapBus bus;
        /** 出租车信息。 */
        private AMapTaxi taxi;
    }

    /**
     * 公交车或地铁信息。
     */
    @Data
    public static class AMapBus {
        /** 公交线路。 */
        private List<AMapBusLine> buslines;
    }

    /**
     * 公交线路信息。
     */
    @Data
    public static class AMapBusLine {
        /** 线路名称。 */
        private String name;
        /** 起始站。 */
        private AMapBusStop departureStop;
        /** 到达站。 */
        private AMapBusStop arrivalStop;
        /** 线路距离。 */
        private String distance;
        /** 预计耗时。 */
        private String duration;
    }

    /**
     * 公交站点信息。
     */
    @Data
    public static class AMapBusStop {
        /** 站点名称。 */
        private String name;
        /** 站点坐标。 */
        private String location;
    }

    /**
     * 出租车信息。
     */
    @Data
    public static class AMapTaxi {
        /** 出租车距离。 */
        private String distance;
        /** 出租车耗时。 */
        private String duration;
        /** 出租车费用。 */
        private String cost;
    }
}
