package com.hzj.alipay.core.payment.impl;

import com.hzj.alipay.core.payment.AlipayPaymentCallbackService;
import com.hzj.alipay.core.payment.domain.AliPayCallbackResult;
import com.hzj.alipay.provider.alipay.payment.AlipayPaymentConfigProvider;
import com.hzj.alipay.provider.alipay.payment.entity.AlipayPaymentConfig;
import com.hzj.alipay.utils.AlipayCallbackValidateUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 支付宝支付异步通知默认实现。
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultAlipayPaymentCallbackService implements AlipayPaymentCallbackService {
    private static final String SUCCESS_RESPONSE = "success";
    private static final String FAIL_RESPONSE = "fail";
    private static final String TEXT_CONTENT_TYPE = "text/plain";

    private final AlipayPaymentConfigProvider provider;

    /**
     * 验签并解析支付宝支付异步通知。
     *
     * @param request 支付宝异步通知请求
     * @param response 支付宝异步通知响应
     * @return 验签并解析后的支付宝回调结果
     */
    @Override
    public AliPayCallbackResult parseCallback(HttpServletRequest request, HttpServletResponse response) {
        try {
            AlipayPaymentConfig config = getAlipayConfig();
            AliPayCallbackResult callbackResult = verifyCallback(request, config);
            validateCallback(callbackResult, config);
            writeResponse(response, HttpServletResponse.SC_OK, SUCCESS_RESPONSE);
            return callbackResult;
        } catch (IOException e) {
            log.error("DefaultAlipayPaymentCallbackService.parseCallback 回写支付宝支付回调应答失败", e);
            writeFailResponse(response);
            throw new IllegalStateException("处理支付宝支付回调失败", e);
        } catch (RuntimeException e) {
            log.error("DefaultAlipayPaymentCallbackService.parseCallback 解析支付宝支付回调失败", e);
            writeFailResponse(response);
            throw e;
        }
    }

    /**
     * 根据配置模式验证支付宝异步通知签名。
     *
     * @param request 支付宝异步通知请求
     * @param config 支付宝配置
     * @return 验签后的回调结果
     */
    private AliPayCallbackResult verifyCallback(HttpServletRequest request, AlipayPaymentConfig config) {
        if (request == null) {
            throw new IllegalArgumentException("支付宝异步通知请求不能为空");
        }
        AliPayCallbackResult callbackResult = config.isCertificates()
                ? AlipayCallbackValidateUtils.rsaCertCheck(request, config.getAlipayPublicCertPath())
                : AlipayCallbackValidateUtils.rsaCheck(request, config.getPublicKey());
        if (callbackResult == null || callbackResult.isEmpty()) {
            throw new IllegalStateException("支付宝异步通知验签失败");
        }
        return callbackResult;
    }

    /**
     * 校验支付宝通知中的关键业务字段及收款方信息。
     *
     * @param callbackResult 支付宝回调结果
     * @param config 支付宝配置
     */
    private void validateCallback(AliPayCallbackResult callbackResult, AlipayPaymentConfig config) {
        if (!StringUtils.hasText(callbackResult.getOutTradeNo())
                || !StringUtils.hasText(callbackResult.getTradeNo())
                || !StringUtils.hasText(callbackResult.getTotalAmount())
                || !StringUtils.hasText(callbackResult.getTradeStatus())) {
            throw new IllegalStateException("支付宝异步通知缺少订单关键字段");
        }
        if (!Objects.equals(config.getAppId(), callbackResult.getAppId())) {
            throw new IllegalStateException("支付宝异步通知应用ID不匹配");
        }
        if (StringUtils.hasText(config.getSellerId())
                && !Objects.equals(config.getSellerId(), callbackResult.getSellerId())) {
            throw new IllegalStateException("支付宝异步通知卖家ID不匹配");
        }
    }

    /**
     * 获取并校验支付宝配置。
     *
     * @return 支付宝配置
     */
    private AlipayPaymentConfig getAlipayConfig() {
        if (provider == null) {
            throw new IllegalStateException("AlipayPaymentConfigProvider 未初始化");
        }
        AlipayPaymentConfig config = provider.getConfig();
        if (config == null) {
            throw new IllegalStateException("支付宝配置不能为空");
        }
        if (!StringUtils.hasText(config.getAppId())) {
            throw new IllegalStateException("支付宝配置缺少应用ID");
        }
        if (config.isCertificates()) {
            if (!StringUtils.hasText(config.getAlipayPublicCertPath())) {
                throw new IllegalStateException("证书模式缺少支付宝公钥证书路径");
            }
        } else if (!StringUtils.hasText(config.getPublicKey())) {
            throw new IllegalStateException("公钥模式缺少支付宝公钥");
        }
        return config;
    }

    /**
     * 回写支付宝异步通知应答。
     *
     * @param response HTTP响应
     * @param status HTTP状态码
     * @param body 应答内容
     * @throws IOException 回写响应失败
     */
    private void writeResponse(HttpServletResponse response, int status, String body) throws IOException {
        if (response == null) {
            throw new IllegalArgumentException("支付宝异步通知响应不能为空");
        }
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(TEXT_CONTENT_TYPE);
        response.getWriter().write(body);
    }

    /**
     * 回写支付宝失败应答。
     *
     * @param response HTTP响应
     */
    private void writeFailResponse(HttpServletResponse response) {
        if (response == null) {
            return;
        }
        try {
            writeResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, FAIL_RESPONSE);
        } catch (IOException e) {
            log.error("DefaultAlipayPaymentCallbackService.writeFailResponse 回写支付宝失败应答异常", e);
        }
    }
}
