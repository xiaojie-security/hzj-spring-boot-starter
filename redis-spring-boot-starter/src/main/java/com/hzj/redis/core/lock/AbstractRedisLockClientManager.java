package com.hzj.redis.core.lock;

import com.hzj.redis.provider.lock.DistributedLockConfigProvider;
import lombok.Setter;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractRedisLockClientManager implements RedisLockService, ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

    protected final DistributedLockConfigProvider configProvider;

    protected final DefaultListableBeanFactory beanFactory;

    public static final String REDISSON_SERVICE_BEAN_NAME = "RedissonClient";

    private static final ReentrantLock REFRESH_LOCK = new ReentrantLock(true);

    protected static final Logger log = LoggerFactory.getLogger(AbstractRedisLockClientManager.class);


    public AbstractRedisLockClientManager(ConfigurableListableBeanFactory beanFactory, DistributedLockConfigProvider configProvider) {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
        this.configProvider = configProvider;
    }


    public RedissonClient getClient(){
        if (applicationContext == null) {
            log.error("AbstractRedisLockClientManager.getClient ApplicationContext容器不存在");
            throw new RuntimeException("获取客户端失败");
        }
        return applicationContext.getBean(REDISSON_SERVICE_BEAN_NAME, RedissonClient.class);
    }

    @Override
    public void refreshClient() throws IOException {

    }

//    public static RedissonClient assembly(DistributedLockConfig config) {
//
//    }
}
