package com.hzj.alipay.provider.alipay.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付宝支付运行时业务配置。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlipayPaymentRuntimeConfig {

    /** 订单有效时间，单位为毫秒。 */
    private Long validityTime;

    /** 支付异步通知地址。 */
    private String paymentNotifyUrl;
}
