package com.hzj.alipay.core.verification;

import com.hzj.alipay.core.verification.domain.AlipayBankCardCheckParam;
import com.hzj.alipay.core.verification.domain.AlipayBankCardCheckResult;
import com.hzj.alipay.core.verification.domain.AlipayMobileThreeMetaCheckParam;
import com.hzj.alipay.core.verification.domain.AlipayMobileThreeMetaCheckResult;
import com.hzj.alipay.core.verification.domain.AlipayTwoMetaCheckParam;
import com.hzj.alipay.core.verification.domain.AlipayTwoMetaCheckResult;

/**
 * 支付宝实名信息核验服务。
 */
public interface AlipayVerificationService {

    /**
     * 身份证二要素核验。
     *
     * @param param 核验参数
     * @return 核验结果
     */
    AlipayTwoMetaCheckResult checkTwoMeta(AlipayTwoMetaCheckParam param);

    /**
     * 银行卡核验。
     *
     * @param param 核验参数
     * @return 核验结果
     */
    AlipayBankCardCheckResult checkBankCard(AlipayBankCardCheckParam param);

    /**
     * 手机号三要素核验。
     *
     * @param param 核验参数
     * @return 核验结果
     */
    AlipayMobileThreeMetaCheckResult checkMobileThreeMeta(AlipayMobileThreeMetaCheckParam param);
}
