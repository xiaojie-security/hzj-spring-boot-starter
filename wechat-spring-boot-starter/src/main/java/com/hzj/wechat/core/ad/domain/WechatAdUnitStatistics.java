package com.hzj.wechat.core.ad.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 广告细分统计指标。
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WechatAdUnitStatistics extends WechatAdDataStatistics {

    /**
     * 广告位类型名称。
     */
    @SerializedName("ad_slot")
    private String adSlot;

    /**
     * 数据日期。
     */
    private String date;
}
