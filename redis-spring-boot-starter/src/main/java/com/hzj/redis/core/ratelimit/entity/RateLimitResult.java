package com.hzj.redis.core.ratelimit.entity;

/**
 * 限流结果。
 *
 * @param allowed 当前请求是否允许通过
 * @param limit 当前窗口最大允许次数
 * @param remaining 当前窗口剩余次数
 * @param retryAfterMillis 被拒绝时建议等待的毫秒数
 * @param key 当前限流key
 */
public record RateLimitResult(boolean allowed, long limit, long remaining,
                              long retryAfterMillis, String key) {
}
