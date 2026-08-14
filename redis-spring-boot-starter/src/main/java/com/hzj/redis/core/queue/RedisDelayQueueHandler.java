package com.hzj.redis.core.queue;

/**
 * Redis延迟队列消息处理器。
 *
 * @param <T> 业务载荷类型
 */
@FunctionalInterface
public interface RedisDelayQueueHandler<T> {

    /**
     * 处理到期消息。
     *
     * @param payload 业务载荷
     * @param message 延迟队列消息信封
     * @throws Exception 业务处理异常
     */
    void handle(T payload, RedisDelayQueueMessage message) throws Exception;
}
