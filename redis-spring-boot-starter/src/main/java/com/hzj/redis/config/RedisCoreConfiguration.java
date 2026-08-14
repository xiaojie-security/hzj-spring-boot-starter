package com.hzj.redis.config;

import com.hzj.redis.provider.connection.RedisConnectionFactoryProvider;
import com.hzj.redis.provider.connection.impl.LettuceRedisConnectionFactoryProvider;
import com.hzj.redis.core.lock.RedisLockService;
import com.hzj.redis.core.lock.AbstractRedisLockClientManager;
import com.hzj.redis.core.lock.impl.DefaultRedisLockService;
import com.hzj.redis.provider.lock.DistributedLockConfigProvider;
import com.hzj.redis.provider.lock.entity.DistributedLockConfig;
import com.hzj.redis.provider.redis.RedisConfigProvider;
import com.hzj.redis.provider.redis.impl.PropertiesRedisConfigProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * Redis 配置类。
 * <p>
 * 负责配置 RedisTemplate 的连接工厂以及 key/value 序列化方式。
 * </p>
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RedisProperties.class)
public class RedisCoreConfiguration {


    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactoryProvider connectionFactoryProvider){
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
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

    @Bean
    @ConditionalOnMissingBean(RedisConfigProvider.class)
    public RedisConfigProvider redisConfigProvider(org.springframework.boot.autoconfigure.data.redis.RedisProperties redisProperties) {
        return new PropertiesRedisConfigProvider(redisProperties);
    }

    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactoryProvider.class)
    public RedisConnectionFactoryProvider redisConnectionFactoryProvider(RedisConfigProvider redisConfigProvider) {
        return new LettuceRedisConnectionFactoryProvider(redisConfigProvider);
    }

    @Bean
    @ConditionalOnMissingBean(DistributedLockConfigProvider.class)
    public DistributedLockConfigProvider distributedLockConfigProvider() {
        return DistributedLockConfig::new;
    }

    @Bean(name = AbstractRedisLockClientManager.REDISSON_SERVICE_BEAN_NAME)
    @ConditionalOnMissingBean(name = AbstractRedisLockClientManager.REDISSON_SERVICE_BEAN_NAME)
    public org.redisson.api.RedissonClient redissonClient(
            RedisConfigProvider redisConfigProvider,
            DistributedLockConfigProvider distributedLockConfigProvider) {
        return AbstractRedisLockClientManager.assembly(
                distributedLockConfigProvider.getConfig(), redisConfigProvider.getConfig());
    }

    @Bean
    @ConditionalOnMissingBean(RedisLockService.class)
    public RedisLockService redisLockService(
            ConfigurableListableBeanFactory beanFactory,
            DistributedLockConfigProvider distributedLockConfigProvider,
            RedisConfigProvider redisConfigProvider) {
        return new DefaultRedisLockService(beanFactory, distributedLockConfigProvider, redisConfigProvider);
    }

}
