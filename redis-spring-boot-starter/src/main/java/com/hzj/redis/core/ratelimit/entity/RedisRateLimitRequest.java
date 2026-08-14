package com.hzj.redis.core.ratelimit.entity;

/**
 * Redis批量限流请求。
 *
 * @param userId 用户ID
 * @param ipAddress IP地址
 * @param permits 请求配额数量
 */
public record RedisRateLimitRequest(String userId, String ipAddress, long permits) {

    /**
     * 创建申请一个配额的限流请求。
     *
     * @param userId 用户ID
     * @param ipAddress IP地址
     * @return 限流请求
     */
    public static RedisRateLimitRequest of(String userId, String ipAddress) {
        return new RedisRateLimitRequest(userId, ipAddress, 1);
    }
}
