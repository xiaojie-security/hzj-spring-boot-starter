package com.hzj.common.provider;

/**
 * 启动期静态配置提供者。
 *
 * <p>实现应在启动阶段加载配置，服务创建后不再刷新配置。适用于应用身份、密钥、证书和其他客户端初始化参数。</p>
 *
 * @param <T> 配置类型
 */
public interface StaticConfigProvider<T> extends ConfigProvider<T> {
}
