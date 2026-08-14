package com.hzj.redis.config;

import com.hzj.redis.core.cache.impl.RedisCacheService;
import com.hzj.redis.core.lock.AbstractRedisLockClientManager;
import com.hzj.redis.core.lock.RedisLockService;
import com.hzj.redis.core.lock.impl.DefaultRedisLockService;
import com.hzj.redis.core.queue.RedisDelayQueueService;
import com.hzj.redis.core.queue.impl.DefaultRedisDelayQueueService;
import com.hzj.redis.core.ratelimit.RedisDimensionRateLimiter;
import com.hzj.redis.core.ratelimit.RedisIpRateLimiter;
import com.hzj.redis.core.ratelimit.RedisRateLimitManager;
import com.hzj.redis.core.ratelimit.RedisRateLimitService;
import com.hzj.redis.core.ratelimit.RedisUserIdRateLimiter;
import com.hzj.redis.core.ratelimit.impl.DefaultRedisIpRateLimiter;
import com.hzj.redis.core.ratelimit.impl.DefaultRedisRateLimitManager;
import com.hzj.redis.core.ratelimit.impl.DefaultRedisRateLimitService;
import com.hzj.redis.core.ratelimit.impl.DefaultRedisUserIdRateLimiter;
import com.hzj.redis.provider.connection.RedisConnectionFactoryProvider;
import com.hzj.redis.provider.connection.impl.LettuceRedisConnectionFactoryProvider;
import com.hzj.redis.provider.lock.DistributedLockConfigProvider;
import com.hzj.redis.provider.ratelimit.RedisRateLimitConfigProvider;
import com.hzj.redis.provider.ratelimit.impl.PropertiesRedisRateLimitConfigProvider;
import com.hzj.redis.provider.ratelimit.properties.RedisRateLimitProperties;
import com.hzj.redis.provider.redis.RedisConfigProvider;
import com.hzj.redis.provider.redis.impl.PropertiesRedisConfigProvider;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;


/**
 * Redis 核心自动配置。
 * <p>
 * 负责注册 Redis 配置提供者、Lettuce 连接工厂、RedisTemplate、缓存服务、
 * RedissonClient 以及分布式锁服务。RedisCacheService 复用同一个已完成初始化的
 * RedisTemplate，避免重复维护连接工厂和序列化器配置。
 * </p>
 */
@AutoConfiguration
@EnableConfigurationProperties({RedisProperties.class, RedisRateLimitProperties.class})
public class RedisCoreConfiguration {

