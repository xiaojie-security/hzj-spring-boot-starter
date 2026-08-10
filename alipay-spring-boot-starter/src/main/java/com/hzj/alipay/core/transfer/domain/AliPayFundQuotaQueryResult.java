package com.hzj.alipay.core.transfer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 转账额度查询结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayFundQuotaQueryResult {

    /**
     * 是否请求成功。
     */
    private boolean success;

    /**
     * 支付宝接口名。
     */
    private String apiMethod;

    /**
     * 支付宝响应码。
     */
    private String code;

    /**
     * 支付宝响应描述。
     */
    private String msg;

    /**
     * 支付宝子响应码。
     */
    private String subCode;

    /**
     * 支付宝子响应描述。
     */
    private String subMsg;

    /**
     * 是否命中新日限额剩余限制。
     */
    private Boolean activeNewQuotaDailyRemainLimited;

    /**
     * 日限额剩余限制类型。
     */
    private String activeNewQuotaDailyRemainLimitType;

    /**
     * 是否命中新月限额剩余限制。
     */
    private Boolean activeNewQuotaMonthlyRemainLimited;

    /**
     * 月限额剩余限制类型。
     */
    private String activeNewQuotaMonthlyRemainLimitType;

    /**
     * 当前是否使用新限额体系。
     */
    private Boolean activeQuotaIsNew;

    /**
     * 新限额体系日额度。
     */
    private String newQuotaDaily;

    /**
     * 新限额体系日剩余额度。
     */
    private String newQuotaDailyRemain;

    /**
     * 新限额体系月额度。
     */
    private String newQuotaMonthly;

    /**
     * 新限额体系月剩余额度。
     */
    private String newQuotaMonthlyRemain;

    /**
     * 新限额体系单笔最大额度。
     */
    private String newQuotaSingleMax;

    /**
     * 新限额体系单笔最小额度。
     */
    private String newQuotaSingleMin;

    /**
     * 对公日可用额度。
     */
    private String toCorporateDailyAvailableAmount;

    /**
     * 对公月可用额度。
     */
    private String toCorporateMonthlyAvailableAmount;

    /**
     * 对私日可用额度。
     */
    private String toPrivateDailyAvailableAmount;

    /**
     * 对私月可用额度。
     */
    private String toPrivateMonthlyAvailableAmount;
}
