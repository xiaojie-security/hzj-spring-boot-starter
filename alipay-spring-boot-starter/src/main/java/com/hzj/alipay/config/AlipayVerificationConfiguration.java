package com.hzj.alipay.config;

import com.hzj.alipay.core.verification.AlipayVerificationService;
import com.hzj.alipay.core.verification.impl.DefaultAlipayVerificationService;
import com.hzj.alipay.provider.alipay.verification.AlipayVerificationConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 支付宝实名信息核验配置。
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "alipay.verification", name = "enable", havingValue = "true", matchIfMissing = true)
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
