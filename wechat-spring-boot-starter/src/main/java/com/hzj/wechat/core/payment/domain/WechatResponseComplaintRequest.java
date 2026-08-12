package com.hzj.wechat.core.payment.domain;

import com.hzj.wechat.core.payment.enums.WechatVirtualPaymentApi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 回复投诉用户请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatResponseComplaintRequest extends WechatVirtualPaymentRequest {
    public WechatResponseComplaintRequest() { super(WechatVirtualPaymentApi.RESPONSE_COMPLAINT); }

    /**
     * 投诉 ID。
     */
    @SerializedName("complaint_id")
    private String complaintId;

    /**
     * 回复内容。
     */
    @SerializedName("response_content")
    private String responseContent;

    /**
     * 回复图片对应的媒体文件 ID 列表。
     */
    @SerializedName("response_images")
    private List<String> responseImages;
}
