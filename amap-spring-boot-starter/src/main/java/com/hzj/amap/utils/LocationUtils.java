package com.hzj.amap.utils;

import cn.hutool.core.util.StrUtil;

/**
 * 坐标相关工具类，处理经纬度。
 */
public final class LocationUtils {

    /**
     * 地球椭球半径。
     */
    private static final double EARTH_RADIUS = 6378245.0;

    /**
     * 偏移常量。
     */
    private static final double OFFSET = 0.00669342162296594323;

    /**
     * 反转经纬度。
     *
     * @param location 原始经纬度。
     * @return 反转后的经纬度。
     */
    public static String reversLatLon(String location) {
        return reversLatLon(location, ",");
    }

    /**
     * 反转经纬度。
     *
     * @param location 原始经纬度。
     * @param symbol 分隔符。
     * @return 反转后的经纬度。
     */
    public static String reversLatLon(String location, String symbol) {
        if (StrUtil.isEmpty(location) || StrUtil.isEmpty(symbol)) {
            return location;
        }
        String[] locationSplit = location.split(symbol);
        if (locationSplit.length != 2) {
            return location;
        }
        return locationSplit[1] + symbol + locationSplit[0];
    }

    /**
     * 数字经纬度返回字符串经纬度。
     *
     * @param lon 经度。
     * @param lat 纬度。
     * @return 字符串经纬度。
     */
    public static String getLocation(Double lon, Double lat) {
        if (lon == null || lat == null) {
            return null;
        }
        return String.format("%s,%s", lon, lat);
    }

    /**
     * 判断坐标是否位于中国大陆范围外。
     *
     * @param lon 经度。
     * @param lat 纬度。
     * @return 是否位于中国大陆范围外。
     */
    public static boolean isOutOfChina(double lon, double lat) {
        return lon < 72.004 || lon > 137.8347 || lat < 0.8293 || lat > 55.8271;
    }

    /**
     * 将 WGS84 坐标转换为 GCJ-02 坐标。
     *
     * @param lon WGS84 经度。
     * @param lat WGS84 纬度。
     * @return GCJ-02 坐标。
     */
    public static double[] wgs84ToGcj02(double lon, double lat) {
        if (isOutOfChina(lon, lat)) {
            return new double[]{lon, lat};
        }

        double deltaLat = transformLatitude(lon - 105.0, lat - 35.0);
        double deltaLon = transformLongitude(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - OFFSET * magic * magic;
        double sqrtMagic = Math.sqrt(magic);

        deltaLat = (deltaLat * 180.0) / (((EARTH_RADIUS * (1 - OFFSET)) / (magic * sqrtMagic)) * Math.PI);
        deltaLon = (deltaLon * 180.0) / ((EARTH_RADIUS / sqrtMagic) * Math.cos(radLat) * Math.PI);

        return new double[]{lon + deltaLon, lat + deltaLat};
    }

    /**
     * 计算纬度偏移量。
     *
     * @param lon 经度差值。
     * @param lat 纬度差值。
     * @return 纬度偏移量。
     */
    private static double transformLatitude(double lon, double lat) {
        double result = -100.0 + 2.0 * lon + 3.0 * lat + 0.2 * lat * lat;
        result += 0.1 * lon * lat + 0.2 * Math.sqrt(Math.abs(lon));
        result += ((20.0 * Math.sin(6.0 * lon * Math.PI) + 20.0 * Math.sin(2.0 * lon * Math.PI)) * 2.0) / 3.0;
        result += ((20.0 * Math.sin(lat * Math.PI) + 40.0 * Math.sin((lat / 3.0) * Math.PI)) * 2.0) / 3.0;
        result += ((160.0 * Math.sin((lat / 12.0) * Math.PI) + 320.0 * Math.sin((lat * Math.PI) / 30.0)) * 2.0) / 3.0;
        return result;
    }

    /**
     * 计算经度偏移量。
     *
     * @param lon 经度差值。
     * @param lat 纬度差值。
     * @return 经度偏移量。
     */
    private static double transformLongitude(double lon, double lat) {
        double result = 300.0 + lon + 2.0 * lat + 0.1 * lon * lon;
        result += 0.1 * lon * lat + 0.1 * Math.sqrt(Math.abs(lon));
        result += ((20.0 * Math.sin(6.0 * lon * Math.PI) + 20.0 * Math.sin(2.0 * lon * Math.PI)) * 2.0) / 3.0;
        result += ((20.0 * Math.sin(lon * Math.PI) + 40.0 * Math.sin((lon / 3.0) * Math.PI)) * 2.0) / 3.0;
        result += ((150.0 * Math.sin((lon / 12.0) * Math.PI) + 300.0 * Math.sin((lon / 30.0) * Math.PI)) * 2.0) / 3.0;
        return result;
    }

}
