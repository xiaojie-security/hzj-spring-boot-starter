package com.hzj.redis.core.lock.impl;

import com.hzj.redis.core.lock.AbstractRedisLockClientManager;
import com.hzj.redis.properties.RedissonProperties;
import org.redisson.api.RedissonClient;

/**
 * 默认 Redis 分布式锁服务。
 */
public class DefaultRedisLockService extends AbstractRedisLockClientManager {

    /**
     * 创建默认分布式锁服务。
     *
     * @param redissonClient Redisson 客户端
     * @param properties     Redisson 配置属性
     */
    public DefaultRedisLockService(RedissonClient redissonClient,
                                   RedissonProperties properties) {
        super(redissonClient, properties);
    }
}
