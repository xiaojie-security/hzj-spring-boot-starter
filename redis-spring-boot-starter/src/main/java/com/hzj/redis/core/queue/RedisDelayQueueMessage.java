package com.hzj.redis.core.queue;

/**
 * Redis延迟队列统一消息信封。
 *
 * @param messageId 消息唯一标识
 * @param topic 消息主题
 * @param payload 业务载荷
 */
public record RedisDelayQueueMessage(String messageId, String topic, Object payload) {
}
