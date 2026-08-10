package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 下载小程序账单请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatDownloadBillRequest extends WechatVirtualPaymentRequest {

    /**
     * 起始日期，格式 yyyyMMdd。
     */
    @SerializedName("begin_ds")
    private Long beginDs;

    /**
     * 截止日期，格式 yyyyMMdd。
     */
    @SerializedName("end_ds")
    private Long endDs;
}
