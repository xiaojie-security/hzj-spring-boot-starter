package com.hzj.alipay.core.payment.domain;

import com.hzj.alipay.core.payment.enums.AlipayTradeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 支付宝交易查询返回值。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayTradeQueryResult {

    private boolean success;

    private String apiMethod;

    private String code;

    private String msg;

    private String subCode;

    private String subMsg;

    private String outTradeNo;

    private String tradeNo;

    private AlipayTradeStatus tradeStatus;

    private String tradeStatusCode;

    private String buyerLogonId;

    private String buyerUserId;

    private String buyerPayAmount;

    private String receiptAmount;

    private String invoiceAmount;

    private Date sendPayDate;

    private String totalAmount;
}
