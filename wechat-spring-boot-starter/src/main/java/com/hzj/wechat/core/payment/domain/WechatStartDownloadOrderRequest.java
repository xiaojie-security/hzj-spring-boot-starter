package com.hzj.wechat.core.payment.domain;

import com.hzj.wechat.core.payment.enums.WechatVirtualPaymentApi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 启动支付订单下载任务请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatStartDownloadOrderRequest extends WechatVirtualPaymentRequest {
    public WechatStartDownloadOrderRequest() { super(WechatVirtualPaymentApi.START_DOWNLOAD_ORDER); }

    /**
     * 开始日期，格式 yyyyMMdd。
     */
    @SerializedName("begin_ds")
    private Long beginDs;

    /**
     * 结束日期，格式 yyyyMMdd。
     */
    @SerializedName("end_ds")
    private Long endDs;

    /**
     * 订单类型。
     */
    @SerializedName("order_type")
    private Integer orderType;

    /**
     * 订单信息搜索关键字。
     */
    @SerializedName("order_info")
    private String orderInfo;

    /**
     * 是否已发货。
     */
    @SerializedName("is_provided")
    private Boolean provided;

    /**
     * 退款状态筛选。
     */
    @SerializedName("refund_status")
    private Integer refundStatus;

    /**
     * 支付渠道。
     */
    @SerializedName("pay_channel")
    private Integer payChannel;
}
