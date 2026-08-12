package com.hzj.wechat.core.payment.enums;

/**
 * 微信支付预下单类型。
 */
public enum WechatPaymentPrepayType {

    /**
     * JSAPI/小程序预下单。
     */
    JSAPI("/v3/pay/transactions/jsapi"),

    /**
     * APP 预下单。
     */
    APP("/v3/pay/transactions/app"),

    /**
     * H5 预下单。
     */
    H5("/v3/pay/transactions/h5"),

    /**
     * Native 预下单。
     */
    NATIVE("/v3/pay/transactions/native");

    /**
     * 微信支付 API 请求路径。
     */
    private final String requestPath;

    WechatPaymentPrepayType(String requestPath) {
        this.requestPath = requestPath;
    }

    /**
     * 获取微信支付 API 请求路径。
     *
     * @return 请求路径
     */
    public String getRequestPath() {
        return requestPath;
    }
}
