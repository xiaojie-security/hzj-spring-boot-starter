package com.hzj.alipay.core.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 支付宝退款返回值。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayRefundResult {

    private boolean success;

    private String apiMethod;

    private String code;

    private String msg;

    private String subCode;

    private String subMsg;

    private String outTradeNo;

    private String tradeNo;

    private String outRequestNo;

    private String buyerLogonId;

    private String buyerUserId;

    private String refundFee;

    private String refundAmount;

    private String refundReason;

    private String fundChange;

    private Date gmtRefundPay;
}
