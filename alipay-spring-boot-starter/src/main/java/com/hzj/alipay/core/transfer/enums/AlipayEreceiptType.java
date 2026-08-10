package com.hzj.alipay.core.transfer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 电子回单类型枚举。
 */
@Getter
@AllArgsConstructor
public enum AlipayEreceiptType {

    ACCOUNT_LOG_DETAIL("ACCOUNT_LOG_DETAIL", "账务流水明细回单"),
    ACCOUNT_FLOW_DETAIL("ACCOUNT_FLOW_DETAIL", "账务区间流水明细回单"),
    FUND_DETAIL("FUND_DETAIL", "资金单据回单"),
    ACCOUNT_LOG_SUM_DAILY("ACCOUNT_LOG_SUM_DAILY", "日汇总回单"),
    ACCOUNT_LOG_SUM_MONTHLY("ACCOUNT_LOG_SUM_MONTHLY", "月汇总回单");

    private final String code;

    private final String message;
}
