package com.hzj.redis.core.ratelimit;

import com.hzj.redis.core.ratelimit.entity.RedisRateLimitRequest;
import com.hzj.redis.core.ratelimit.entity.RateLimitResult;

/**
 * 可参与批量执行的维度限流器。
 */
public interface RedisDimensionRateLimiter extends RedisRateLimiter {

    /**
     * 根据批量限流请求执行当前维度的限流。
     *
     * @param request 批量限流请求
     * @return 当前维度限流结果
     */
    RateLimitResult tryAcquire(RedisRateLimitRequest request);
}
