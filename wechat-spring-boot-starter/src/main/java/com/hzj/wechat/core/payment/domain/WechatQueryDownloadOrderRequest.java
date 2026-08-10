package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询支付订单下载任务请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatQueryDownloadOrderRequest extends WechatVirtualPaymentRequest {

    /**
     * 下载任务 ID。
     */
    @SerializedName("task_id")
    private String taskId;
}
