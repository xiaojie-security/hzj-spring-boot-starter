package com.hzj.alipay.core.transfer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资金账户查询结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayFundAccountQueryResult {

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
     * 账户总金额。
     */
    private String totalAmount;

    /**
     * 可用金额。
     */
    private String availableAmount;

    /**
     * 冻结金额。
     */
    private String freezeAmount;

    /**
     * 账户金额明细。
     */
    private AliPayBalanceAccountDetail amountDetail;

    /**
     * 外卡账户信息。
     */
    private AliPayExtCardInfo extCardInfo;
}
