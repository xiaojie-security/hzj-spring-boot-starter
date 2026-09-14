package com.hzj.wechat.core.xcx.ad.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 微信小程序结算收入响应。
 */
@Data
@NoArgsConstructor
public class WechatXcxAdSettlementResponse {

    /**
     * 接口基础响应。
     */
    @SerializedName("base_resp")
    private WechatXcxAdBaseResponse baseResp;

    /**
     * 结算主体名称。
     */
    private String body;

    /**
     * 累计收入，单位为分。
     */
    @SerializedName("revenue_all")
    private Long revenueAll;

    /**
     * 累计扣除金额，单位为分。
     */
    @SerializedName("penalty_all")
    private Long penaltyAll;

    /**
     * 累计已结算金额，单位为分。
     */
    @SerializedName("settled_revenue_all")
    private Long settledRevenueAll;

    /**
     * 结算区间列表。
     */
    @SerializedName("settlement_list")
    private List<WechatXcxAdSettlementItem> settlementList;

    /**
     * 返回总条数。
     */
    @SerializedName("total_num")
    private Integer totalNum;
}
