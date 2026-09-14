package com.hzj.alipay.config;

import com.hzj.alipay.core.transfer.AlipayTransferService;
import com.hzj.alipay.core.transfer.impl.DefaultAlipayTransferService;
import com.hzj.alipay.provider.alipay.transfer.AlipayTransferConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 支付宝转账自动装配配置。
 * <p>
 * 仅当使用方提供了 {@link AlipayTransferConfigProvider} Bean 时才会装配。
 */
@AutoConfiguration
@ConditionalOnBean(AlipayTransferConfigProvider.class)
public class AlipayTransferConfiguration {


    /**
     * 装配支付宝转账服务。
     *
     * @return 转账服务
     */
    @Bean
    @ConditionalOnMissingBean(AlipayTransferService.class)
    public AlipayTransferService alipayTransferService(AlipayTransferConfigProvider provider) {
        return new DefaultAlipayTransferService(provider);
    }
}
