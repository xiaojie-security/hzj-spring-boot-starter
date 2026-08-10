package com.hzj.wechat.core.ad.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 微信小程序广告汇总明细。
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WechatAdDataItem extends WechatAdDataStatistics {

    /**
     * 广告位类型 ID。
     */
    @SerializedName("slot_id")
    private Long slotId;

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
