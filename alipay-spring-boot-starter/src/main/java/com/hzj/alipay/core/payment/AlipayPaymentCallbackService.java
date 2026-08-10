package com.hzj.alipay.core.payment;

import com.hzj.alipay.core.payment.domain.AliPayCallbackResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 支付宝支付异步通知服务接口。
 * 用于接收支付宝异步通知、验签并解析通知参数。
 */
public interface AlipayPaymentCallbackService {

    /**
     * 解析支付宝支付异步通知。
     * 方法会根据支付宝配置完成验签，并向支付宝回写处理结果。
     *
     * @param request 支付宝异步通知请求
     * @param response 支付宝异步通知响应
     * @return 验签并解析后的支付宝回调结果
     */
    AliPayCallbackResult parseCallback(HttpServletRequest request, HttpServletResponse response);
}
