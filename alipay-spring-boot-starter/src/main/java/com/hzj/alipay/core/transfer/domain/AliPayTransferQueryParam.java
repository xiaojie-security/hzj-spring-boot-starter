package com.hzj.alipay.core.transfer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 转账单据查询参数。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliPayTransferQueryParam {

    /**
     * 转账业务场景。
     */
    private String bizScene;

    /**
     * 支付宝转账单据号。
     */
    private String orderId;

    /**
     * 商户转账单号。
     */
    private String outBizNo;

    /**
     * 支付宝支付资金流水号。
     */
    private String payFundOrderId;

    /**
     * 转账产品码。
     */
    private String productCode;
}
