package com.hzj.alipay.core.transfer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 资金账户类型枚举。
 */
@Getter
@AllArgsConstructor
public enum AlipayFundAccountType {

    /**
     * 余额账户。
     */
    ACCTRANS_ACCOUNT("ACCTRANS_ACCOUNT", "余额账户");

    private final String code;

    private final String message;
}
