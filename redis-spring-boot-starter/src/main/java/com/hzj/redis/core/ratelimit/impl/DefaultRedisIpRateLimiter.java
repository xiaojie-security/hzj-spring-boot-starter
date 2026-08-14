package com.hzj.redis.core.ratelimit.impl;

import com.hzj.redis.core.ratelimit.RedisIpRateLimiter;
import com.hzj.redis.provider.ratelimit.RedisRateLimitConfigProvider;
import com.hzj.redis.provider.ratelimit.enums.RateLimitDimension;
import org.redisson.api.RedissonClient;

/**
 * 基于IP地址的Redis限流器默认实现。
 */
public class DefaultRedisIpRateLimiter extends AbstractRedisDimensionRateLimiter
        implements RedisIpRateLimiter {

    /**
     * 创建IP限流器。
     *
     * @param client Redisson客户端
     * @param configProvider 限流配置提供者
     */
    public DefaultRedisIpRateLimiter(RedissonClient client,
                                     RedisRateLimitConfigProvider configProvider) {
        super(client, configProvider);
    }

    /**
     * 返回IP维度。
     *
     * @return IP维度
     */
    @Override
    protected RateLimitDimension dimension() {
        return RateLimitDimension.IP;
    }

    /**
     * 返回IP key前缀。
     *
     * @return IP key前缀
     */
    @Override
    protected String dimensionPrefix() {
        return "ip:";
    }
}
