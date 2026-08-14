package com.hzj.redis.core.ratelimit;

import com.hzj.redis.core.ratelimit.entity.RateLimitResult;

/**
 * Redis统一限流服务。
 * <p>用户ID和IP仅用于生成隔离key，具体限流算法由动态配置决定。</p>
 */
public interface RedisRateLimitService extends RedisRateLimiter {

    /**
     * 按用户ID尝试通过一次请求。
     *
     * @param userId 用户ID
     * @return 限流结果
     */
    RateLimitResult tryAcquireByUserId(String userId);

    /**
     * 按用户ID申请指定数量的请求配额。
     *
     * @param userId 用户ID
     * @param permits 请求配额数量
     * @return 限流结果
     */
    RateLimitResult tryAcquireByUserId(String userId, long permits);

    /**
     * 按IP地址尝试通过一次请求。
     *
     * @param ipAddress IP地址
     * @return 限流结果
     */
    RateLimitResult tryAcquireByIp(String ipAddress);

    /**
     * 按IP地址申请指定数量的请求配额。
     *
     * @param ipAddress IP地址
     * @param permits 请求配额数量
     * @return 限流结果
     */
    RateLimitResult tryAcquireByIp(String ipAddress, long permits);
}
