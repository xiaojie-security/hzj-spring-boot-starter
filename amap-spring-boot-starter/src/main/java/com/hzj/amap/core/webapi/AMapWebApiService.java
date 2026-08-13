package com.hzj.amap.core.webapi;

import com.hzj.amap.core.webapi.domain.*;

/**
 * 高德 Web 服务 API。
 */
public interface AMapWebApiService {

    /**
     * 查询地理编码。
     *
     * @param request 地理编码请求
     * @return 地理编码响应
     */
    AMapGeoResponse queryGeo(AMapGeoRequest request);

    /**
     * 查询逆地理编码。
     *
     * @param request 逆地理编码请求
     * @return 逆地理编码响应
     */
    AMapReverseGeoResponse queryReverseGeo(AMapReverseGeoRequest request);

    /**
     * 查询路径规划 2.0。
     *
     * @param request 路径规划请求
     * @return 路径规划响应
     */
    AMapRouteResponse queryRoute(AMapRouteRequest request);

    /**
     * 查询行政区域。
     *
     * @param request 行政区域请求
     * @return 行政区域响应
     */
    AMapDistrictResponse queryDistrict(AMapDistrictRequest request);

    /**
     * 查询 IP 定位。
     *
     * @param request IP 定位请求
     * @return IP 定位响应
     */
    AMapIpLocationResponse queryIpLocation(AMapIpLocationRequest request);

    /**
     * 查询静态地图。
     *
     * @param request 静态地图请求
     * @return 静态地图图片响应
     */
    AMapStaticMapResponse queryStaticMap(AMapStaticMapRequest request);

    /**
     * 转换坐标。
     *
     * @param request 坐标转换请求
     * @return 坐标转换响应
     */
    AMapCoordinateConvertResponse convertCoordinate(AMapCoordinateConvertRequest request);

    /**
     * 查询输入提示。
     *
     * @param request 输入提示请求
     * @return 输入提示响应
     */
    AMapInputTipsResponse queryInputTips(AMapInputTipsRequest request);

    /**
     * 查询天气。
     *
     * @param request 天气查询请求
     * @return 天气查询响应
     */
    AMapWeatherResponse queryWeather(AMapWeatherRequest request);
}
