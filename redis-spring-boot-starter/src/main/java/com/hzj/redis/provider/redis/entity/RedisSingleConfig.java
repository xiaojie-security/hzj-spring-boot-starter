package com.hzj.redis.provider.redis.entity;

import lombok.Data;
import org.springframework.data.redis.connection.RedisNode;

@Data
public class RedisSingleConfig {
    /**
     * 地址格式：127.0.0.1:6379
     */
    private RedisNode address;
}