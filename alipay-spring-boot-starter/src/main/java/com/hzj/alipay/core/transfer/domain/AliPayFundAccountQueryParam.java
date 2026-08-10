package com.hzj.alipay.core.transfer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资金账户查询参数。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliPayFundAccountQueryParam {

    /**
     * 开户产品码。
     */
    private String accountProductCode;

    /**
     * 开户场景码。
     */
    private String accountSceneCode;

    /**
     * 查询账户类型。
     */
    private String accountType;

    /**
     * 支付宝 OpenID。
     */
    private String alipayOpenId;

    /**
     * 支付宝用户 ID。
     */
    private String alipayUserId;

    /**
     * 业务扩展参数。
     */
    private String extInfo;

    /**
     * 商户侧用户唯一标识。
     */
    private String merchantUserId;
}
