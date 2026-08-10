package com.hzj.alipay.core.verification.domain;

import lombok.Data;

/**
 * 身份证二要素核验参数。
 */
@Data
public class AlipayTwoMetaCheckParam {

    /** 业务场景码。 */
    private String bizCode;
    /** 姓名。 */
    private String certName;
    /** 身份证号。 */
    private String certNo;
    /** 证件类型，身份证通常为 IDCARD。 */
    private String certType;
    /** 商户请求流水号。 */
    private String outerBizNo;
}
