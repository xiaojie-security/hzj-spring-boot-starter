package com.hzj.alipay.core.transfer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 转账额度查询参数。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliPayFundQuotaQueryParam {

    /**
     * 业务场景。
     */
    private String bizScene;

    /**
     * 产品码。
     */
    private String productCode;
}
