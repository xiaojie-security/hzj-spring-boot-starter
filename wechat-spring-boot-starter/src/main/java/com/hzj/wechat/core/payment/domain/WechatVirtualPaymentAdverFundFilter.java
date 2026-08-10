package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 广告金发放记录查询过滤条件。
 */
@Data
public class WechatVirtualPaymentAdverFundFilter {

    /**
     * 结算周期开始时间。
     */
    @SerializedName("settle_begin")
    private Long settleBegin;

    /**
     * 结算周期结束时间。
     */
    @SerializedName("settle_end")
    private Long settleEnd;

    /**
     * 广告金发放原因。
     */
    @SerializedName("fund_type")
    private Integer fundType;
}
