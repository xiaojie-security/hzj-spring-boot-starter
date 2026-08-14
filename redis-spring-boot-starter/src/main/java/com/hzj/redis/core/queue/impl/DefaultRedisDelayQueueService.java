package com.hzj.redis.core.queue.impl;

import org.redisson.api.RedissonClient;

/**
 * Redis延迟队列默认服务实现。
 */
public class DefaultRedisDelayQueueService extends AbstractRedisDelayQueueService {

    /**
     * 创建默认延迟队列服务。
     *
     * @param client Redisson客户端
     */
    public DefaultRedisDelayQueueService(RedissonClient client) {
        super(client);
    }
}
