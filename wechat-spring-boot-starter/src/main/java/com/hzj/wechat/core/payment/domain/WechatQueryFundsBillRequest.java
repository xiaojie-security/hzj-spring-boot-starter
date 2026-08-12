package com.hzj.wechat.core.payment.domain;

import com.hzj.wechat.core.payment.enums.WechatVirtualPaymentApi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询广告金充值记录请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatQueryFundsBillRequest extends WechatVirtualPaymentRequest {
    public WechatQueryFundsBillRequest() { super(WechatVirtualPaymentApi.QUERY_FUNDS_BILL); }

    /**
     * 页码。
     */
    private Integer page;

    /**
     * 每页记录数量。
     */
    @SerializedName("page_size")
    private Integer pageSize;

    /**
     * 查询过滤条件。
     */
    private WechatVirtualPaymentFundsFilter filter;
}
