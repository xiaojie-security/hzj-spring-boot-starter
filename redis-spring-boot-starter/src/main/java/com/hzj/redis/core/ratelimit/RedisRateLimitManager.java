package com.hzj.redis.core.ratelimit;

import com.hzj.redis.core.ratelimit.entity.RedisRateLimitBatchResult;
import com.hzj.redis.core.ratelimit.entity.RedisRateLimitRequest;

/**
 * Redis批量限流管理器。
 * <p>通过 Spring 注入的维度限流器集合，统一遍历执行多维度限流。</p>
 */
public interface RedisRateLimitManager {

    /**
     * 执行批量限流。
     *
     * @param request 批量限流请求
     * @return 批量限流结果
     */
    RedisRateLimitBatchResult tryAcquire(RedisRateLimitRequest request);
}
