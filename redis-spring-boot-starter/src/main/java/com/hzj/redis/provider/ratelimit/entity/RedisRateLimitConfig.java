package com.hzj.redis.provider.ratelimit.entity;

import com.hzj.redis.provider.ratelimit.enums.RateLimitAlgorithm;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * Redis限流动态配置。
 */
@Data
public class RedisRateLimitConfig {

    /**
     * 限流算法。
     */
    private RateLimitAlgorithm algorithm = RateLimitAlgorithm.FIXED_WINDOW;

    /**
     * 窗口内允许通过的请求数。
     */
    private long limit = 100;

    /**
     * 时间窗口大小。
     */
    private long window = 1;

    /**
     * 时间窗口单位。
     */
    private TimeUnit timeUnit = TimeUnit.MINUTES;

    /**
     * Redis限流key前缀。
     */
    private String keyPrefix = "redis:rate-limit:";

    /**
     * 校验配置并返回窗口毫秒数。
     *
     * @return 窗口毫秒数
     */
    public long getWindowMillis() {
        if (algorithm == null) {
            throw new IllegalArgumentException("限流算法不能为空");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("限流次数必须大于0");
        }
        if (window <= 0) {
            throw new IllegalArgumentException("限流窗口必须大于0");
        }
        if (timeUnit == null) {
            throw new IllegalArgumentException("限流时间单位不能为空");
        }
        if (keyPrefix == null || keyPrefix.isBlank()) {
            throw new IllegalArgumentException("限流key前缀不能为空");
        }
        long windowMillis = timeUnit.toMillis(window);
        if (windowMillis <= 0) {
            throw new IllegalArgumentException("限流窗口毫秒数必须大于0");
        }
        return windowMillis;
    }
}
