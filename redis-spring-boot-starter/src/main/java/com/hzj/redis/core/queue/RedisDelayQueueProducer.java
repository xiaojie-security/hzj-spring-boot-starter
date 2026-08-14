package com.hzj.redis.core.queue;

import java.util.concurrent.TimeUnit;

/**
 * Redis延迟队列生产者接口。
 */
public interface RedisDelayQueueProducer {

    /**
     * 投递一条延迟消息。
     *
     * @param topic 消息主题
     * @param payload 业务载荷
     * @param delay 延迟时间
     * @param timeUnit 时间单位
     * @param <T> 业务载荷类型
     * @return 消息唯一标识
     */
    <T> String offer(String topic, T payload, long delay, TimeUnit timeUnit);
}
