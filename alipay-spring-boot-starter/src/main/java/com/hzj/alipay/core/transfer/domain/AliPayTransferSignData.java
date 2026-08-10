package com.hzj.alipay.core.transfer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 特殊转账签名信息。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliPayTransferSignData {

    /**
     * 原应用 ID。
     */
    private String oriAppId;

    /**
     * 原字符集。
     */
    private String oriCharSet;

    /**
     * 原商户转账单号。
     */
    private String oriOutBizNo;

    /**
     * 原签名值。
     */
    private String oriSign;

    /**
     * 原签名算法类型。
     */
    private String oriSignType;

    /**
     * 合作方标识。
     */
    private String partnerId;
}
