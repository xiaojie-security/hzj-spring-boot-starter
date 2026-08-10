package com.hzj.aliyun.core.pns.impl;

import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hzj.aliyun.core.pns.AliyunPnsService;
import com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeResponseBody;
import com.hzj.aliyun.provider.aliyun.pns.AliyunPnsConfigProvider;
import com.hzj.aliyun.provider.aliyun.pns.entity.AliyunPnsConfig;
import com.aliyun.tea.*;
import com.hzj.aliyun.core.pns.domain.AliyunPnsTemplateParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 阿里云号码认证服务
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultAliyunPnsService implements AliyunPnsService {

    private final AliyunPnsConfigProvider configProvider;
    private final com.aliyun.dypnsapi20170525.Client client;

    @Override
    public boolean sendSmsCode(String schemeName,String phoneNumber, String templateCode, AliyunPnsTemplateParam aliyunPnsTemplateParam) {
        AliyunPnsConfig config = getConfig();
        String signName = config.getSignName();
        if(StrUtil.isEmpty(phoneNumber)) {
            log.warn("DefaultAliyunPnsService smsCodeSend 手机号不能为空");
            throw new IllegalArgumentException("手机号不能为空");
        }
        if (!PhoneUtil.isPhone(phoneNumber)) {
            log.warn("DefaultAliyunPnsService smsCodeSend 手机号格式不正确，手机号:{}", phoneNumber);
            throw new IllegalArgumentException("手机号格式不正确");
        }
        CharSequence hideBetweenPhone = PhoneUtil.hideBetween(phoneNumber);
        com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest sendSmsVerifyCodeRequest = new com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest()
                .setCountryCode(config.getCountryCode())
                .setPhoneNumber(phoneNumber)
                .setTemplateCode(templateCode)
                .setTemplateParam(JSONUtil.toJsonStr(aliyunPnsTemplateParam))
                .setSchemeName(schemeName)
                .setSignName(signName)
                .setCodeLength(config.getCodeLength())
                .setValidTime(config.getValidTime())
                .setDuplicatePolicy(config.getDuplicatePolicy())
                .setInterval(config.getInterval())
                .setCodeType(config.getCodeType())
                .setReturnVerifyCode(config.getReturnVerifyCode())
                .setAutoRetry(config.getAutoRetry());
        try {
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse resp = client.sendSmsVerifyCodeWithOptions(sendSmsVerifyCodeRequest, runtime);
            return resp.getBody().getSuccess();
        } catch (TeaException error) {
            log.error("DefaultAliyunPnsService smsCodeSend 发送短信验证码失败, phoneNumber: {},  errorMessage: {}, recommend: {}",
                    hideBetweenPhone, error.getMessage(), error.getData().get("Recommend"));
        } catch (Exception _error) {
            log.error("DefaultAliyunPnsService smsCodeSend 发送短信验证码异常, phoneNumber: {}",
                    hideBetweenPhone, _error);
        }
        return false;
    }

    @Override
    public boolean checkSmsVerifyCode(String schemeName, String phoneNumber, String verifyCode) {
        AliyunPnsConfig config = getConfig();
        if(StrUtil.isEmpty(phoneNumber)) {
            log.warn("DefaultAliyunPnsService checkSmsVerifyCode 手机号不能为空");
            return false;
        }
        if (!PhoneUtil.isPhone(phoneNumber)) {
            log.warn("DefaultAliyunPnsService checkSmsVerifyCode 手机号格式不正确，手机号:{}", phoneNumber);
            return false;
        }
        CharSequence hideBetweenPhone = PhoneUtil.hideBetween(phoneNumber);
        try {
            com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeRequest checkSmsVerifyCodeRequest = new com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeRequest()
                    .setPhoneNumber(phoneNumber)
                    .setCountryCode(config.getCountryCode())
                    .setSchemeName(schemeName)
                    .setVerifyCode(verifyCode);

            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeResponse resp = client.checkSmsVerifyCodeWithOptions(checkSmsVerifyCodeRequest, runtime);

            CheckSmsVerifyCodeResponseBody body = resp.getBody();
            String verifyResult = body.getModel().getVerifyResult();
            return StrUtil.equals("PASS", verifyResult);
        } catch (TeaException error) {
            log.error("DefaultAliyunPnsService checkSmsVerifyCode 验证短信验证码失败, phoneNumber: {}, verifyCode: {}, errorMessage: {}, recommend: {}",
                    hideBetweenPhone, verifyCode, error.getMessage(), error.getData().get("Recommend"));
        } catch (Exception _error) {
            log.error("DefaultAliyunPnsService checkSmsVerifyCode 验证短信验证码异常, phoneNumber: {}, verifyCode: {}",
                    hideBetweenPhone, verifyCode, _error);
        }
        return false;
    }

    private AliyunPnsConfig getConfig() {
        AliyunPnsConfig config = configProvider.getConfig();
        if (config == null) {
            throw new IllegalStateException("AliyunPnsConfigProvider 返回的配置不能为空");
        }
        return config;
    }

    @Override
    public String getMobile(String accessToken) {
        if(StrUtil.isEmpty(accessToken)) {
            log.warn("DefaultAliyunPnsService getMobile accessToken不能为空");
            throw new IllegalArgumentException("accessToken不能为空");
        }
        com.aliyun.dypnsapi20170525.models.GetMobileRequest getMobileRequest = new com.aliyun.dypnsapi20170525.models.GetMobileRequest()
                .setAccessToken(accessToken);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            com.aliyun.dypnsapi20170525.models.GetMobileResponse resp = client.getMobileWithOptions(getMobileRequest, runtime);
            if (resp == null || resp.getBody() == null || resp.getBody().getGetMobileResultDTO() == null) {
                log.error("LoginByPnsController.queryMobileByToken 一键登录取号异常, 返回结果为空, accessToken={}", accessToken);
                return null;
            }
            String mobile = resp.getBody().getGetMobileResultDTO().getMobile();
            if (StrUtil.isBlank(mobile)) {
                log.error("LoginByPnsController.queryMobileByToken 一键登录取号异常, 手机号为空, accessToken={}", accessToken);
                return null;
            }
            return mobile;
        } catch (TeaException error) {
            log.error("LoginByPnsController.queryMobileByToken TeaException 一键登录取号异常, accessToken={}, message={}", accessToken, error.getMessage());
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            log.error("LoginByPnsController.queryMobileByToken Exception 一键登录取号异常, accessToken={}, message={}", accessToken, error.getMessage());
        }
        return null;
    }
}
