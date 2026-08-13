package com.hzj.redis.core;


import org.springframework.data.redis.core.RedisTemplate;

public class RedisCacheService extends RedisTemplate<String,Object> {

    private static final String EMPTY_PLACEHOLDER = "::NULL_PLACEHOLDER::";


}
