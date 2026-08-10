package com.hzj.alipay.core.payment.domain;

import com.hzj.alipay.core.payment.enums.AlipayPaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付宝统一支付返回值。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayPaymentResult {

    /**
     * 是否成功。
     */
    private boolean success;

    /**
     * 支付类型。
     */
    private AlipayPaymentType paymentType;

    /**
     * 支付宝接口名。
     */
    private String apiMethod;

    /**
     * 支付宝响应码。
     */
    private String code;

    /**
     * 支付宝响应描述。
     */
    private String msg;

    /**
     * 支付宝子响应码。
     */
    private String subCode;

    /**
     * 支付宝子响应描述。
     */
    private String subMsg;

    /**
     * 商户订单号。
     */
    private String outTradeNo;

    /**
     * 支付宝交易号。
     */
    private String tradeNo;

    /**
     * 卖家支付宝用户 ID。
     */
    private String sellerId;

    /**
     * 订单金额。
     */
    private String totalAmount;

    /**
     * 通用支付内容。
     * APP 为订单串，WAP/PAGE 为表单 HTML，PRECREATE 为二维码码串。
     */
    private String payData;

    /**
     * 预下单二维码码串。
     */
    private String qrCode;

    /**
     * 预下单 ID。
     */
    private String prepayId;

    /**
     * 吱口令码串。
     */
    private String shareCode;
}
