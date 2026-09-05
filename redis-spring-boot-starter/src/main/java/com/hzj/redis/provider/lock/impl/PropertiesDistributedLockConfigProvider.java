package com.hzj.redis.provider.lock.impl;

import com.hzj.redis.provider.lock.DistributedLockConfigProvider;
import com.hzj.redis.provider.lock.entity.DistributedLockConfig;
import com.hzj.redis.provider.lock.properties.RedisLockProperties;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring Boot Properties 的默认分布式锁配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesDistributedLockConfigProvider implements DistributedLockConfigProvider {

    private final RedisLockProperties properties;

    /**
     * 获取分布式锁配置。
     *
     * @return 分布式锁配置
     */
    @Override
    public DistributedLockConfig getConfig() {
        DistributedLockConfig config = new DistributedLockConfig();
        config.setDefaultWaitTime(properties.getDefaultWaitTime());
        config.setDefaultLeaseTime(properties.getDefaultLeaseTime());
        config.setTimeUnit(properties.getTimeUnit());
        config.setLockWatchdogTimeout(properties.getLockWatchdogTimeout());
        config.setFairLock(properties.isFairLock());
        config.setSpinInterval(properties.getSpinInterval());
        config.setFailFast(properties.isFailFast());
        config.setEnableLockLog(properties.isEnableLockLog());
        config.setUseScriptCache(properties.isUseScriptCache());
        config.setCheckLockSyncSlaves(properties.isCheckLockSyncSlaves());
        config.setSlaveSyncTimeout(properties.getSlaveSyncTimeout());
        return config;
    }
}
