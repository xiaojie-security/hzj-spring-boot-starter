package com.hzj.redis.core.ratelimit.impl;

import com.hzj.redis.core.ratelimit.RedisDimensionRateLimiter;
import com.hzj.redis.core.ratelimit.entity.RedisRateLimitRequest;
import com.hzj.redis.core.ratelimit.entity.RateLimitResult;
import com.hzj.redis.provider.ratelimit.RedisRateLimitConfigProvider;
import com.hzj.redis.provider.ratelimit.entity.RedisRateLimitConfig;
import com.hzj.redis.provider.ratelimit.enums.RateLimitDimension;
import org.redisson.api.RedissonClient;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Redis维度限流器抽象实现。
 * <p>负责维度参数校验和动态激活判断，具体算法委托给固定窗口或滑动窗口实现。</p>
 */
public abstract class AbstractRedisDimensionRateLimiter implements RedisDimensionRateLimiter {

    private final RedisRateLimitConfigProvider configProvider;

    private final FixedWindowRedisRateLimiter fixedWindowRateLimiter;

    private final SlidingWindowRedisRateLimiter slidingWindowRateLimiter;

    /**
     * 创建维度限流器。
     *
     * @param client Redisson客户端
     * @param configProvider 限流配置提供者
     */
    protected AbstractRedisDimensionRateLimiter(RedissonClient client,
                                                RedisRateLimitConfigProvider configProvider) {
        this.configProvider = Objects.requireNonNull(configProvider, "RedisRateLimitConfigProvider 不能为空");
        this.fixedWindowRateLimiter = new FixedWindowRedisRateLimiter(client, configProvider);
        this.slidingWindowRateLimiter = new SlidingWindowRedisRateLimiter(client, configProvider);
    }

    /**
     * 按当前动态配置申请一次配额。
     *
     * @param value 维度值
     * @return 限流结果
     */
    @Override
    public final RateLimitResult tryAcquire(String value) {
        return tryAcquire(value, 1);
    }

    /**
     * 按当前动态配置申请指定配额。
     *
     * @param value 维度值
     * @param permits 请求配额数量
     * @return 限流结果
     */
    @Override
    public final RateLimitResult tryAcquire(String value, long permits) {
        validateValue(value);
        validatePermits(permits);
        RedisRateLimitConfig config = requireConfig();
        if (!config.isDimensionEnabled(dimension())) {
            return disabledResult(value, config);
        }
        return tryAcquireEnabled(value, permits, config);
    }

    /**
     * 根据批量请求执行当前维度限流。
     * <p>关闭当前维度时不要求请求提供该维度值，便于管理器组合执行多个维度。</p>
     *
     * @param request 批量限流请求
     * @return 当前维度限流结果
     */
    @Override
    public final RateLimitResult tryAcquire(RedisRateLimitRequest request) {
        Objects.requireNonNull(request, "批量限流请求不能为空");
        validatePermits(request.permits());
        RedisRateLimitConfig config = requireConfig();
        if (!config.isDimensionEnabled(dimension())) {
            String value = extractValue(request);
            return disabledResult(value == null ? "disabled" : value, config);
        }
        return tryAcquireEnabled(extractValue(request), request.permits(), config);
    }

    private RateLimitResult tryAcquireEnabled(String value, long permits,
                                              RedisRateLimitConfig config) {
        validateValue(value);
        validatePermits(permits);
        String key = dimensionPrefix() + value.trim();
        return switch (config.getAlgorithm()) {
            case FIXED_WINDOW -> fixedWindowRateLimiter.tryAcquire(key, permits, config);
            case SLIDING_WINDOW -> slidingWindowRateLimiter.tryAcquire(key, permits, config);
        };
    }

    private RateLimitResult disabledResult(String value, RedisRateLimitConfig config) {
        String key = dimensionPrefix() + value.trim();
        return new RateLimitResult(true, config.getLimit(), config.getLimit(), 0, key);
    }

    private String extractValue(RedisRateLimitRequest request) {
        return switch (dimension()) {
            case IP -> request.ipAddress();
            case USER_ID -> request.userId();
        };
    }

    private RedisRateLimitConfig requireConfig() {
        RedisRateLimitConfig config = configProvider.getConfig();
        if (config == null) {
            throw new IllegalStateException("Redis限流配置不能为空");
        }
        config.getWindowMillis();
        return config;
    }

    /**
     * 返回当前限流器维度。
     *
     * @return 限流维度
     */
    protected abstract RateLimitDimension dimension();

    /**
     * 返回当前维度的key前缀。
     *
     * @return 维度key前缀
     */
    protected abstract String dimensionPrefix();

    private void validateValue(String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(dimension() + "限流值不能为空");
        }
    }

    private void validatePermits(long permits) {
        if (permits <= 0) {
            throw new IllegalArgumentException("请求配额必须大于0");
        }
    }
}
