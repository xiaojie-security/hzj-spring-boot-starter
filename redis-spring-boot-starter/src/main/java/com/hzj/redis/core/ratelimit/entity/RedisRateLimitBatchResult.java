package com.hzj.redis.core.ratelimit.entity;

import java.util.List;

/**
 * Redis批量限流结果。
 *
 * @param allowed 所有已注册限流器是否均允许通过
 * @param results 各个限流器的执行结果
 */
public record RedisRateLimitBatchResult(boolean allowed, List<RateLimitResult> results) {
}
