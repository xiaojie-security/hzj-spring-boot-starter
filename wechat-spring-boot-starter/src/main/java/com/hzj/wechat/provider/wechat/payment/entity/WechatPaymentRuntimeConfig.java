package com.hzj.wechat.provider.wechat.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信支付运行时业务配置。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WechatPaymentRuntimeConfig {

    /** 微信支付异步通知地址。 */
    private String paymentNotifyUrl;
}
