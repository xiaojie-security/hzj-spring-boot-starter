package com.hzj.alipay.core.transfer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付宝账单类型枚举。
 */
@Getter
@AllArgsConstructor
public enum AlipayBillType {

    /**
     * 交易账单。
     */
    TRADE("trade", "商户基于支付宝交易收单的业务账单"),

    /**
     * 签约账单。
     */
    SIGNCUSTOMER("signcustomer", "商户基于支付宝余额收入及支出等资金变动的帐务账单"),

    /**
     * 营销账单。
     */
    MERCHANT_ACT("merchant_act", "商家活动账单"),

    /**
     * 直付通二级商户账单。
     */
    TRADE_ZFT_MERCHANT("trade_zft_merchant", "直付通二级商户查询交易账单");

    private final String code;

    private final String message;
}
