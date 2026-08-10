package com.hzj.wechat.core.ad.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信小程序广告细分数据明细。
 */
@Data
@NoArgsConstructor
public class WechatAdDataDetailItem {

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
     * 按广告位和日期统计的数据。
     */
    @SerializedName("stat_item")
    private WechatAdUnitStatistics statItem;
}
