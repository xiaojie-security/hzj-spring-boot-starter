package com.hzj.redis.core.lock;

import org.redisson.api.RedissonClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public interface RedisLockService {


    /**
     * 获取 RedissonClient 客户端
     * @return RedissonClient 客户端
     */
    RedissonClient getClient();

    /**
     * 使用默认配置获取分布式锁。
     *
     * @param lockName 锁名称
     */
    void lock(String lockName);

    /**
     * 在指定租约时间内持有分布式锁。
     *
     * @param lockName  锁名称
     * @param leaseTime 租约时间
     * @param timeUnit  时间单位
     */
    void lock(String lockName, long leaseTime, TimeUnit timeUnit);

    /**
     * 使用默认租约配置尝试获取分布式锁。
     *
     * @param lockName 锁名称
     * @return 是否获取成功
     * @throws InterruptedException 等待过程中线程被中断
     */
    boolean tryLock(String lockName) throws InterruptedException;

    /**
     * 在指定等待时间内尝试获取分布式锁，并使用看门狗自动续期。
     *
     * @param lockName 锁名称
     * @param waitTime 最大等待时间
     * @param timeUnit 时间单位
     * @return 是否获取成功
     * @throws InterruptedException 等待过程中线程被中断
     */
    boolean tryLock(String lockName, long waitTime, TimeUnit timeUnit) throws InterruptedException;

    /**
     * 在指定等待时间内尝试获取分布式锁。
     *
     * @param lockName  锁名称
     * @param waitTime  最大等待时间
     * @param leaseTime 租约时间，小于等于0时使用看门狗自动续期
     * @param timeUnit  时间单位
     * @return 是否获取成功
     * @throws InterruptedException 等待过程中线程被中断
     */
    boolean tryLock(String lockName, long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException;

    /**
     * 释放当前线程持有的分布式锁。
     *
     * @param lockName 锁名称
     */
    void unlock(String lockName);

    /**
     * 强制释放分布式锁，不校验当前线程是否为持有者。
     *
     * @param lockName 锁名称
     * @return 是否成功释放
     */
    boolean forceUnlock(String lockName);

    /**
     * 判断锁是否已被任意线程持有。
     *
     * @param lockName 锁名称
     * @return 是否已加锁
     */
    boolean isLocked(String lockName);

    /**
     * 判断当前线程是否持有锁。
     *
     * @param lockName 锁名称
     * @return 当前线程是否持有锁
     */
    boolean isHeldByCurrentThread(String lockName);

    /**
     * 刷新 RedissonClient 客户端
     * 根据传入的配置信息刷新 RedissonClient 客户端实例。
     */
    void refreshClient() throws IOException;
}
