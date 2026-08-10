package com.hzj.alipay.core.payment.domain;

import com.hzj.alipay.core.payment.enums.AlipayRefundStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 支付宝退款查询返回值。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayRefundQueryResult {

    private boolean success;

    private String apiMethod;

    private String code;

    private String msg;

    private String subCode;

    private String subMsg;

    private String outTradeNo;

    private String tradeNo;

    private String outRequestNo;

    private AlipayRefundStatus refundStatus;

    private String refundStatusCode;

    private String refundAmount;

    private String sendBackFee;

    private String refundReason;

    private Date gmtRefundPay;

    private String totalAmount;
}
