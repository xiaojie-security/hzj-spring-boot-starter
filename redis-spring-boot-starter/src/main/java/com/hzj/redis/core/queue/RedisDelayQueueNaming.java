package com.hzj.redis.core.queue;

import org.springframework.util.StringUtils;

/**
 * Redis延迟队列名称构建工具。
 */
public final class RedisDelayQueueNaming {

    /**
     * 所有延迟队列统一使用的Redis主题前缀。
     */
    public static final String TOPIC_PREFIX = "redis:delay-queue:";

    private RedisDelayQueueNaming() {
        throw new AssertionError("禁止实例化工具类");
    }

    /**
     * 标准化消息主题。
     *
     * @param topic 消息主题
     * @return 去除首尾空白后的主题
     */
    public static String normalizeTopic(String topic) {
        if (!StringUtils.hasText(topic)) {
            throw new IllegalArgumentException("延迟队列主题不能为空");
        }
        return topic.trim();
    }

    /**
     * 构建延迟队列名称。
     *
     * @param topic 消息主题
     * @return Redis延迟队列名称
     */
    public static String buildQueueName(String topic) {
        return TOPIC_PREFIX + normalizeTopic(topic);
    }
}
