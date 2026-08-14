package com.hzj.redis.core.ratelimit.impl;

import com.hzj.redis.core.ratelimit.RedisDimensionRateLimiter;
import com.hzj.redis.core.ratelimit.RedisRateLimitManager;
import com.hzj.redis.core.ratelimit.entity.RedisRateLimitBatchResult;
import com.hzj.redis.core.ratelimit.entity.RedisRateLimitRequest;
import com.hzj.redis.core.ratelimit.entity.RateLimitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Redis批量限流管理器默认实现。
 */
public class DefaultRedisRateLimitManager implements RedisRateLimitManager {

    private final List<RedisDimensionRateLimiter> rateLimiters;

    /**
     * 创建批量限流管理器。
     *
     * @param rateLimiters Spring容器中的维度限流器集合
     */
    public DefaultRedisRateLimitManager(List<RedisDimensionRateLimiter> rateLimiters) {
        this.rateLimiters = List.copyOf(
                Objects.requireNonNull(rateLimiters, "限流器集合不能为空"));
    }

    /**
     * 遍历所有已注册维度限流器并汇总结果。
     *
     * @param request 批量限流请求
     * @return 批量限流结果
     */
    @Override
    public RedisRateLimitBatchResult tryAcquire(RedisRateLimitRequest request) {
        Objects.requireNonNull(request, "批量限流请求不能为空");
        List<RateLimitResult> results = new ArrayList<>(rateLimiters.size());
        boolean allowed = true;
        for (RedisDimensionRateLimiter rateLimiter : rateLimiters) {
            RateLimitResult result = Objects.requireNonNull(
                    rateLimiter.tryAcquire(request), "限流器返回结果不能为空");
            results.add(result);
            allowed = allowed && result.allowed();
        }
        return new RedisRateLimitBatchResult(allowed, List.copyOf(results));
    }
}
