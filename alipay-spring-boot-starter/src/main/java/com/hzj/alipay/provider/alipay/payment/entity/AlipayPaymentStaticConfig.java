package com.hzj.alipay.provider.alipay.payment.entity;

import com.hzj.alipay.provider.alipay.entity.AlipayBaseConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 支付宝支付启动期静态配置。
 *
 * <p>支付宝客户端和商户身份相关配置在启动时读取，应用运行期间不支持动态刷新。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AlipayPaymentStaticConfig extends AlipayBaseConfig {

    /** 卖家 ID。 */
    private String sellerId;
}
