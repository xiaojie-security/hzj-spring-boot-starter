package com.hzj.alipay.core.transfer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 转账业务场景枚举。
 */
@Getter
@AllArgsConstructor
public enum AlipayFundTransferBizScene {

    DIRECT_TRANSFER("DIRECT_TRANSFER", "单笔无密转账"),
    PERSONAL_COLLECTION("PERSONAL_COLLECTION", "现金红包领红包"),
    PERSONAL_PAY("PERSONAL_PAY", "现金红包发红包"),
    REFUND("REFUND", "现金红包退回"),
    DEFAULT("DEFAULT", "默认场景");

    private final String code;

    private final String message;
}
