package com.hzj.redis.provider.ratelimit.properties;

import com.hzj.redis.provider.ratelimit.enums.RateLimitAlgorithm;
import com.hzj.redis.provider.ratelimit.enums.RateLimitDimension;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 基于 Spring Boot Properties 的 Redis限流默认配置。
 */
@Data
@ConfigurationProperties(prefix = "spring.redis.rate-limit")
public class RedisRateLimitProperties {

    /**
     * 是否启用默认限流服务。
     */
    private boolean enabled = true;

    /**
     * 限流算法。
     */
    private RateLimitAlgorithm algorithm = RateLimitAlgorithm.FIXED_WINDOW;

    /**
     * 已激活的限流维度。
     */
    private Set<RateLimitDimension> enabledDimensions = EnumSet.allOf(RateLimitDimension.class);

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
}
