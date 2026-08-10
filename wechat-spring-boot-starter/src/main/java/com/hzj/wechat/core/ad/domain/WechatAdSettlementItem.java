package com.hzj.wechat.core.ad.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 小程序广告结算区间数据。
 */
@Data
@NoArgsConstructor
public class WechatAdSettlementItem {

    /**
     * 数据更新时间。
     */
    private String date;

    /**
     * 结算日期区间。
     */
    private String zone;

    /**
     * 收入月份。
     */
    private String month;

    /**
     * 半月顺序，1 表示上半月，2 表示下半月。
     */
    private Integer order;

    /**
     * 结算状态。
     */
    @SerializedName("sett_status")
    private Integer settStatus;

    /**
     * 区间结算收入，单位为分。
     */
    @SerializedName("settled_revenue")
    private Long settledRevenue;

    /**
     * 结算单编号。
     */
    @SerializedName("sett_no")
    private String settNo;

    /**
     * 申请补发结算单次数。
     */
    @SerializedName("mail_send_cnt")
    private Integer mailSendCnt;

    /**
     * 各广告位结算收入。
     */
    @SerializedName("slot_revenue")
    private List<WechatAdSettlementSlotRevenue> slotRevenue;
}
