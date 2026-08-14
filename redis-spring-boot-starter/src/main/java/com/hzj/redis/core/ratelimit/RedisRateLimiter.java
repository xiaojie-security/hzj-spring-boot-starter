package com.hzj.redis.core.ratelimit;

import com.hzj.redis.core.ratelimit.entity.RateLimitResult;

/**
 * Redis限流顶层接口。
 */
public interface RedisRateLimiter {

    /**
     * 尝试通过一次请求。
     *
     * @param key 业务限流key
     * @return 限流结果
     */
    RateLimitResult tryAcquire(String key);

    /**
     * 尝试申请指定数量的请求配额。
     *
     * @param key 业务限流key
     * @param permits 请求配额数量
     * @return 限流结果
     */
    RateLimitResult tryAcquire(String key, long permits);
}
