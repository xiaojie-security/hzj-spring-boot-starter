package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信虚拟支付订单详情。
 */
@Data
@NoArgsConstructor
public class WechatVirtualPaymentOrder {

    /**
     * 商户订单号。
     */
    @SerializedName("order_id")
    private String orderId;

    /**
     * 创建时间，Unix 秒级时间戳。
     */
    @SerializedName("create_time")
    private Long createTime;

    /**
     * 更新时间，Unix 秒级时间戳。
     */
    @SerializedName("update_time")
    private Long updateTime;

    /**
     * 订单当前状态。
     */
    private Integer status;

    /**
     * 业务类型。
     */
    @SerializedName("biz_type")
    private Integer bizType;

    /**
     * 订单金额，单位为分。
     */
    @SerializedName("order_fee")
    private Long orderFee;

    /**
     * 用户实际支付金额，单位为分。
     */
    @SerializedName("paid_fee")
    private Long paidFee;

    /**
     * 订单类型。
     */
    @SerializedName("order_type")
    private Integer orderType;

    /**
     * 退款金额，单位为分。
     */
    @SerializedName("refund_fee")
    private Long refundFee;

    /**
     * 支付或退款时间，Unix 秒级时间戳。
     */
    @SerializedName("paid_time")
    private Long paidTime;

    /**
     * 发货时间，Unix 秒级时间戳。
     */
    @SerializedName("provide_time")
    private Long provideTime;

    /**
     * 商家自定义数据。
     */
    @SerializedName("biz_meta")
    private String bizMeta;

    /**
     * 环境类型。
     */
    @SerializedName("env_type")
    private Integer envType;

    /**
     * 米大师订单 token。
     */
    private String token;

    /**
     * 当前订单剩余可退款金额，单位为分。
     */
    @SerializedName("left_fee")
    private Long leftFee;

    /**
     * 微信内部订单号。
     */
    @SerializedName("wx_order_id")
    private String wxOrderId;

    /**
     * 渠道订单号。
     */
    @SerializedName("channel_order_id")
    private String channelOrderId;

    /**
     * 微信支付交易单号。
     */
    @SerializedName("wxpay_order_id")
    private String wxpayOrderId;

    /**
     * 结算时间，Unix 秒级时间戳。
     */
    @SerializedName("sett_time")
    private Long settTime;

    /**
     * 结算状态。
     */
    @SerializedName("sett_state")
    private Integer settState;

    /**
     * 虚拟支付技术服务费，单位为分。
     */
    @SerializedName("platform_fee_fen")
    private Long platformFeeFen;

    /**
     * CPS 服务费，单位为分。
     */
    @SerializedName("cps_fee_fen")
    private Long cpsFeeFen;
}
