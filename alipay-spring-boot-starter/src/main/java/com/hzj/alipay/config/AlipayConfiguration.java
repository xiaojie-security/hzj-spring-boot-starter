package com.hzj.alipay.config;

import com.hzj.alipay.properties.AlipayProperties;
import com.hzj.alipay.provider.AlipayConfigProvider;
import com.hzj.alipay.provider.impl.PropertiesAlipayConfigProvider;
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
    @ConditionalOnMissingBean(AlipayConfigProvider.class)
    public AlipayConfigProvider alipayConfigProvider(AlipayProperties alipayProperties) {
        return new PropertiesAlipayConfigProvider(alipayProperties);
    }
}