    /**
     * 注册 RedisTemplate。
     *
     * @param connectionFactoryProvider Redis 连接工厂提供者
     * @return 已配置连接工厂和序列化器的 RedisTemplate
     */
    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactoryProvider connectionFactoryProvider) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(connectionFactoryProvider.getRedisConnectionFactory());
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
     * 注册基于 Spring Boot RedisProperties 的默认 Redis 配置提供者。
     *
     * @param redisProperties Spring Boot Redis 配置属性
     * @return Redis 配置提供者
     */
    @Bean
    @ConditionalOnMissingBean(RedisConfigProvider.class)
    public RedisConfigProvider redisConfigProvider(RedisProperties redisProperties) {
        return new PropertiesRedisConfigProvider(redisProperties);
    }

    /**
     * 注册默认 Lettuce Redis 连接工厂提供者。
     *
     * @param redisConfigProvider Redis 配置提供者
     * @return Redis 连接工厂提供者
     */
    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactoryProvider.class)
    public RedisConnectionFactoryProvider redisConnectionFactoryProvider(RedisConfigProvider redisConfigProvider) {
        return new LettuceRedisConnectionFactoryProvider(redisConfigProvider);
    }

    /**
     * 注册 RedissonClient 单例。
     *
     * @param redisConfigProvider Redis 配置提供者
     * @param distributedLockConfigProvider 分布式锁配置提供者
     * @return RedissonClient 单例
     */
    @Bean(name = AbstractRedisLockClientManager.REDISSON_SERVICE_BEAN_NAME)
    @ConditionalOnMissingBean(
            name = AbstractRedisLockClientManager.REDISSON_SERVICE_BEAN_NAME )
    @ConditionalOnBean(DistributedLockConfigProvider.class)
    public RedissonClient redissonClient(
            RedisConfigProvider redisConfigProvider,
            DistributedLockConfigProvider distributedLockConfigProvider) {
        return AbstractRedisLockClientManager.assembly(
                distributedLockConfigProvider.getConfig(), redisConfigProvider.getConfig());
    }

    /**
     * 注册分布式锁服务。
     *
     * @param beanFactory Spring Bean 工厂，用于动态替换 RedissonClient 单例
     * @param distributedLockConfigProvider 分布式锁配置提供者
     * @param redisConfigProvider Redis 配置提供者
     * @return 分布式锁服务
     */
    @Bean
    @ConditionalOnMissingBean(RedisLockService.class)
    @ConditionalOnBean(DistributedLockConfigProvider.class)
    public RedisLockService redisLockService(
            ConfigurableListableBeanFactory beanFactory,
            DistributedLockConfigProvider distributedLockConfigProvider,
            RedisConfigProvider redisConfigProvider) {
        return new DefaultRedisLockService(beanFactory, distributedLockConfigProvider, redisConfigProvider);
    }

    /**
     * 注册基于 Spring Boot Properties 的默认 Redis 限流配置提供者。
     *
     * @param properties Redis限流属性
     * @return Redis限流配置提供者
     */
    @Bean
    @ConditionalOnMissingBean(RedisRateLimitConfigProvider.class)
    public RedisRateLimitConfigProvider redisRateLimitConfigProvider(RedisRateLimitProperties properties) {
        return new PropertiesRedisRateLimitConfigProvider(properties);
    }

    /**
     * 注册基于IP地址的限流器。
     *
     * @param redissonClient Redisson客户端
     * @param configProvider Redis限流配置提供者
     * @return IP限流器
     */
    @Bean
    @ConditionalOnMissingBean(RedisIpRateLimiter.class)
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnProperty(prefix = "spring.redis.rate-limit", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public RedisIpRateLimiter redisIpRateLimiter(
            RedissonClient redissonClient,
            RedisRateLimitConfigProvider configProvider) {
        return new DefaultRedisIpRateLimiter(redissonClient, configProvider);
    }

    /**
     * 注册基于用户ID的限流器。
     *
     * @param redissonClient Redisson客户端
     * @param configProvider Redis限流配置提供者
     * @return 用户ID限流器
     */
    @Bean
    @ConditionalOnMissingBean(RedisUserIdRateLimiter.class)
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnProperty(prefix = "spring.redis.rate-limit", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public RedisUserIdRateLimiter redisUserIdRateLimiter(
            RedissonClient redissonClient,
            RedisRateLimitConfigProvider configProvider) {
        return new DefaultRedisUserIdRateLimiter(redissonClient, configProvider);
    }

    /**
     * 注册 Redis 统一限流服务。
     * <p>限流服务复用 RedissonClient，并根据动态配置选择固定窗口或滑动窗口算法。</p>
     *
     * @param redissonClient Redisson客户端
     * @param configProvider Redis限流配置提供者
     * @return Redis限流服务
     */
    @Bean
    @ConditionalOnMissingBean(RedisRateLimitService.class)
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnProperty(prefix = "spring.redis.rate-limit", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public RedisRateLimitService redisRateLimitService(
            RedissonClient redissonClient,
            RedisRateLimitConfigProvider configProvider,
            RedisIpRateLimiter ipRateLimiter,
            RedisUserIdRateLimiter userIdRateLimiter) {
        return new DefaultRedisRateLimitService(
                redissonClient, configProvider, ipRateLimiter, userIdRateLimiter);
    }

    /**
     * 注册 Redis 批量限流管理器。
     * <p>Spring 会自动注入所有已注册的维度限流器，管理器负责统一遍历执行。</p>
     *
     * @param rateLimiters Spring容器中的维度限流器集合
     * @return Redis批量限流管理器
     */
    @Bean
    @ConditionalOnMissingBean(RedisRateLimitManager.class)
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnProperty(prefix = "spring.redis.rate-limit", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public RedisRateLimitManager redisRateLimitManager(
            List<RedisDimensionRateLimiter> rateLimiters) {
        return new DefaultRedisRateLimitManager(rateLimiters);
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
