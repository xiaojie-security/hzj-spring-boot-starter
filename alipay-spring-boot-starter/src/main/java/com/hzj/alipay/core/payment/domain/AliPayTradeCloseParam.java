package com.hzj.alipay.core.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付宝交易关闭入参。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliPayTradeCloseParam {

    /**
     * 商户订单号。
     */
    private String outTradeNo;

    /**
     * 支付宝交易号。
     */
    private String tradeNo;

    /**
     * 商家操作员编号。
     */
    private String operatorId;
}
