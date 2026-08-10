package com.hzj.alipay.core.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付宝交易关闭返回值。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayTradeCloseResult {

    private boolean success;

    private String apiMethod;

    private String code;

    private String msg;

    private String subCode;

    private String subMsg;

    private String outTradeNo;

    private String tradeNo;
}
