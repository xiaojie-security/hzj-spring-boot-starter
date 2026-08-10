package com.hzj.alipay.core.verification.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.DatadigitalFincloudGeneralsaasBankcardCheckModel;
import com.alipay.api.domain.DatadigitalFincloudGeneralsaasMobilethreemetaDetailCheckModel;
import com.alipay.api.domain.DatadigitalFincloudGeneralsaasTwometaCheckModel;
import com.alipay.api.request.DatadigitalFincloudGeneralsaasBankcardCheckRequest;
import com.alipay.api.request.DatadigitalFincloudGeneralsaasMobilethreemetaDetailCheckRequest;
import com.alipay.api.request.DatadigitalFincloudGeneralsaasTwometaCheckRequest;
import com.alipay.api.response.DatadigitalFincloudGeneralsaasBankcardCheckResponse;
import com.alipay.api.response.DatadigitalFincloudGeneralsaasMobilethreemetaDetailCheckResponse;
import com.alipay.api.response.DatadigitalFincloudGeneralsaasTwometaCheckResponse;
import com.hzj.alipay.core.AbstractAlipayService;
import com.hzj.alipay.core.verification.AlipayVerificationService;
import com.hzj.alipay.core.verification.domain.AlipayBankCardCheckParam;
import com.hzj.alipay.core.verification.domain.AlipayBankCardCheckResult;
import com.hzj.alipay.core.verification.domain.AlipayMobileThreeMetaCheckParam;
import com.hzj.alipay.core.verification.domain.AlipayMobileThreeMetaCheckResult;
import com.hzj.alipay.core.verification.domain.AlipayTwoMetaCheckParam;
import com.hzj.alipay.core.verification.domain.AlipayTwoMetaCheckResult;
import com.hzj.alipay.core.verification.enums.AlipayVerificationMatch;
import com.hzj.alipay.provider.alipay.verification.AlipayVerificationConfigProvider;
import com.hzj.alipay.provider.alipay.verification.entity.AlipayVerificationConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 支付宝实名信息核验默认实现。
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultAlipayVerificationService extends AbstractAlipayService<AlipayVerificationConfig> implements AlipayVerificationService {

    private final AlipayVerificationConfigProvider provider;

    @Override
    protected com.alipay.api.AlipayClient getAlipayClient() {
        return createAlipayClient();
    }

    @Override
    protected AlipayVerificationConfigProvider getAlipayConfigProvider() {
        return provider;
    }

    @Override
    public AlipayTwoMetaCheckResult checkTwoMeta(AlipayTwoMetaCheckParam param) {
        requireParam(param, "身份证二要素核验参数");
        requireText(param.getBizCode(), "bizCode");
        requireText(param.getCertName(), "certName");
        requireText(param.getCertNo(), "certNo");
        requireText(param.getOuterBizNo(), "outerBizNo");
        DatadigitalFincloudGeneralsaasTwometaCheckModel model = new DatadigitalFincloudGeneralsaasTwometaCheckModel();
        model.setBizCode(param.getBizCode());
        model.setCertName(param.getCertName());
        model.setCertNo(param.getCertNo());
        model.setCertType(param.getCertType());
        model.setOuterBizNo(param.getOuterBizNo());
        DatadigitalFincloudGeneralsaasTwometaCheckRequest request = new DatadigitalFincloudGeneralsaasTwometaCheckRequest();
        request.setBizModel(model);
        try {
            DatadigitalFincloudGeneralsaasTwometaCheckResponse response = execute(request);
            return AlipayTwoMetaCheckResult.builder().success(response.isSuccess()).apiMethod(request.getApiMethodName())
                    .code(response.getCode()).msg(response.getMsg()).subCode(response.getSubCode()).subMsg(response.getSubMsg())
                    .certifyId(response.getCertifyId()).match(response.getMatch())
                    .matchStatus(AlipayVerificationMatch.fromCode(response.getMatch())).build();
        } catch (AlipayApiException e) {
            log.error("DefaultAlipayVerificationService.checkTwoMeta 身份证二要素核验失败, outerBizNo={}", param.getOuterBizNo(), e);
            throw new IllegalStateException("身份证二要素核验失败", e);
        }
    }

    @Override
    public AlipayBankCardCheckResult checkBankCard(AlipayBankCardCheckParam param) {
        requireParam(param, "银行卡核验参数");
        requireText(param.getBizCode(), "bizCode");
        requireText(param.getBankcardNo(), "bankcardNo");
        requireText(param.getCertName(), "certName");
        requireText(param.getCertNo(), "certNo");
        requireText(param.getOuterBizNo(), "outerBizNo");
        requireText(param.getPhone(), "phone");
        DatadigitalFincloudGeneralsaasBankcardCheckModel model = new DatadigitalFincloudGeneralsaasBankcardCheckModel();
        model.setBankcardNo(param.getBankcardNo());
        model.setBizCode(param.getBizCode());
        model.setCertName(param.getCertName());
        model.setCertNo(param.getCertNo());
        model.setCertType(param.getCertType());
        model.setOuterBizNo(param.getOuterBizNo());
        model.setPhone(param.getPhone());
        model.setProductType(param.getProductType());
        DatadigitalFincloudGeneralsaasBankcardCheckRequest request = new DatadigitalFincloudGeneralsaasBankcardCheckRequest();
        request.setBizModel(model);
        try {
            DatadigitalFincloudGeneralsaasBankcardCheckResponse response = execute(request);
            return AlipayBankCardCheckResult.builder().success(response.isSuccess()).apiMethod(request.getApiMethodName())
                    .code(response.getCode()).msg(response.getMsg()).subCode(response.getSubCode()).subMsg(response.getSubMsg())
                    .certifyId(response.getCertifyId()).match(response.getMatch())
                    .matchStatus(AlipayVerificationMatch.fromCode(response.getMatch())).detail(response.getDetail()).build();
        } catch (AlipayApiException e) {
            log.error("DefaultAlipayVerificationService.checkBankCard 银行卡核验失败, outerBizNo={}", param.getOuterBizNo(), e);
            throw new IllegalStateException("银行卡核验失败", e);
        }
    }

    @Override
    public AlipayMobileThreeMetaCheckResult checkMobileThreeMeta(AlipayMobileThreeMetaCheckParam param) {
        requireParam(param, "手机号三要素核验参数");
        requireText(param.getBizCode(), "bizCode");
        requireText(param.getCertName(), "certName");
        requireText(param.getCertNo(), "certNo");
        requireText(param.getOuterBizNo(), "outerBizNo");
        requireText(param.getPhone(), "phone");
        DatadigitalFincloudGeneralsaasMobilethreemetaDetailCheckModel model = new DatadigitalFincloudGeneralsaasMobilethreemetaDetailCheckModel();
        model.setBizCode(param.getBizCode());
        model.setCertName(param.getCertName());
        model.setCertNo(param.getCertNo());
        model.setOuterBizNo(param.getOuterBizNo());
        model.setPhone(param.getPhone());
        DatadigitalFincloudGeneralsaasMobilethreemetaDetailCheckRequest request = new DatadigitalFincloudGeneralsaasMobilethreemetaDetailCheckRequest();
        request.setBizModel(model);
        try {
            DatadigitalFincloudGeneralsaasMobilethreemetaDetailCheckResponse response = execute(request);
            return AlipayMobileThreeMetaCheckResult.builder().success(response.isSuccess()).apiMethod(request.getApiMethodName())
                    .code(response.getCode()).msg(response.getMsg()).subCode(response.getSubCode()).subMsg(response.getSubMsg())
                    .certifyId(response.getCertifyId()).match(response.getMatch())
                    .matchStatus(AlipayVerificationMatch.fromCode(response.getMatch())).detail(response.getDetail()).isp(response.getIsp()).build();
        } catch (AlipayApiException e) {
            log.error("DefaultAlipayVerificationService.checkMobileThreeMeta 手机号三要素核验失败, outerBizNo={}", param.getOuterBizNo(), e);
            throw new IllegalStateException("手机号三要素核验失败", e);
        }
    }

    private void requireParam(Object param, String message) {
        if (param == null) {
            log.error("DefaultAlipayVerificationService.requireParam {}", message);
            throw new IllegalArgumentException(message + "不能为空");
        }
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            log.error("DefaultAlipayVerificationService.requireText 核验字段不能为空, fieldName={}", fieldName);
            throw new IllegalArgumentException("核验字段不能为空: " + fieldName);
        }
    }
}
