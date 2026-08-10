package com.hzj.alipay.core.transfer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 转账产品码枚举。
 */
@Getter
@AllArgsConstructor
public enum AlipayFundTransferProductCode {

    TRANS_ACCOUNT_NO_PWD("TRANS_ACCOUNT_NO_PWD", "转账到支付宝账户"),
    STD_RED_PACKET("STD_RED_PACKET", "现金红包"),
    TRANS_BANKCARD_NO_PWD("TRANS_BANKCARD_NO_PWD", "转账到银行卡"),
    DEFAULT("DEFAULT", "默认产品码");

    private final String code;

    private final String message;
}
