package com.hzj.alipay.core.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付宝退款查询入参。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliPayRefundQueryParam {

    /**
     * 商户订单号。
     */
    private String outTradeNo;

    /**
     * 支付宝交易号。
     */
    private String tradeNo;

    /**
     * 退款请求号。
     */
    private String outRequestNo;

    /**
     * 收单机构 pid。
     */
    private String orgPid;

    /**
     * 查询选项。
     */
    private String[] queryOptions;
}
