package com.hzj.amap.core.webapi.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 高德天气查询响应。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AMapWeatherResponse extends AMapWebApiResponse {

    /** 实况天气列表。 */
    private List<AMapWeatherLive> lives;
    /** 预报天气列表。 */
    private List<AMapWeatherForecast> forecasts;

    /**
     * 实况天气信息。
     */
    @Data
    public static class AMapWeatherLive {
        /** 省份。 */
        private String province;
        /** 城市。 */
        private String city;
        /** 城市编码。 */
        private String adcode;
        /** 天气现象。 */
        private String weather;
        /** 实时气温。 */
        private String temperature;
        /** 风向。 */
        private String winddirection;
        /** 风力等级。 */
        private String windpower;
        /** 空气湿度。 */
        private String humidity;
        /** 数据发布时间。 */
        private String reporttime;
        /** 温度单位。 */
        private String temperatureFloat;
        /** 湿度单位。 */
        private String humidityFloat;
    }

    /**
     * 预报天气信息。
     */
    @Data
    public static class AMapWeatherForecast {
        /** 省份。 */
        private String province;
        /** 城市。 */
        private String city;
        /** 城市编码。 */
        private String adcode;
        /** 预报发布时间。 */
        private String reporttime;
        /** 每日预报列表。 */
        private List<AMapWeatherCast> casts;
    }

    /**
     * 每日天气预报信息。
     */
    @Data
    public static class AMapWeatherCast {
        /** 日期。 */
        private String date;
        /** 星期。 */
        private String week;
        /** 白天天气。 */
        private String dayweather;
        /** 晚间天气。 */
        private String nightweather;
        /** 白天温度。 */
        private String daytemp;
        /** 晚间温度。 */
        private String nighttemp;
        /** 白天风向。 */
        private String daywind;
        /** 晚间风向。 */
        private String nightwind;
        /** 白天风力。 */
        private String daypower;
        /** 晚间风力。 */
        private String nightpower;
        /** 白天温度浮点值。 */
        private String daytempFloat;
        /** 晚间温度浮点值。 */
        private String nighttempFloat;
    }
}
