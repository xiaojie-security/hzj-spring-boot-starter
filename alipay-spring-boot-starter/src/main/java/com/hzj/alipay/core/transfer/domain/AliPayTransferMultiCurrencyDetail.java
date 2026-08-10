package com.hzj.alipay.core.transfer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 多币种转账信息。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliPayTransferMultiCurrencyDetail {

    /**
     * 扩展信息。
     */
    private String extInfo;

    /**
     * 支付金额。
     */
    private String paymentAmount;

    /**
     * 支付币种。
     */
    private String paymentCurrency;

    /**
     * 结算金额。
     */
    private String settlementAmount;

    /**
     * 结算币种。
     */
    private String settlementCurrency;

    /**
     * 转账金额。
     */
    private String transAmount;

    /**
     * 转账币种。
     */
    private String transCurrency;
}
