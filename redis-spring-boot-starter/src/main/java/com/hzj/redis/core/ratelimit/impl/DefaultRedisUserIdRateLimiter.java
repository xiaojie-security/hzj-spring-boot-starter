package com.hzj.redis.core.ratelimit.impl;

import com.hzj.redis.core.ratelimit.RedisUserIdRateLimiter;
import com.hzj.redis.provider.ratelimit.RedisRateLimitConfigProvider;
import com.hzj.redis.provider.ratelimit.enums.RateLimitDimension;
import org.redisson.api.RedissonClient;

/**
 * 基于用户ID的Redis限流器默认实现。
 */
public class DefaultRedisUserIdRateLimiter extends AbstractRedisDimensionRateLimiter
        implements RedisUserIdRateLimiter {

    /**
     * 创建用户ID限流器。
     *
     * @param client Redisson客户端
     * @param configProvider 限流配置提供者
     */
    public DefaultRedisUserIdRateLimiter(RedissonClient client,
                                         RedisRateLimitConfigProvider configProvider) {
        super(client, configProvider);
    }

    /**
     * 返回用户ID维度。
     *
     * @return 用户ID维度
     */
    @Override
    protected RateLimitDimension dimension() {
        return RateLimitDimension.USER_ID;
    }

    /**
     * 返回用户ID key前缀。
     *
     * @return 用户ID key前缀
     */
    @Override
    protected String dimensionPrefix() {
        return "user:";
    }
}
