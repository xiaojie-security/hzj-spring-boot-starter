package com.hzj.redis.provider.ratelimit;

import com.hzj.common.provider.ConfigProvider;
import com.hzj.redis.provider.ratelimit.entity.RedisRateLimitConfig;

/**
 * Redis限流动态配置提供者。
 */
public interface RedisRateLimitConfigProvider extends ConfigProvider<RedisRateLimitConfig> {
}
