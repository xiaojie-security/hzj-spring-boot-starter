package com.hzj.redis.provider.connection;

import org.springframework.data.redis.connection.RedisConnectionFactory;

public interface RedisConnectionFactoryProvider {

    RedisConnectionFactory getRedisConnectionFactory();
}
