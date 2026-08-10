package com.hzj.alipay.core.transfer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 银行卡扩展信息。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliPayBankcardExtInfo {

    /**
     * 银行卡账户类型。
     */
    private String accountType;

    /**
     * 银行联行号。
     */
    private String bankCode;

    /**
     * 开户支行名称。
     */
    private String instBranchName;

    /**
     * 开户行所在城市。
     */
    private String instCity;

    /**
     * 开户银行名称。
     */
    private String instName;

    /**
     * 开户行所在省份。
     */
    private String instProvince;
}
