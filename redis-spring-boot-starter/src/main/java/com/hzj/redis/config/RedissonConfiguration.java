package com.hzj.redis.config;

import com.hzj.redis.core.lock.AbstractRedisLockClientManager;
import com.hzj.redis.core.lock.RedisLockService;
import com.hzj.redis.core.lock.impl.DefaultRedisLockService;
import com.hzj.redis.core.queue.RedisDelayQueueService;
import com.hzj.redis.core.queue.impl.DefaultRedisDelayQueueService;
import com.hzj.redis.properties.RedissonProperties;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Redisson 自动配置。
 * <p>
 * 独立于 {@link RedisCoreConfiguration}：连接参数与分布式锁行为参数统一由
 * {@link RedissonProperties}（{@code spring.redis.redisson.*}）读取后直接装配
 * {@link RedissonClient}，不再提供动态配置提供者，也不保留运行期动态刷新能力。
 * </p>
 * <p>
 * Redisson 在启动时会立即与 Redis 建立连接，因此本配置默认关闭，只有显式配置
 * {@code spring.redis.redisson.enable=true} 时才生效。使用方也可自行注册
 * {@link RedissonClient} Bean 覆盖默认装配结果。
 * </p>
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "spring.redis.redisson", name = "enable", havingValue = "true")
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonConfiguration {

    /**
     * 注册 RedissonClient 单例。
     *
     * @param properties Redisson 配置属性
     * @return RedissonClient 单例
     */
    @Bean(name = AbstractRedisLockClientManager.REDISSON_SERVICE_BEAN_NAME)
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClient(RedissonProperties properties) {
        return AbstractRedisLockClientManager.assembly(properties);
    }

    /**
     * 注册分布式锁服务。
     *
     * @param redissonClient Redisson 客户端
     * @param properties     Redisson 配置属性
     * @return 分布式锁服务
     */
    @Bean
    @ConditionalOnMissingBean(RedisLockService.class)
    public RedisLockService redisLockService(RedissonClient redissonClient,
                                             RedissonProperties properties) {
        return new DefaultRedisLockService(redissonClient, properties);
    }

    /**
     * 注册 Redis 延迟队列服务。
     * <p>
     * 延迟队列服务复用自动配置的 RedissonClient，并统一管理生产者、消费者、消息序列化和主题前缀。
     * </p>
     *
     * @param redissonClient Redisson 客户端
     * @return Redis 延迟队列服务
     */
    @Bean
    @ConditionalOnMissingBean(RedisDelayQueueService.class)
    public RedisDelayQueueService redisDelayQueueService(RedissonClient redissonClient) {
        return new DefaultRedisDelayQueueService(redissonClient);
    }
}
