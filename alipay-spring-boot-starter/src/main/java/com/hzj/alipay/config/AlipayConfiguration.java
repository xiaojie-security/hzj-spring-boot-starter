package com.hzj.alipay.config;

import com.hzj.alipay.properties.AlipayProperties;
import com.hzj.alipay.provider.alipay.oauth2.AlipayOAuth2ConfigProvider;
import com.hzj.alipay.provider.alipay.oauth2.impl.PropertiesAlipayOAuth2ConfigProvider;
import com.hzj.alipay.provider.alipay.payment.AlipayPaymentConfigProvider;
import com.hzj.alipay.provider.alipay.payment.impl.PropertiesAlipayPaymentConfigProvider;
import com.hzj.alipay.provider.alipay.transfer.AlipayTransferConfigProvider;
import com.hzj.alipay.provider.alipay.transfer.impl.PropertiesAlipayTransferConfigProvider;
import com.hzj.alipay.provider.alipay.verification.AlipayVerificationConfigProvider;
import com.hzj.alipay.provider.alipay.verification.impl.PropertiesAlipayVerificationConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 支付宝基础配置入口。
 * 仅负责启用配置属性绑定，具体客户端装配由各子配置类负责。
 */
@AutoConfiguration
@EnableConfigurationProperties(AlipayProperties.class)
public class AlipayConfiguration {

    @Bean
    @ConditionalOnMissingBean(AlipayOAuth2ConfigProvider.class)
    public AlipayOAuth2ConfigProvider alipayOAuth2ConfigProvider(AlipayProperties alipayProperties) {
        return new PropertiesAlipayOAuth2ConfigProvider(alipayProperties);
    }

    @Bean
    @ConditionalOnMissingBean(AlipayPaymentConfigProvider.class)
    public AlipayPaymentConfigProvider alipayPaymentConfigProvider(AlipayProperties alipayProperties) {
        return new PropertiesAlipayPaymentConfigProvider(alipayProperties);
    }

    @Bean
    @ConditionalOnMissingBean(AlipayTransferConfigProvider.class)
    public AlipayTransferConfigProvider alipayTransferConfigProvider(AlipayProperties alipayProperties) {
        return new PropertiesAlipayTransferConfigProvider(alipayProperties);
    }

    @Bean
    @ConditionalOnMissingBean(AlipayVerificationConfigProvider.class)
    public AlipayVerificationConfigProvider alipayVerificationConfigProvider(AlipayProperties alipayProperties) {
        return new PropertiesAlipayVerificationConfigProvider(alipayProperties);
    }
}
