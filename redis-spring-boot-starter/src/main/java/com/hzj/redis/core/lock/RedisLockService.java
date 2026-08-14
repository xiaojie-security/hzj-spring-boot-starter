package com.hzj.redis.core.lock;

import org.redisson.api.RedissonClient;

import java.io.IOException;

public  interface RedisLockService {


    /**
     * 获取 RedissonClient 客户端
     * @return RedissonClient 客户端
     */
    RedissonClient getClient();

    /**
     * 刷新 RedissonClient 客户端
     * 根据传入的配置信息刷新 RedissonClient 客户端实例。
     */
    void refreshClient() throws IOException;
}
