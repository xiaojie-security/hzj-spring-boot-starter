package com.hzj.redis.provider.ratelimit.enums;

/**
 * Redis限流维度。
 */
public enum RateLimitDimension {

    /**
     * 用户ID维度。
     */
    USER_ID,

    /**
     * IP地址维度。
     */
    IP
}
