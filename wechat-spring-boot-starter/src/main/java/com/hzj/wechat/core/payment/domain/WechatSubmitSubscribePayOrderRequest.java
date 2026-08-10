package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 提交订阅扣款订单请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatSubmitSubscribePayOrderRequest extends WechatVirtualPaymentRequest {

    /**
     * 用户 openid。
     */
    private String openid;

    /**
     * 米大师侧申请的应用 ID；为空时使用配置值。
     */
    @SerializedName("offer_id")
    private String offerId;

    /**
     * 购买数量，官方要求填 1。
     */
    @SerializedName("buy_quantity")
    private Integer buyQuantity;

    /**
     * 币种；为空时使用配置值。
     */
    @SerializedName("currency_type")
    private String currencyType;

    /**
     * 订阅道具 ID。
     */
    @SerializedName("product_id")
    private String productId;

    /**
     * 扣款金额，单位为分。
     */
    @SerializedName("deduct_price")
    private Long deductPrice;

    /**
     * 商户业务订单号。
     */
    @SerializedName("order_id")
    private String orderId;

    /**
     * 发货通知透传数据。
     */
    private String attach;
}
