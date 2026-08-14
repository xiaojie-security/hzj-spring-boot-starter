package com.hzj.redis.core.ratelimit.impl;

import com.hzj.redis.core.ratelimit.RedisRateLimiter;
import com.hzj.redis.core.ratelimit.entity.RateLimitResult;
import com.hzj.redis.provider.ratelimit.RedisRateLimitConfigProvider;
import com.hzj.redis.provider.ratelimit.entity.RedisRateLimitConfig;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * Redis限流算法抽象实现。
 * <p>每次请求都会读取配置提供者的最新配置，支持动态调整限流参数。</p>
 */
@Slf4j
public abstract class AbstractRedisRateLimiter implements RedisRateLimiter {

    /**
     * 用户ID限流key前缀。
     */
    protected static final String USER_KEY_PREFIX = "user:";

    /**
     * IP限流key前缀。
     */
    protected static final String IP_KEY_PREFIX = "ip:";

    protected final RedissonClient client;

    protected final RedisRateLimitConfigProvider configProvider;

    /**
     * 创建限流算法实现。
     *
     * @param client Redisson客户端
     * @param configProvider 限流配置提供者
     */
    protected AbstractRedisRateLimiter(RedissonClient client,
                                       RedisRateLimitConfigProvider configProvider) {
        this.client = Objects.requireNonNull(client, "RedissonClient 不能为空");
        this.configProvider = Objects.requireNonNull(configProvider, "RedisRateLimitConfigProvider 不能为空");
    }

    /**
     * 使用当前动态配置申请一次配额。
     *
     * @param key 业务限流key
     * @return 限流结果
     */
    @Override
    public final RateLimitResult tryAcquire(String key) {
        return tryAcquire(key, 1);
    }

    /**
     * 使用当前动态配置申请指定配额。
     *
     * @param key 业务限流key
     * @param permits 请求配额数量
     * @return 限流结果
     */
    @Override
    public final RateLimitResult tryAcquire(String key, long permits) {
        RedisRateLimitConfig config = requireConfig();
        return tryAcquire(key, permits, config);
    }

    /**
     * 使用指定配置申请配额，供统一服务保证算法选择和执行使用同一份配置快照。
     *
     * @param key 业务限流key
     * @param permits 请求配额数量
     * @param config 限流配置
     * @return 限流结果
     */
    public final RateLimitResult tryAcquire(String key, long permits, RedisRateLimitConfig config) {
        validateRequest(key, permits);
        Objects.requireNonNull(config, "限流配置不能为空");
        long windowMillis = config.getWindowMillis();
        if (permits > config.getLimit()) {
            return new RateLimitResult(false, config.getLimit(), 0, windowMillis,
                    buildKey(key, config));
        }
        return doAcquire(buildKey(key, config), config, permits, windowMillis);
    }

    /**
     * 执行具体算法。
     *
     * @param key 完整Redis限流key
     * @param config 限流配置
     * @param permits 请求配额数量
     * @param windowMillis 窗口毫秒数
     * @return 限流结果
     */
    protected abstract RateLimitResult doAcquire(String key, RedisRateLimitConfig config,
                                                 long permits, long windowMillis);

    /**
     * 返回当前算法的key片段。
     *
     * @return 算法key片段
     */
    protected abstract String algorithmKey();

    /**
     * 获取当前动态配置。
     *
     * @return 当前配置
     */
    protected RedisRateLimitConfig requireConfig() {
        RedisRateLimitConfig config = configProvider.getConfig();
        if (config == null) {
            log.error("AbstractRedisRateLimiter.requireConfig Redis限流配置不能为空");
            throw new IllegalStateException("Redis限流配置不能为空");
        }
        config.getWindowMillis();
        return config;
    }

    /**
     * 解析Lua脚本返回结果。
     *
     * @param key 完整Redis限流key
     * @param config 限流配置
     * @param values Lua返回值
     * @return 限流结果
     */
    protected RateLimitResult toResult(String key, RedisRateLimitConfig config,
                                       List<?> values) {
        if (values == null || values.size() < 3) {
            throw new IllegalStateException("Redis限流脚本返回结果非法");
        }
        long allowed = toLong(values.get(0));
        long remaining = Math.max(0, toLong(values.get(1)));
        long retryAfterMillis = Math.max(0, toLong(values.get(2)));
        if (allowed == 1) {
            retryAfterMillis = 0;
        }
        return new RateLimitResult(allowed == 1, config.getLimit(), remaining,
                retryAfterMillis, key);
    }

    /**
     * 构建算法隔离的Redis key。
     *
     * @param key 业务限流key
     * @param config 限流配置
     * @return Redis key
     */
    protected String buildKey(String key, RedisRateLimitConfig config) {
        String prefix = config.getKeyPrefix().trim();
        if (!prefix.endsWith(":")) {
            prefix += ":";
        }
        return prefix + algorithmKey() + ":" + key.trim();
    }

    private void validateRequest(String key, long permits) {
        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException("限流key不能为空");
        }
        if (permits <= 0) {
            throw new IllegalArgumentException("请求配额必须大于0");
        }
    }

    private long toLong(Object value) {
        if (!(value instanceof Number number)) {
            throw new IllegalStateException("Redis限流脚本返回值不是数字: " + value);
        }
        return number.longValue();
    }
}
