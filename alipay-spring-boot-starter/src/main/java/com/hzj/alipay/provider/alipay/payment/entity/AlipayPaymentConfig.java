package com.hzj.alipay.provider.alipay.payment.entity;

import com.hzj.alipay.provider.alipay.entity.AlipayBaseConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 支付宝支付配置快照。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AlipayPaymentConfig extends AlipayBaseConfig {

    /** 卖家 ID。 */
    private String sellerId;

    /** 订单有效时间，单位为毫秒。 */
    private Long validityTime;

    /** 支付异步通知地址。 */
    private String paymentNotifyUrl;
}
