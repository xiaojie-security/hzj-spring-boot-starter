package com.hzj.redis.core.cache;

import java.util.concurrent.TimeUnit;

/**
 * Redis 临时凭证服务。
 * <p>
 * 用于生成带有效期的一次性凭证，并在业务消费成功后立即使凭证失效。
 * </p>
 */
public interface RedisCredentialService {

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
     * 消费一次性凭证。
     *
     * @param key 业务隔离键
     * @param credential 一次性凭证
     * @return 是否消费成功
     */
    boolean consumeOnceCredential(String key, String credential);
}
