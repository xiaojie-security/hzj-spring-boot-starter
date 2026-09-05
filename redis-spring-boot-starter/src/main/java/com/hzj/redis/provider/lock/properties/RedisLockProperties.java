package com.hzj.redis.provider.lock.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * Redis 分布式锁配置属性。
 */
@Data
@ConfigurationProperties(prefix = "spring.redis.lock")
public class RedisLockProperties {

    /**
     * 默认获取锁最大等待时长。
     */
    private long defaultWaitTime = 5L;

    /**
     * 默认锁持有时长，-1 表示启用看门狗自动续期。
     */
    private long defaultLeaseTime = -1L;

    /**
     * 等待时长和租约时长的默认时间单位。
     */
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    /**
     * Redisson 看门狗超时时长，单位为毫秒。
     */
    private long lockWatchdogTimeout = 30000L;

    /**
     * 是否使用公平锁。
     */
    private boolean fairLock;

    /**
     * 抢锁自旋间隔，单位为毫秒。
     */
    private long spinInterval = 100L;

    /**
     * 获取锁失败时是否直接抛出异常。
     */
    private boolean failFast = true;

    /**
     * 是否输出锁日志。
     */
    private boolean enableLockLog = true;

    /**
     * 是否启用 Lua 脚本缓存。
     */
    private boolean useScriptCache = true;

    /**
     * 是否校验锁同步到从节点。
     */
    private boolean checkLockSyncSlaves = true;

    /**
     * 从节点同步等待时长，单位为毫秒。
     */
    private long slaveSyncTimeout = 1000L;
}
