package com.hzj.redis.config;

import com.hzj.redis.core.cache.RedisCacheService;
import com.hzj.redis.core.lock.AbstractRedisLockClientManager;
import com.hzj.redis.core.lock.RedisLockService;
import com.hzj.redis.core.lock.impl.DefaultRedisLockService;
import com.hzj.redis.provider.connection.RedisConnectionFactoryProvider;
import com.hzj.redis.provider.connection.impl.LettuceRedisConnectionFactoryProvider;
import com.hzj.redis.provider.lock.DistributedLockConfigProvider;
import com.hzj.redis.provider.lock.entity.DistributedLockConfig;
import com.hzj.redis.provider.redis.RedisConfigProvider;
import com.hzj.redis.provider.redis.impl.PropertiesRedisConfigProvider;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * Redis 核心自动配置。
 * <p>
 * 负责注册 Redis 配置提供者、Lettuce 连接工厂、RedisTemplate、缓存服务、
 * RedissonClient 以及分布式锁服务。RedisCacheService 复用同一个已完成初始化的
 * RedisTemplate，避免重复维护连接工厂和序列化器配置。
 * </p>
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
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
     * 注册默认分布式锁配置提供者。
     *
     * @return 分布式锁配置提供者
     */
    @Bean
    @ConditionalOnMissingBean(DistributedLockConfigProvider.class)
    public DistributedLockConfigProvider distributedLockConfigProvider() {
        return DistributedLockConfig::new;
    }

    /**
     * 注册 RedissonClient 单例。
     *
     * @param redisConfigProvider Redis 配置提供者
     * @param distributedLockConfigProvider 分布式锁配置提供者
     * @return RedissonClient 单例
     */
    @Bean(name = AbstractRedisLockClientManager.REDISSON_SERVICE_BEAN_NAME)
    @ConditionalOnMissingBean(name = AbstractRedisLockClientManager.REDISSON_SERVICE_BEAN_NAME)
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
    public RedisLockService redisLockService(
            ConfigurableListableBeanFactory beanFactory,
            DistributedLockConfigProvider distributedLockConfigProvider,
            RedisConfigProvider redisConfigProvider) {
        return new DefaultRedisLockService(beanFactory, distributedLockConfigProvider, redisConfigProvider);
    }

}
