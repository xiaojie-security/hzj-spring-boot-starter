package com.hzj.alipay.utils;


import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.hzj.alipay.core.payment.domain.AliPayCallbackResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;


@Slf4j
public final class AlipayCallbackValidateUtils {

    public static final String CHARSET = "UTF-8";
    public static final String SIGN_TYPE = "RSA2";

    /**
     * 验证支付宝异步回调签名（证书模式）。
     *
     * @param request HTTP请求对象，包含支付宝异步通知参数
     * @param alipayPublicCertPath 支付宝公钥证书路径
     * @return 验证成功返回参数字典；验证失败或异常返回null
     */
    public static AliPayCallbackResult rsaCertCheck(HttpServletRequest request, String alipayPublicCertPath)  {
        try {
            Map<String, String> params = extractParams(request);
            boolean signVerified = AlipaySignature.rsaCertCheckV1(params, alipayPublicCertPath, CHARSET, SIGN_TYPE);  //调用SDK验证签名
            if (!signVerified) {
                log.error("AlipayUtils rsaCertCheck 验证支付宝异步回调签名失败");
                return null;
            }
            return new AliPayCallbackResult(params);
        } catch (AlipayApiException e) {
            log.error("AlipayUtils rsaCertCheck 验证支付宝异步回调签名异常, alipayPublicCertPath={}", alipayPublicCertPath, e);
            return null;
        }
    }

    /**
     * 验证支付宝异步回调签名（非证书模式）。
     *
     * @param request HTTP请求对象，包含支付宝异步通知参数
     * @param alipayPublicKey 支付宝公钥
     * @return 验证成功返回参数字典；验证失败或异常返回null
     */
    public static AliPayCallbackResult rsaCheck(HttpServletRequest request, String alipayPublicKey)  {
        try {
            Map<String, String> params = extractParams(request);
            boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayPublicKey, CHARSET, SIGN_TYPE);
            if (!signVerified) {
                log.error("AlipayUtils rsaCheck 验证支付宝异步回调签名失败");
                return null;
            }
            return new AliPayCallbackResult(params);
        } catch (AlipayApiException e) {
            log.error("AlipayUtils rsaCheck 验证支付宝异步回调签名异常, alipayPublicKey={}", alipayPublicKey, e);
            return null;
        }
    }

    /**
     * 提取回调请求参数。
     *
     * @param request HTTP请求对象
     * @return 参数字典
     */
    private static Map<String, String> extractParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 出现乱码则打开
            // valueStr = new String(valueStr.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            params.put(name, valueStr);
        }
        return params;
    }

}
