package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.Expose;
import com.hzj.wechat.core.enums.WechatHttpMethod;

/**
 * 微信支付 V3 API 请求基础参数。
 */
public class WechatPaymentApiRequest {

    /**
     * 微信支付 API 主机地址。
     */
    @Expose(serialize = false)
    public String requestHost = "https://api.mch.weixin.qq.com";

    /**
     * 微信支付 API 请求路径。
     */
    @Expose(serialize = false)
    public String requestPath;

    /**
     * 微信支付 API 请求方法。
     */
    @Expose(serialize = false)
    public WechatHttpMethod requestMethod;
}
