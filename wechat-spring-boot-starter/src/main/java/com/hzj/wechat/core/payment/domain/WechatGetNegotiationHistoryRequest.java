package com.hzj.wechat.core.payment.domain;

import com.hzj.wechat.core.payment.enums.WechatVirtualPaymentApi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取投诉协商历史请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatGetNegotiationHistoryRequest extends WechatVirtualPaymentRequest {
    public WechatGetNegotiationHistoryRequest() { super(WechatVirtualPaymentApi.GET_NEGOTIATION_HISTORY); }

    /**
     * 投诉 ID。
     */
    @SerializedName("complaint_id")
    private String complaintId;

    /**
     * 筛选偏移量。
     */
    private Integer offset;

    /**
     * 最多返回条数。
     */
    private Integer limit;
}
