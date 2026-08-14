package com.hzj.redis.provider.ratelimit.enums;

/**
 * Redis限流算法。
 */
public enum RateLimitAlgorithm {

    /**
     * 固定窗口限流。
     */
    FIXED_WINDOW,

    /**
     * 滑动窗口限流。
     */
    SLIDING_WINDOW
}
