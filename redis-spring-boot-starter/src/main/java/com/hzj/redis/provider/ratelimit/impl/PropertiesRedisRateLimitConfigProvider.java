package com.hzj.redis.provider.ratelimit.impl;

import com.hzj.redis.provider.ratelimit.RedisRateLimitConfigProvider;
import com.hzj.redis.provider.ratelimit.entity.RedisRateLimitConfig;
import com.hzj.redis.provider.ratelimit.properties.RedisRateLimitProperties;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring Boot Properties 的 Redis限流配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesRedisRateLimitConfigProvider implements RedisRateLimitConfigProvider {

    private final RedisRateLimitProperties properties;

    /**
     * 获取当前限流配置。
     *
     * @return 当前限流配置
     */
    @Override
    public RedisRateLimitConfig getConfig() {
        RedisRateLimitConfig config = new RedisRateLimitConfig();
        config.setAlgorithm(properties.getAlgorithm());
        config.setLimit(properties.getLimit());
        config.setWindow(properties.getWindow());
        config.setTimeUnit(properties.getTimeUnit());
        config.setKeyPrefix(properties.getKeyPrefix());
        return config;
    }
}
