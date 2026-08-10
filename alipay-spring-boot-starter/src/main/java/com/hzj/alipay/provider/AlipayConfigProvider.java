package com.hzj.alipay.provider;

import com.hzj.alipay.provider.domain.AlipayConfig;

/**
 * 支付宝动态配置提供者。
 */
public interface AlipayConfigProvider {

    /**
     * 获取当前支付宝配置。
     *
     * @return 当前配置
     */
    AlipayConfig getConfig();
}
