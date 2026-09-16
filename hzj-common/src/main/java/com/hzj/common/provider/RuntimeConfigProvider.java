package com.hzj.common.provider;

/**
 * 运行时配置提供者。
 *
 * <p>实现应在每次获取配置时返回当前生效的配置，允许配置中心、缓存或数据库在运行时刷新配置。</p>
 *
 * @param <T> 配置类型
 */
public interface RuntimeConfigProvider<T> extends ConfigProvider<T> {
}
