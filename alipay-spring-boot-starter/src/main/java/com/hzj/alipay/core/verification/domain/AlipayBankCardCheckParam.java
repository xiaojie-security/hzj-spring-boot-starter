package com.hzj.alipay.core.verification.domain;

import lombok.Data;

/**
 * 银行卡核验参数。
 */
@Data
public class AlipayBankCardCheckParam {

    /** 银行卡号。 */
    private String bankcardNo;
    /** 业务场景码。 */
    private String bizCode;
    /** 姓名。 */
    private String certName;
    /** 证件号。 */
    private String certNo;
    /** 证件类型。 */
    private String certType;
    /** 商户请求流水号。 */
    private String outerBizNo;
    /** 银行预留手机号。 */
    private String phone;
    /** 产品类型。 */
    private String productType;
}
