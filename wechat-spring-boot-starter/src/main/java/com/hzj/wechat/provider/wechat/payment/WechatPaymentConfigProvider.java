package com.hzj.wechat.provider.wechat.payment;

import com.hzj.wechat.provider.ConfigProvider;
import com.hzj.wechat.provider.wechat.payment.entity.WechatPaymentConfig;

/**
 * 微信商户配置提供者。
 * <p>
 * Starter 在运行时通过该接口获取当前生效的商户配置，
 * 调用方可以基于配置文件、数据库、缓存或远程配置中心提供实现。
 *
 * @author YourName
 * @version 1.0
 * @since 2026-07-15
 */
public interface WechatPaymentConfigProvider extends ConfigProvider<WechatPaymentConfig> {

}
