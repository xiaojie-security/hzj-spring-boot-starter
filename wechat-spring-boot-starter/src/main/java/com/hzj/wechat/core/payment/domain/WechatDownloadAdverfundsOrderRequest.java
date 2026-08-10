package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 下载广告金商户订单信息请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatDownloadAdverfundsOrderRequest extends WechatVirtualPaymentRequest {

    /**
     * 广告金发放 ID。
     */
    @SerializedName("fund_id")
    private String fundId;
}
