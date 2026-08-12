package com.hzj.wechat.core.payment.domain;

import com.hzj.wechat.core.payment.enums.WechatVirtualPaymentApi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取投诉详情请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatGetComplaintDetailRequest extends WechatVirtualPaymentRequest {
    public WechatGetComplaintDetailRequest() { super(WechatVirtualPaymentApi.GET_COMPLAINT_DETAIL); }

    /**
     * 投诉 ID。
     */
    @SerializedName("complaint_id")
    private String complaintId;
}
