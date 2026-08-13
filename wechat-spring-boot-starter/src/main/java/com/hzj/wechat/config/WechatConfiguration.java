package com.hzj.wechat.config;

import com.hzj.wechat.properties.WechatAccessProperties;
import com.hzj.wechat.properties.WechatPaymentProperties;
import com.hzj.wechat.properties.WechatQrCodeProperties;
import com.hzj.wechat.properties.WechatTransferProperties;
import com.hzj.wechat.properties.WechatVirtualPaymentProperties;
import com.hzj.wechat.provider.wechat.access.WechatAccessConfigProvider;
import com.hzj.wechat.provider.wechat.access.impl.PropertiesWechatAccessConfigProvider;
import com.hzj.wechat.provider.wechat.payment.WechatPaymentConfigProvider;
import com.hzj.wechat.provider.wechat.payment.impl.PropertiesWechatPaymentConfigProvider;
import com.hzj.wechat.provider.wechat.qrcode.WechatQrCodeConfigProvider;
import com.hzj.wechat.provider.wechat.qrcode.impl.PropertiesWechatQrCodeConfigProvider;
import com.hzj.wechat.provider.wechat.transfer.WechatTransferConfigProvider;
import com.hzj.wechat.provider.wechat.transfer.impl.PropertiesWechatTransferConfigProvider;
import com.hzj.wechat.provider.wechat.virtual.WechatVirtualPaymentConfigProvider;
import com.hzj.wechat.provider.wechat.virtual.impl.PropertiesWechatVirtualPaymentConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 微信支付 Starter 自动装配配置。
 */
@AutoConfiguration
@EnableConfigurationProperties({
        WechatAccessProperties.class,
        WechatPaymentProperties.class,
        WechatQrCodeProperties.class,
        WechatTransferProperties.class,
        WechatVirtualPaymentProperties.class
})
public class WechatConfiguration {

    /**
     * 注册基于配置属性的微信接口调用凭据配置提供者。
     *
     * @param properties 微信接口调用凭据配置属性
     * @return 微信接口调用凭据配置提供者
     */
    @Bean
    @ConditionalOnMissingBean(WechatAccessConfigProvider.class)
    public WechatAccessConfigProvider wechatAccessConfigProvider(WechatAccessProperties properties) {
        return new PropertiesWechatAccessConfigProvider(properties);
    }

    /**
     * 注册基于配置属性的微信支付配置提供者。
     *
     * @param properties 微信支付配置属性
     * @return 微信支付配置提供者
     */
    @Bean
    @ConditionalOnMissingBean(WechatPaymentConfigProvider.class)
    public WechatPaymentConfigProvider wechatPaymentConfigProvider(WechatPaymentProperties properties) {
        return new PropertiesWechatPaymentConfigProvider(properties);
    }

    /**
     * 注册基于配置属性的微信小程序二维码配置提供者。
     *
     * @param properties 微信小程序二维码配置属性
     * @return 微信小程序二维码配置提供者
     */
    @Bean
    @ConditionalOnMissingBean(WechatQrCodeConfigProvider.class)
    public WechatQrCodeConfigProvider wechatQrCodeConfigProvider(WechatQrCodeProperties properties) {
        return new PropertiesWechatQrCodeConfigProvider(properties);
    }

    /**
     * 注册基于配置属性的微信商家转账配置提供者。
     *
     * @param properties 微信商家转账配置属性
     * @return 微信商家转账配置提供者
     */
    @Bean
    @ConditionalOnMissingBean(WechatTransferConfigProvider.class)
    public WechatTransferConfigProvider wechatTransferConfigProvider(WechatTransferProperties properties) {
        return new PropertiesWechatTransferConfigProvider(properties);
    }

    /**
     * 注册基于配置属性的微信虚拟支付配置提供者。
     *
     * @param properties 微信虚拟支付配置属性
     * @return 微信虚拟支付配置提供者
     */
    @Bean
    @ConditionalOnMissingBean(WechatVirtualPaymentConfigProvider.class)
    public WechatVirtualPaymentConfigProvider wechatVirtualPaymentConfigProvider(
            WechatVirtualPaymentProperties properties) {
        return new PropertiesWechatVirtualPaymentConfigProvider(properties);
    }
}
