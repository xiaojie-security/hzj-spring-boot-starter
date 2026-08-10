package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 完成投诉处理请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatCompleteComplaintRequest extends WechatVirtualPaymentRequest {

    /**
     * 投诉 ID。
     */
    @SerializedName("complaint_id")
    private String complaintId;
}
