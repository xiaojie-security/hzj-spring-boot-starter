package com.hzj.redis.core.cache;

import org.springframework.data.redis.core.RedisOperations;

import java.util.concurrent.TimeUnit;

/**
 * Redis缓存服务接口。
 *
 * @see RedisOperations
 */
public interface CacheService extends RedisOperations<String, Object> {

    /**
     * 查询缓存，未命中时加载数据并缓存结果。
     *
     * @param key 缓存键
     * @param loader 缓存未命中时的数据加载器
     * @param cacheTtl 非空数据缓存时间
     * @param timeUnit 时间单位
     * @param <T> 数据类型
     * @return 缓存数据或加载结果，数据不存在时返回 null
     * @throws InterruptedException 等待分布式锁过程中线程被中断
     */
    <T> T getOrLoad(String key, CacheLoader<T> loader, long cacheTtl, TimeUnit timeUnit)
            throws InterruptedException;

    /**
     * 使用空值占位防止缓存穿透。
     *
     * @param key 缓存键
     * @param loader 缓存未命中时的数据加载器
     * @param cacheTtl 非空数据缓存时间
     * @param nullCacheTtl 空值占位缓存时间
     * @param timeUnit 时间单位
     * @param <T> 数据类型
     * @return 缓存数据或加载结果，数据不存在时返回 null
     */
    <T> T getOrLoadWithPenetrationProtection(String key, CacheLoader<T> loader,
                                             long cacheTtl, long nullCacheTtl, TimeUnit timeUnit);

    /**
     * 使用默认空值缓存时间防止缓存穿透。
     *
     * @param key 缓存键
     * @param loader 缓存未命中时的数据加载器
     * @param cacheTtl 缓存时间，空值占位使用相同时间
     * @param timeUnit 时间单位
     * @param <T> 数据类型
     * @return 缓存数据或加载结果，数据不存在时返回 null
     */
    <T> T getOrLoadWithPenetrationProtection(String key, CacheLoader<T> loader,
                                             long cacheTtl, TimeUnit timeUnit);

    /**
     * 使用分布式锁和二次检查防止缓存击穿。
     *
     * @param key 缓存键
     * @param loader 缓存未命中时的数据加载器
     * @param cacheTtl 非空数据缓存时间
     * @param nullCacheTtl 空值占位缓存时间
     * @param timeUnit 时间单位
     * @param <T> 数据类型
     * @return 缓存数据或加载结果，数据不存在时返回 null
     * @throws InterruptedException 等待分布式锁过程中线程被中断
     */
    <T> T getOrLoadWithBreakdownProtection(String key, CacheLoader<T> loader,
                                           long cacheTtl, long nullCacheTtl, TimeUnit timeUnit)
            throws InterruptedException;

    /**
     * 使用默认空值缓存时间防止缓存击穿。
     *
     * @param key 缓存键
     * @param loader 缓存未命中时的数据加载器
     * @param cacheTtl 缓存时间，空值占位使用相同时间
     * @param timeUnit 时间单位
     * @param <T> 数据类型
     * @return 缓存数据或加载结果，数据不存在时返回 null
     * @throws InterruptedException 等待分布式锁过程中线程被中断
     */
    <T> T getOrLoadWithBreakdownProtection(String key, CacheLoader<T> loader,
                                           long cacheTtl, TimeUnit timeUnit)
            throws InterruptedException;
}
