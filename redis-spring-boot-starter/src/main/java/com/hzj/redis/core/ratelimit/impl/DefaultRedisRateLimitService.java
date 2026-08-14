package com.hzj.redis.core.ratelimit.impl;

import com.hzj.redis.core.ratelimit.RedisIpRateLimiter;
import com.hzj.redis.core.ratelimit.RedisRateLimitService;
import com.hzj.redis.core.ratelimit.RedisUserIdRateLimiter;
import com.hzj.redis.core.ratelimit.entity.RateLimitResult;
import com.hzj.redis.provider.ratelimit.RedisRateLimitConfigProvider;
import com.hzj.redis.provider.ratelimit.entity.RedisRateLimitConfig;
import org.redisson.api.RedissonClient;

import java.util.Objects;

/**
 * Redis统一限流服务默认实现。
 */
public class DefaultRedisRateLimitService implements RedisRateLimitService {

    private final RedisRateLimitConfigProvider configProvider;

    private final FixedWindowRedisRateLimiter fixedWindowRateLimiter;

    private final SlidingWindowRedisRateLimiter slidingWindowRateLimiter;

    private final RedisIpRateLimiter ipRateLimiter;

    private final RedisUserIdRateLimiter userIdRateLimiter;

    /**
     * 创建Redis统一限流服务。
     *
     * @param client Redisson客户端
     * @param configProvider 限流配置提供者
     */
    public DefaultRedisRateLimitService(RedissonClient client,
                                        RedisRateLimitConfigProvider configProvider) {
        this(client,
                configProvider,
                new DefaultRedisIpRateLimiter(client, configProvider),
                new DefaultRedisUserIdRateLimiter(client, configProvider));
    }

    /**
     * 创建Redis统一限流服务。
     *
     * @param client Redisson客户端
     * @param configProvider 限流配置提供者
     * @param ipRateLimiter IP限流器
     * @param userIdRateLimiter 用户ID限流器
     */
    public DefaultRedisRateLimitService(RedissonClient client,
                                        RedisRateLimitConfigProvider configProvider,
                                        RedisIpRateLimiter ipRateLimiter,
                                        RedisUserIdRateLimiter userIdRateLimiter) {
        this.configProvider = Objects.requireNonNull(configProvider, "RedisRateLimitConfigProvider 不能为空");
        this.fixedWindowRateLimiter = new FixedWindowRedisRateLimiter(client, configProvider);
        this.slidingWindowRateLimiter = new SlidingWindowRedisRateLimiter(client, configProvider);
        this.ipRateLimiter = Objects.requireNonNull(ipRateLimiter, "RedisIpRateLimiter 不能为空");
        this.userIdRateLimiter = Objects.requireNonNull(userIdRateLimiter, "RedisUserIdRateLimiter 不能为空");
    }

    /**
     * 按当前动态配置申请一次配额。
     *
     * @param key 业务限流key
     * @return 限流结果
     */
    @Override
    public RateLimitResult tryAcquire(String key) {
        return tryAcquire(key, 1);
    }

    /**
     * 按当前动态配置申请指定配额。
     *
     * @param key 业务限流key
     * @param permits 请求配额数量
     * @return 限流结果
     */
    @Override
    public RateLimitResult tryAcquire(String key, long permits) {
        RedisRateLimitConfig config = requireConfig();
        return switch (config.getAlgorithm()) {
            case FIXED_WINDOW -> fixedWindowRateLimiter.tryAcquire(key, permits, config);
            case SLIDING_WINDOW -> slidingWindowRateLimiter.tryAcquire(key, permits, config);
        };
    }

    /**
     * 按用户ID申请一次配额。
     *
     * @param userId 用户ID
     * @return 限流结果
     */
    @Override
    public RateLimitResult tryAcquireByUserId(String userId) {
        return tryAcquireByUserId(userId, 1);
    }

    /**
     * 按用户ID申请指定配额。
     *
     * @param userId 用户ID
     * @param permits 请求配额数量
     * @return 限流结果
     */
    @Override
    public RateLimitResult tryAcquireByUserId(String userId, long permits) {
        return userIdRateLimiter.tryAcquire(userId, permits);
    }

    /**
     * 按IP地址申请一次配额。
     *
     * @param ipAddress IP地址
     * @return 限流结果
     */
    @Override
    public RateLimitResult tryAcquireByIp(String ipAddress) {
        return tryAcquireByIp(ipAddress, 1);
    }

    /**
     * 按IP地址申请指定配额。
     *
     * @param ipAddress IP地址
     * @param permits 请求配额数量
     * @return 限流结果
     */
    @Override
    public RateLimitResult tryAcquireByIp(String ipAddress, long permits) {
        return ipRateLimiter.tryAcquire(ipAddress, permits);
    }

    private RedisRateLimitConfig requireConfig() {
        RedisRateLimitConfig config = configProvider.getConfig();
        if (config == null) {
            throw new IllegalStateException("Redis限流配置不能为空");
        }
        config.getWindowMillis();
        return config;
    }

}
