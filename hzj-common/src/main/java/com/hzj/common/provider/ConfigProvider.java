package com.hzj.common.provider;

/**
 * 动态配置提供者。
 *
 * @param <T> 配置类型
 */
public interface ConfigProvider<T> {

    /**
     * 获取当前配置。
     *
     * @return 配置对象
     */
    T getConfig();
}
