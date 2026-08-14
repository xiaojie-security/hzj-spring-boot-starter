package com.hzj.redis.core.ratelimit.impl;

import com.hzj.redis.core.ratelimit.RedisRateLimitService;
import com.hzj.redis.core.ratelimit.entity.RateLimitResult;
import com.hzj.redis.provider.ratelimit.RedisRateLimitConfigProvider;
import com.hzj.redis.provider.ratelimit.entity.RedisRateLimitConfig;
import org.redisson.api.RedissonClient;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Redis统一限流服务默认实现。
 */
public class DefaultRedisRateLimitService implements RedisRateLimitService {

    private static final String USER_KEY_PREFIX = "user:";

    private static final String IP_KEY_PREFIX = "ip:";

    private final RedisRateLimitConfigProvider configProvider;

    private final FixedWindowRedisRateLimiter fixedWindowRateLimiter;

    private final SlidingWindowRedisRateLimiter slidingWindowRateLimiter;

    /**
     * 创建Redis统一限流服务。
     *
     * @param client Redisson客户端
     * @param configProvider 限流配置提供者
     */
    public DefaultRedisRateLimitService(RedissonClient client,
                                        RedisRateLimitConfigProvider configProvider) {
        this.configProvider = Objects.requireNonNull(configProvider, "RedisRateLimitConfigProvider 不能为空");
        this.fixedWindowRateLimiter = new FixedWindowRedisRateLimiter(client, configProvider);
        this.slidingWindowRateLimiter = new SlidingWindowRedisRateLimiter(client, configProvider);
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
        validateDimension(userId, "用户ID");
        return tryAcquire(USER_KEY_PREFIX + userId.trim(), permits);
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
        validateDimension(ipAddress, "IP地址");
        return tryAcquire(IP_KEY_PREFIX + ipAddress.trim(), permits);
    }

    private RedisRateLimitConfig requireConfig() {
        RedisRateLimitConfig config = configProvider.getConfig();
        if (config == null) {
            throw new IllegalStateException("Redis限流配置不能为空");
        }
        config.getWindowMillis();
        return config;
    }

    private void validateDimension(String value, String name) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(name + "不能为空");
        }
    }
}
