package com.hzj.alipay.core.verification.domain;

import lombok.Data;

/**
 * 手机号三要素核验参数。
 */
@Data
public class AlipayMobileThreeMetaCheckParam {

    /** 业务场景码。 */
    private String bizCode;
    /** 姓名。 */
    private String certName;
    /** 证件号。 */
    private String certNo;
    /** 商户请求流水号。 */
    private String outerBizNo;
    /** 手机号。 */
    private String phone;
}
