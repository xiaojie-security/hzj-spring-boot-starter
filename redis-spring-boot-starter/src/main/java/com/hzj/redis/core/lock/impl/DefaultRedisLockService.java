package com.hzj.redis.core.lock.impl;

import com.hzj.redis.core.lock.AbstractRedisLockClientManager;
import com.hzj.redis.provider.lock.DistributedLockConfigProvider;
import org.redisson.api.RedissonClient;

/**
 * 默认 Redis 分布式锁服务。
 */
public class DefaultRedisLockService extends AbstractRedisLockClientManager {

    /**
     * 创建默认分布式锁服务。
     *
     * @param redissonClient Redisson 客户端
     * @param configProvider 分布式锁配置提供者
     */
    public DefaultRedisLockService(RedissonClient redissonClient,
                                   DistributedLockConfigProvider configProvider) {
        super(redissonClient, configProvider);
    }
}
