package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取投诉图片签名请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatGetUploadFileSignRequest extends WechatVirtualPaymentRequest {

    /**
     * 微信支付图片地址。
     */
    @SerializedName("wxpay_url")
    private String wxpayUrl;

    /**
     * 是否转存到 COS。
     */
    @SerializedName("convert_cos")
    private Boolean convertCos;

    /**
     * 对应投诉 ID。
     */
    @SerializedName("complaint_id")
    private String complaintId;
}
