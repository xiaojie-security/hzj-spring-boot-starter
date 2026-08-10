package com.hzj.alipay.core;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
import com.alipay.api.DefaultAlipayClient;
import com.hzj.alipay.provider.AlipayConfigProvider;

public abstract class AbstractAlipayService {
    private static final String FORMAT = "json";
    private static final String CHARSET = "UTF-8";
    private static final String SIGN_TYPE = "RSA2";

    protected abstract AlipayClient getAlipayClient();

    protected abstract AlipayConfigProvider getAlipayConfigProvider();

    /**
     * 执行支付宝请求。
     *
     * @param request 支付宝请求
     * @param <T>     响应类型
     * @return 支付宝响应
     * @throws AlipayApiException 支付宝接口异常
     */
    protected <T extends AlipayResponse> T execute(AlipayRequest<T> request) throws AlipayApiException {
        if (useCertificateMode()) {
            return getAlipayClient().certificateExecute(request);
        }
        return getAlipayClient().execute(request);
    }

    /**
     * 执行带访问令牌的支付宝请求。
     *
     * @param request     支付宝请求
     * @param accessToken 访问令牌
     * @param <T>         响应类型
     * @return 支付宝响应
     * @throws AlipayApiException 支付宝接口异常
     */
    protected <T extends AlipayResponse> T execute(AlipayRequest<T> request, String accessToken) throws AlipayApiException {
        if (useCertificateMode()) {
            return getAlipayClient().certificateExecute(request, accessToken);
        }
        return getAlipayClient().execute(request, accessToken);
    }

    /**
     * 是否启用证书模式。
     *
     * @return true-启用证书模式，false-公钥模式
     */
    protected boolean useCertificateMode() {
        return getCurrentConfig().isCertificates();
    }

    protected com.hzj.alipay.provider.domain.AlipayConfig getCurrentConfig() {
        AlipayConfigProvider provider = getAlipayConfigProvider();
        if (provider == null) {
            throw new IllegalStateException("AlipayConfigProvider 未初始化");
        }
        com.hzj.alipay.provider.domain.AlipayConfig config = provider.getConfig();
        if (config == null) {
            throw new IllegalStateException("AlipayConfigProvider 返回的配置不能为空");
        }
        return config;
    }

    protected AlipayClient createAlipayClient() {
        com.hzj.alipay.provider.domain.AlipayConfig config = getCurrentConfig();
        com.alipay.api.AlipayConfig alipayConfig = new com.alipay.api.AlipayConfig();
        alipayConfig.setServerUrl(config.getGateWay());
        alipayConfig.setAppId(config.getAppId());
        alipayConfig.setFormat(FORMAT);
        alipayConfig.setPrivateKey(config.getPrivateKey());
        if (config.isCertificates()) {
            alipayConfig.setAppCertPath(config.getAppCertPath());
            alipayConfig.setAlipayPublicCertPath(config.getAlipayPublicCertPath());
            alipayConfig.setRootCertPath(config.getRootCertPath());
        }
        alipayConfig.setAlipayPublicKey(config.getPublicKey());
        alipayConfig.setCharset(CHARSET);
        alipayConfig.setSignType(SIGN_TYPE);
        try {
            return new DefaultAlipayClient(alipayConfig);
        } catch (AlipayApiException e) {
            throw new IllegalStateException("支付宝客户端创建失败", e);
        }
    }
}
