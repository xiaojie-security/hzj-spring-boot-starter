package com.hzj.wechat.core.ad.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 微信小程序广告位信息。
 */
@Data
@NoArgsConstructor
public class WechatAdUnitInfo {

    /**
     * 广告位类型名称。
     */
    @SerializedName("ad_slot")
    private String adSlot;

    /**
     * 广告位 ID。
     */
    @SerializedName("ad_unit_id")
    private String adUnitId;

    /**
     * 广告位名称。
     */
    @SerializedName("ad_unit_name")
    private String adUnitName;

    /**
     * 广告位尺寸列表。
     */
    @SerializedName("ad_unit_size")
    private List<WechatAdUnitSize> adUnitSize;

    /**
     * 广告位状态。
     */
    @SerializedName("ad_unit_status")
    private String adUnitStatus;

    /**
     * 广告位类型。
     */
    @SerializedName("ad_unit_type")
    private String adUnitType;

    /**
     * 小程序 AppID。
     */
    private String appid;

    /**
     * 视频广告最大时长。
     */
    @SerializedName("video_duration_max")
    private Integer videoDurationMax;

    /**
     * 视频广告最小时长。
     */
    @SerializedName("video_duration_min")
    private Integer videoDurationMin;
}
