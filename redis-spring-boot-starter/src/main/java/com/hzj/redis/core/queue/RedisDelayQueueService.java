package com.hzj.redis.core.queue;

/**
 * Redis延迟队列统一服务接口。
 *
 * <p>同时提供统一生产者和消费者模板，适用于订单超时关闭、优惠券过期、定时重试和延时任务执行。</p>
 */
public interface RedisDelayQueueService extends RedisDelayQueueProducer, RedisDelayQueueConsumer {
}
