package com.hzj.redis.core.queue.impl;

import com.hzj.redis.core.queue.RedisDelayQueueHandler;
import com.hzj.redis.core.queue.RedisDelayQueueMessage;
import com.hzj.redis.core.queue.RedisDelayQueueNaming;
import com.hzj.redis.core.queue.RedisDelayQueueService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.context.SmartLifecycle;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Redis延迟队列统一抽象实现。
 * <p>
 * 使用 Redisson 延迟队列将到期消息转移至阻塞队列，再由每个主题对应的消费线程处理。
 * 队列元素统一使用 JsonJacksonCodec 序列化，确保消息信封和业务载荷使用同一套编解码规则。
 * </p>
 */
@Slf4j
public abstract class AbstractRedisDelayQueueService implements RedisDelayQueueService, SmartLifecycle {

    private final RedissonClient client;

    private final Codec codec;

    private final ExecutorService consumerExecutor;

    private final ConcurrentMap<String, RBlockingQueue<RedisDelayQueueMessage>> blockingQueues = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, RDelayedQueue<RedisDelayQueueMessage>> delayedQueues = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, ConsumerRegistration<?>> registrations = new ConcurrentHashMap<>();

    private volatile boolean running;

    /**
     * 创建延迟队列抽象服务。
     *
     * @param client Redisson客户端
     */
    protected AbstractRedisDelayQueueService(RedissonClient client) {
        this(client, JsonJacksonCodec.INSTANCE);
    }

    /**
     * 创建延迟队列抽象服务。
     *
     * @param client Redisson客户端
     * @param codec 队列元素Codec
     */
    protected AbstractRedisDelayQueueService(RedissonClient client, Codec codec) {
        this.client = Objects.requireNonNull(client, "RedissonClient 不能为空");
        this.codec = Objects.requireNonNull(codec, "延迟队列Codec 不能为空");
        this.consumerExecutor = Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable, "redis-delay-queue-consumer");
            thread.setDaemon(true);
            return thread;
        });
    }

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
    @Override
    public <T> String offer(String topic, T payload, long delay, TimeUnit timeUnit) {
        String normalizedTopic = RedisDelayQueueNaming.normalizeTopic(topic);
        if (payload == null) {
            throw new IllegalArgumentException("延迟队列载荷不能为空");
        }
        if (delay < 0) {
            throw new IllegalArgumentException("延迟时间不能小于0");
        }
        Objects.requireNonNull(timeUnit, "时间单位不能为空");

        String messageId = UUID.randomUUID().toString();
        RedisDelayQueueMessage message = new RedisDelayQueueMessage(
                messageId,
                normalizedTopic,
                payload);
        getDelayedQueue(normalizedTopic).offer(message, delay, timeUnit);
        return messageId;
    }

    /**
     * 注册主题消费者。
     *
     * @param topic 消息主题
     * @param payloadType 业务载荷类型
     * @param handler 消息处理器
     * @param <T> 业务载荷类型
     */
    @Override
    public <T> void registerConsumer(String topic, Class<T> payloadType,
                                     RedisDelayQueueHandler<T> handler) {
        String normalizedTopic = RedisDelayQueueNaming.normalizeTopic(topic);
        Objects.requireNonNull(payloadType, "业务载荷类型不能为空");
        Objects.requireNonNull(handler, "消息处理器不能为空");

        ConsumerRegistration<T> registration = new ConsumerRegistration<>(normalizedTopic, payloadType, handler);
        if (registrations.putIfAbsent(normalizedTopic, registration) != null) {
            throw new IllegalStateException("延迟队列主题已注册消费者: " + normalizedTopic);
        }
        if (running) {
            startConsumer(registration);
        }
    }

    /**
     * 移除主题消费者。
     *
     * @param topic 消息主题
     */
    @Override
    public void unregisterConsumer(String topic) {
        String normalizedTopic = RedisDelayQueueNaming.normalizeTopic(topic);
        ConsumerRegistration<?> registration = registrations.remove(normalizedTopic);
        if (registration != null && registration.future != null) {
            registration.future.cancel(true);
        }
    }

    /**
     * 启动已注册的主题消费者。
     */
    @Override
    public synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        registrations.values().forEach(this::startConsumer);
    }

    /**
     * 停止所有主题消费者。
     */
    @Override
    public synchronized void stop() {
        if (!running) {
            return;
        }
        running = false;
        registrations.values().forEach(registration -> {
            if (registration.future != null) {
                registration.future.cancel(true);
            }
        });
        consumerExecutor.shutdownNow();
    }

    /**
     * 停止所有主题消费者并执行回调。
     *
     * @param callback 停止完成回调
     */
    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    /**
     * 返回消费者是否运行中。
     *
     * @return 是否运行中
     */
    @Override
    public boolean isRunning() {
        return running;
    }

    /**
     * 配置为 Spring 自动启动组件。
     *
     * @return true
     */
    @Override
    public boolean isAutoStartup() {
        return true;
    }

    /**
     * 返回生命周期阶段。
     *
     * @return 生命周期阶段
     */
    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    private void startConsumer(ConsumerRegistration<?> registration) {
        if (!registration.started.compareAndSet(false, true)) {
            return;
        }
        registration.future = consumerExecutor.submit(() -> consume(registration));
    }

    private void consume(ConsumerRegistration<?> registration) {
        RBlockingQueue<RedisDelayQueueMessage> queue = getBlockingQueue(registration.topic);
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                RedisDelayQueueMessage message = queue.take();
                if (message == null || !registration.topic.equals(message.topic())) {
                    log.error("AbstractRedisDelayQueueService.consume 延迟队列主题不匹配, topic={}", registration.topic);
                    continue;
                }
                dispatch(registration, message);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception exception) {
                log.error("AbstractRedisDelayQueueService.consume 延迟消息处理失败, topic={}",
                        registration.topic, exception);
            }
        }
    }

    private <T> void dispatch(ConsumerRegistration<T> registration, RedisDelayQueueMessage message)
            throws Exception {
        T payload = registration.payloadType.cast(message.payload());
        registration.handler.handle(payload, message);
    }

    private RBlockingQueue<RedisDelayQueueMessage> getBlockingQueue(String topic) {
        String queueName = RedisDelayQueueNaming.buildQueueName(topic);
        return blockingQueues.computeIfAbsent(queueName, name -> client.getBlockingQueue(name, codec));
    }

    private RDelayedQueue<RedisDelayQueueMessage> getDelayedQueue(String topic) {
        String queueName = RedisDelayQueueNaming.buildQueueName(topic);
        return delayedQueues.computeIfAbsent(queueName,
                name -> client.getDelayedQueue(getBlockingQueue(topic)));
    }

    private static final class ConsumerRegistration<T> {

        private final String topic;

        private final Class<T> payloadType;

        private final RedisDelayQueueHandler<T> handler;

        private final AtomicBoolean started = new AtomicBoolean();

        private volatile Future<?> future;

        private ConsumerRegistration(String topic, Class<T> payloadType,
                                     RedisDelayQueueHandler<T> handler) {
            this.topic = topic;
            this.payloadType = payloadType;
            this.handler = handler;
        }
    }
}
