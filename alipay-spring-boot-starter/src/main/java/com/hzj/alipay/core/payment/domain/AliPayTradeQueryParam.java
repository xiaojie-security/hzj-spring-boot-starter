package com.hzj.alipay.core.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付宝交易查询入参。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliPayTradeQueryParam {

    /**
     * 商户订单号。
     */
    private String outTradeNo;

    /**
     * 支付宝交易号。
     */
    private String tradeNo;

    /**
     * 收单机构 pid。
     */
    private String orgPid;

    /**
     * 查询选项。
     */
    private String[] queryOptions;
}
