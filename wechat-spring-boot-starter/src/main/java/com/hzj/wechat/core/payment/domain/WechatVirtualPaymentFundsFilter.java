package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 广告金充值记录查询过滤条件。
 */
@Data
public class WechatVirtualPaymentFundsFilter {

    /**
     * 充值开始时间。
     */
    @SerializedName("oper_time_begin")
    private Long operTimeBegin;

    /**
     * 充值结束时间。
     */
    @SerializedName("oper_time_end")
    private Long operTimeEnd;

    /**
     * 充值单 ID。
     */
    @SerializedName("bill_id")
    private String billId;

    /**
     * 创建充值单时的请求 ID。
     */
    @SerializedName("request_id")
    private String requestId;
}
