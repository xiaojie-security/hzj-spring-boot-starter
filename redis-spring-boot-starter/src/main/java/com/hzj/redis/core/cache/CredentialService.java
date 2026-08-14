package com.hzj.redis.core.cache;

import java.util.concurrent.TimeUnit;

/**
 * Redis 临时凭证服务。
 * <p>
 * 用于生成带有效期的一次性凭证，并在业务消费成功后立即使凭证失效。
 * </p>
 */
public interface CredentialService {

    /**
     * 获取一次性凭证。
     *
     * @param key 业务隔离键
     * @param ttl 凭证有效时间
     * @param timeUnit 时间单位
     * @return 一次性凭证
     */
    String getOnceCredential(String key, long ttl, TimeUnit timeUnit);

    /**
     * 获取一次性凭证并存储凭证载体。
     *
     * @param key 业务隔离键
     * @param carrier 凭证载体
     * @param ttl 凭证有效时间
     * @param timeUnit 时间单位
     * @return 一次性凭证
     */
    String getOnceCredential(String key, Object carrier, long ttl, TimeUnit timeUnit);

    /**
     * 消费一次性凭证。
     *
     * @param key 业务隔离键
     * @param credential 一次性凭证
     * @return 是否消费成功
     */
    boolean consumeOnceCredential(String key, String credential);

    /**
     * 消费带载体的一次性凭证并返回凭证载体。
     *
     * @param key 业务隔离键
     * @param credential 一次性凭证
     * @param carrierType 凭证载体类型
     * @param <T> 凭证载体类型
     * @return 凭证载体；凭证不存在、已过期或已消费时返回 null
     */
    <T> T consumeOnceCredential(String key, String credential, Class<T> carrierType);
}
