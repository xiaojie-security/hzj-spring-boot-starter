package com.hzj.redis.config;

import com.hzj.redis.core.cache.impl.RedisCacheService;
import com.hzj.redis.core.lock.AbstractRedisLockClientManager;
import com.hzj.redis.core.lock.RedisLockService;
import com.hzj.redis.core.lock.impl.DefaultRedisLockService;
import com.hzj.redis.core.queue.RedisDelayQueueService;
import com.hzj.redis.core.queue.impl.DefaultRedisDelayQueueService;
import com.hzj.redis.provider.lock.DistributedLockConfigProvider;
import com.hzj.redis.provider.lock.impl.PropertiesDistributedLockConfigProvider;
import com.hzj.redis.provider.lock.properties.RedisLockProperties;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * Redis 核心自动配置。
 * <p>
 * 负责注册 RedisTemplate、缓存服务、RedissonClient 以及分布式锁服务。
 * Redis 连接工厂和连接参数由 Spring Boot Redis 自动配置负责；本配置只在应用
 * 启动时根据 RedisProperties 创建 RedissonClient，不提供运行期动态刷新。
 * </p>
 */
@AutoConfiguration(after = RedisAutoConfiguration.class)
@EnableConfigurationProperties({RedisProperties.class, RedisLockProperties.class})
public class RedisCoreConfiguration {

    /**
     * 注册 RedisTemplate。
     *
     * @param connectionFactory Redis 连接工厂
     * @return 已配置连接工厂和序列化器的 RedisTemplate
     */
    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(connectionFactory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    /**
     * 注册缓存服务。
     * <p>缓存服务通过组合复用已配置的 RedisTemplate。</p>
     *
     * @param redisLockService 分布式锁服务
     * @param redisTemplate RedisTemplate
     * @return Redis 缓存服务
     */
    @Bean
    @ConditionalOnMissingBean(RedisCacheService.class)
    public RedisCacheService redisCacheService(RedisLockService redisLockService,
                                               RedisTemplate<String, Object> redisTemplate) {
        return new RedisCacheService(redisLockService, redisTemplate);
    }

    /**
     * 注册基于 Spring Boot Properties 的默认分布式锁配置提供者。
     *
     * @param properties 分布式锁配置属性
     * @return 分布式锁配置提供者
     */
    @Bean
    @ConditionalOnMissingBean(DistributedLockConfigProvider.class)
    public DistributedLockConfigProvider distributedLockConfigProvider(RedisLockProperties properties) {
        return new PropertiesDistributedLockConfigProvider(properties);
    }

    /**
     * 注册 RedissonClient 单例。
     *
     * @param redisProperties Spring Boot Redis 配置属性
     * @param distributedLockConfigProvider 分布式锁配置提供者
     * @return RedissonClient 单例
     */
    @Bean(name = AbstractRedisLockClientManager.REDISSON_SERVICE_BEAN_NAME)
    @ConditionalOnMissingBean(
            name = AbstractRedisLockClientManager.REDISSON_SERVICE_BEAN_NAME )
    @ConditionalOnBean(DistributedLockConfigProvider.class)
    public RedissonClient redissonClient(
            RedisProperties redisProperties,
            DistributedLockConfigProvider distributedLockConfigProvider) {
        return AbstractRedisLockClientManager.assembly(
                distributedLockConfigProvider.getConfig(), redisProperties);
    }

    /**
     * 注册分布式锁服务。
     *
     * @param redissonClient Redisson 客户端
     * @param distributedLockConfigProvider 分布式锁配置提供者
     * @return 分布式锁服务
     */
    @Bean
    @ConditionalOnMissingBean(RedisLockService.class)
    @ConditionalOnBean(DistributedLockConfigProvider.class)
    public RedisLockService redisLockService(
            RedissonClient redissonClient,
            DistributedLockConfigProvider distributedLockConfigProvider) {
        return new DefaultRedisLockService(redissonClient, distributedLockConfigProvider);
    }


    /**
     * 注册 Redis 延迟队列服务。
     * <p>
     * 延迟队列服务复用自动配置的 RedissonClient，并统一管理生产者、消费者、消息序列化和主题前缀。
     * </p>
     *
     * @param redissonClient Redisson客户端
     * @return Redis延迟队列服务
     */
    @Bean
    @ConditionalOnMissingBean(RedisDelayQueueService.class)
    @ConditionalOnBean(RedissonClient.class)
    public RedisDelayQueueService redisDelayQueueService(RedissonClient redissonClient) {
        return new DefaultRedisDelayQueueService(redissonClient);
    }

}
