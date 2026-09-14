package com.hzj.redis.config;

import com.hzj.redis.core.cache.impl.RedisCacheService;
import com.hzj.redis.core.lock.RedisLockService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 核心自动配置（纯 Redis 部分）。
 * <p>
 * 只负责 {@link RedisTemplate} 与基于它的缓存服务；Redisson 客户端、分布式锁与延迟队列
 * 由 {@link RedissonConfiguration} 独立装配，两者的配置来源也相互独立：
 * 连接参数来自 Spring Boot 的 {@link RedisProperties}（{@code spring.data.redis.*}），
 * 而 Redisson 使用 {@code spring.redis.redisson.*}。
 * </p>
 * <p>
 * 必须在 {@link RedisAutoConfiguration} 之前注册：Spring Boot 自带的 redisTemplate
 * 带 {@code @ConditionalOnMissingBean}，只要本配置的 redisTemplate 先完成注册，
 * 它就会自动退让；反之若排在其后，同名 Bean 会被重复注册，在默认的
 * allow-bean-definition-overriding=false 下直接导致启动失败。同时必须排在
 * {@link RedissonConfiguration} 之后，才能正确判断分布式锁服务是否可用。
 * </p>
 */
@AutoConfiguration(before = RedisAutoConfiguration.class, after = RedissonConfiguration.class)
@EnableConfigurationProperties(RedisProperties.class)
public class RedisCoreConfiguration {

    /**
     * 注册 RedisTemplate。
     *
     * @param connectionFactory Redis 连接工厂
     * @return 已配置连接工厂和序列化器的 RedisTemplate
     */
    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
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
     * <p>缓存服务通过组合复用已配置的 RedisTemplate，并依赖分布式锁服务实现缓存击穿双检。
     * 由于 {@link RedissonConfiguration} 默认关闭，只有显式开启
     * {@code spring.redis.redisson.enable=true}（或使用方自行提供 {@link RedisLockService}）
     * 时才会装配本 Bean。</p>
     *
     * @param redisLockService 分布式锁服务
     * @param redisTemplate    RedisTemplate
     * @return Redis 缓存服务
     */
    @Bean
    @ConditionalOnBean(RedisLockService.class)
    @ConditionalOnMissingBean(RedisCacheService.class)
    public RedisCacheService redisCacheService(RedisLockService redisLockService,
                                               @Qualifier("redisTemplate") RedisTemplate<?, ?> redisTemplate) {
        /*
         * Spring Boot 默认提供的 RedisTemplate 泛型通常为 <Object, Object>，
         * 而缓存服务内部约定使用 <String, Object>。泛型仅用于编译期，
         * 此处在自动装配边界进行受控转换，避免因泛型不变导致 Bean 无法注入。
         */
        @SuppressWarnings("unchecked")
        RedisTemplate<String, Object> typedRedisTemplate =
                (RedisTemplate<String, Object>) (RedisTemplate<?, ?>) redisTemplate;
        return new RedisCacheService(redisLockService, typedRedisTemplate);
    }
}
