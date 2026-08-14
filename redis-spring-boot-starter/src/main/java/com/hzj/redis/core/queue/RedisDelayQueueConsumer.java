package com.hzj.redis.core.queue;

/**
 * Redis延迟队列消费者接口。
 */
public interface RedisDelayQueueConsumer {

    /**
     * 注册主题消费者。
     * <p>
     * 同一个服务实例中同一个主题只能注册一个处理器；多个服务实例可以共同消费同一个主题。
     * </p>
     *
     * @param topic 消息主题
     * @param payloadType 业务载荷类型
     * @param handler 消息处理器
     * @param <T> 业务载荷类型
     */
    <T> void registerConsumer(String topic, Class<T> payloadType,
                              RedisDelayQueueHandler<T> handler);

    /**
     * 移除主题消费者。
     * <p>移除消费者不会删除 Redis 中尚未到期的消息。</p>
     *
     * @param topic 消息主题
     */
    void unregisterConsumer(String topic);
}
