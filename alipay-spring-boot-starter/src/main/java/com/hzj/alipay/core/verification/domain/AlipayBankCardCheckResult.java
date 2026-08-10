package com.hzj.alipay.core.verification.domain;

import com.hzj.alipay.core.verification.enums.AlipayVerificationMatch;
import lombok.Builder;
import lombok.Data;

/**
 * 银行卡核验结果。
 */
@Data
@Builder
public class AlipayBankCardCheckResult {

    /** 是否调用成功。 */
    private boolean success;
    /** API 方法名。 */
    private String apiMethod;
    /** 响应码。 */
    private String code;
    /** 响应信息。 */
    private String msg;
    /** 子响应码。 */
    private String subCode;
    /** 子响应信息。 */
    private String subMsg;
    /** 支付宝核验流水号。 */
    private String certifyId;
    /** 核验匹配结果。 */
    private String match;
    /** 结构化匹配结果。 */
    private AlipayVerificationMatch matchStatus;
    /** 详细核验结果。 */
    private String detail;
}
