package com.hzj.wechat.core.ad.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 结算区间内广告位收入。
 */
@Data
@NoArgsConstructor
public class WechatAdSettlementSlotRevenue {

    /**
     * 广告位类型 ID。
     */
    @SerializedName("slot_id")
    private String slotId;

    /**
     * 广告位结算金额，单位为分。
     */
    @SerializedName("slot_settled_revenue")
    private Long slotSettledRevenue;
}
