package com.hzj.alipay.config;

import com.hzj.alipay.core.verification.AlipayVerificationService;
import com.hzj.alipay.core.verification.impl.DefaultAlipayVerificationService;
import com.hzj.alipay.provider.alipay.verification.AlipayVerificationConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 支付宝实名信息核验自动装配配置。
 * <p>
 * 仅当使用方提供了 {@link AlipayVerificationConfigProvider} Bean 时才会装配。
 */
@AutoConfiguration
@ConditionalOnBean(AlipayVerificationConfigProvider.class)
public class AlipayVerificationConfiguration {

    /**
     * 注册支付宝核验服务。
     *
     * @param provider 支付宝动态配置提供者
     * @return 核验服务
     */
    @Bean
    @ConditionalOnMissingBean(AlipayVerificationService.class)
    public AlipayVerificationService alipayVerificationService(AlipayVerificationConfigProvider provider) {
        return new DefaultAlipayVerificationService(provider);
    }
}
