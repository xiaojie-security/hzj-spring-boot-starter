package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 下载 iOS 月结账单请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatDownloadIosSettlementBillRequest extends WechatVirtualPaymentRequest {

    /**
     * 开始月份，格式 yyyyMM。
     */
    @SerializedName("start_month")
    private String startMonth;

    /**
     * 结束月份，格式 yyyyMM。
     */
    @SerializedName("end_month")
    private String endMonth;
}
