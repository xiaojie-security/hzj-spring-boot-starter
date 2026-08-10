package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 广告金回收记录查询过滤条件。
 */
@Data
public class WechatVirtualPaymentRecoverFilter {

    /**
     * 回收开始时间。
     */
    @SerializedName("recover_time_begin")
    private Long recoverTimeBegin;

    /**
     * 回收结束时间。
     */
    @SerializedName("recover_time_end")
    private Long recoverTimeEnd;

    /**
     * 广告金回收单 ID。
     */
    @SerializedName("bill_id")
    private String billId;
}
