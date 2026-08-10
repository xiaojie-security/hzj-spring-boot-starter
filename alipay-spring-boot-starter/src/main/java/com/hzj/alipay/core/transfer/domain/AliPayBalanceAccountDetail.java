package com.hzj.alipay.core.transfer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资金账户明细。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayBalanceAccountDetail {

    /**
     * 余额账户金额。
     */
    private String acs;

    /**
     * 银行账户金额。
     */
    private String bank;
}
