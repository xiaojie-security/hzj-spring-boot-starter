package com.hzj.teststarter.provider;

import com.hzj.redis.provider.lock.DistributedLockConfigProvider;
import com.hzj.redis.provider.lock.entity.DistributedLockConfig;

public class DistributedLockConfigProviderImpl implements DistributedLockConfigProvider {
    @Override
    public DistributedLockConfig getConfig() {
        return new DistributedLockConfig();
    }
}
