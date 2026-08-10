package com.hzj.wechat.core.ad.domain;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信广告数据统计指标。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WechatAdDataStatistics {

    /**
     * 广告请求成功次数。
     */
    @SerializedName("req_succ_count")
    private Long reqSuccCount;

    /**
     * 广告曝光次数。
     */
    @SerializedName("exposure_count")
    private Long exposureCount;

    /**
     * 广告曝光率。
     */
    @SerializedName("exposure_rate")
    private Double exposureRate;

    /**
     * 广告点击次数。
     */
    @SerializedName("click_count")
    private Long clickCount;

    /**
     * 广告点击率。
     */
    @SerializedName("click_rate")
    private Double clickRate;

    /**
     * 广告收入，单位为分。
     */
    @SerializedName("income")
    private Long income;

    /**
     * 广告千次曝光收益，单位为分。
     */
    @SerializedName("ecpm")
    private Double ecpm;
}
