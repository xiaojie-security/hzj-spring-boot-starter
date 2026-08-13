package com.hzj.redis.core.lock.impl;

import com.hzj.redis.core.lock.AbstractRedisLockClientManager;
import com.hzj.redis.provider.lock.DistributedLockConfigProvider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class DefaultRedisLockService extends AbstractRedisLockClientManager {
    public DefaultRedisLockService(ConfigurableListableBeanFactory beanFactory, DistributedLockConfigProvider configProvider) {
        super(beanFactory, configProvider);
    }
}
