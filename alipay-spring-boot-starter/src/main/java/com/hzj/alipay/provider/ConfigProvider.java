package com.hzj.alipay.provider;

/**
 * 动态配置提供者统一入口。
 *
 * @param <T> 配置类型
 */
public interface ConfigProvider<T> {

    /**
     * 获取当前生效配置。
     *
     * @return 当前配置
     */
    T getConfig();
}
