package com.hzj.redis.core.lock;

import com.hzj.redis.provider.lock.DistributedLockConfigProvider;
import com.hzj.redis.provider.lock.entity.DistributedLockConfig;
import com.hzj.redis.provider.redis.RedisConfigProvider;
import com.hzj.redis.provider.redis.entity.RedisConfig;
import lombok.Setter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.config.SubscriptionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

public abstract class AbstractRedisLockClientManager implements RedisLockService, ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

    protected final DistributedLockConfigProvider configProvider;

    protected final RedisConfigProvider redisConfigProvider;

    protected final DefaultListableBeanFactory beanFactory;

    public static final String REDISSON_SERVICE_BEAN_NAME = "RedissonClient";

    private static final ReentrantLock REFRESH_LOCK = new ReentrantLock(true);

    protected static final Logger log = LoggerFactory.getLogger(AbstractRedisLockClientManager.class);


    public AbstractRedisLockClientManager(ConfigurableListableBeanFactory beanFactory,
                                         DistributedLockConfigProvider configProvider,
                                         RedisConfigProvider redisConfigProvider) {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
        this.configProvider = configProvider;
        this.redisConfigProvider = redisConfigProvider;
    }


    public RedissonClient getClient(){
        if (applicationContext == null) {
            log.error("AbstractRedisLockClientManager.getClient ApplicationContext容器不存在");
            throw new RuntimeException("获取客户端失败");
        }
        return applicationContext.getBean(REDISSON_SERVICE_BEAN_NAME, RedissonClient.class);
    }

    @Override
    public void lock(String lockName) {
        RLock lock = getLock(lockName);
        DistributedLockConfig config = getLockConfig();
        if (config.getDefaultLeaseTime() > 0) {
            lock.lock(config.getDefaultLeaseTime(), getTimeUnit(config));
            return;
        }
        lock.lock();
    }

    @Override
    public void lock(String lockName, long leaseTime, TimeUnit timeUnit) {
        validateDuration(leaseTime, "leaseTime");
        Objects.requireNonNull(timeUnit, "timeUnit 不能为空");
        getLock(lockName).lock(leaseTime, timeUnit);
    }

    @Override
    public boolean tryLock(String lockName) throws InterruptedException {
        DistributedLockConfig config = getLockConfig();
        long waitTime = config.getDefaultWaitTime();
        validateDuration(waitTime, "defaultWaitTime");
        TimeUnit timeUnit = getTimeUnit(config);
        boolean acquired = config.getDefaultLeaseTime() > 0
                ? getLock(lockName).tryLock(waitTime, config.getDefaultLeaseTime(), timeUnit)
                : getLock(lockName).tryLock(waitTime, timeUnit);
        return handleLockResult(lockName, acquired, config);
    }

    @Override
    public boolean tryLock(String lockName, long waitTime, TimeUnit timeUnit) throws InterruptedException {
        validateDuration(waitTime, "waitTime");
        Objects.requireNonNull(timeUnit, "timeUnit 不能为空");
        boolean acquired = getLock(lockName).tryLock(waitTime, timeUnit);
        return handleLockResult(lockName, acquired, getLockConfig());
    }

    @Override
    public boolean tryLock(String lockName, long waitTime, long leaseTime, TimeUnit timeUnit)
            throws InterruptedException {
        validateDuration(waitTime, "waitTime");
        validateDuration(leaseTime, "leaseTime");
        Objects.requireNonNull(timeUnit, "timeUnit 不能为空");
        boolean acquired = leaseTime > 0
                ? getLock(lockName).tryLock(waitTime, leaseTime, timeUnit)
                : getLock(lockName).tryLock(waitTime, timeUnit);
        return handleLockResult(lockName, acquired, getLockConfig());
    }

    @Override
    public void unlock(String lockName) {
        RLock lock = getLock(lockName);
        DistributedLockConfig config = getLockConfig();
        if (!config.isSafeUnlockCheck() || lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    @Override
    public boolean forceUnlock(String lockName) {
        return getLock(lockName).forceUnlock();
    }

    @Override
    public boolean isLocked(String lockName) {
        return getLock(lockName).isLocked();
    }

    @Override
    public boolean isHeldByCurrentThread(String lockName) {
        return getLock(lockName).isHeldByCurrentThread();
    }

    /**
     * 获取当前配置对应的锁实例。
     *
     * @param lockName 锁名称
     * @return 锁实例
     */
    protected RLock getLock(String lockName) {
        if (!org.springframework.util.StringUtils.hasText(lockName)) {
            throw new IllegalArgumentException("锁名称不能为空");
        }
        DistributedLockConfig config = getLockConfig();
        return config.isFairLock() ? getClient().getFairLock(lockName) : getClient().getLock(lockName);
    }

    private DistributedLockConfig getLockConfig() {
        DistributedLockConfig config = configProvider.getConfig();
        if (config == null) {
            throw new IllegalStateException("分布式锁配置不能为空");
        }
        return config;
    }

    private TimeUnit getTimeUnit(DistributedLockConfig config) {
        return Objects.requireNonNull(config.getTimeUnit(), "分布式锁时间单位不能为空");
    }

    private boolean handleLockResult(String lockName, boolean acquired, DistributedLockConfig config) {
        if (!acquired && config.isFailFast()) {
            throw new IllegalStateException("获取分布式锁失败: " + lockName);
        }
        return acquired;
    }

    private void validateDuration(long duration, String fieldName) {
        if (duration < 0) {
            throw new IllegalArgumentException(fieldName + " 不能小于0");
        }
    }

    @Override
    public void refreshClient() throws IOException {
        if (!REFRESH_LOCK.tryLock()) {
            throw new IllegalStateException("正在执行 Redisson 客户端刷新操作，请稍后重试");
        }
        RedissonClient newClient = null;
        try {
            newClient = assembly(configProvider.getConfig(), redisConfigProvider.getConfig());
            RedissonClient oldClient = null;
            if (beanFactory.containsSingleton(REDISSON_SERVICE_BEAN_NAME)) {
                oldClient = beanFactory.getBean(REDISSON_SERVICE_BEAN_NAME, RedissonClient.class);
                beanFactory.destroySingleton(REDISSON_SERVICE_BEAN_NAME);
            }
            beanFactory.registerSingleton(REDISSON_SERVICE_BEAN_NAME, newClient);
            newClient = null;
            if (oldClient != null) {
                oldClient.shutdown();
            }
        } finally {
            if (newClient != null) {
                newClient.shutdown();
            }
            REFRESH_LOCK.unlock();
        }
    }

    public static RedissonClient assembly(DistributedLockConfig distributedLockConfig, RedisConfig redisConfig) {
        if (distributedLockConfig == null) {
            throw new IllegalArgumentException("分布式锁配置不能为空");
        }
        if (redisConfig == null) {
            throw new IllegalArgumentException("Redis 配置不能为空");
        }
        if (redisConfig.getDeployMode() == null) {
            throw new IllegalArgumentException("Redis 部署模式不能为空");
        }

        Config config = new Config();
        config.setLockWatchdogTimeout(distributedLockConfig.getLockWatchdogTimeout());
        switch (redisConfig.getDeployMode()) {
            case SINGLE -> configureSingle(config.useSingleServer(), redisConfig);
            case SENTINEL -> configureSentinel(config.useSentinelServers(), redisConfig);
            case CLUSTER -> configureCluster(config.useClusterServers(), redisConfig);
            default -> throw new IllegalArgumentException("不支持的 Redis 部署模式: " + redisConfig.getDeployMode());
        }
        return org.redisson.Redisson.create(config);
    }

    private static void configureSingle(SingleServerConfig serverConfig, RedisConfig redisConfig) {
        if (redisConfig.getSingle() == null || redisConfig.getSingle().getAddress() == null) {
            throw new IllegalArgumentException("Redis 单机模式地址不能为空");
        }
        serverConfig.setAddress(toAddress(redisConfig.getSingle().getAddress().getHost(),
                redisConfig.getSingle().getAddress().getPort(), redisConfig.isSsl()));
        serverConfig.setDatabase(redisConfig.getDatabase());
        serverConfig.setTimeout(toInt(redisConfig.getTimeoutMs(), "timeoutMs"));
        serverConfig.setConnectTimeout(toInt(redisConfig.getConnectTimeoutMs(), "connectTimeoutMs"));
        serverConfig.setIdleConnectionTimeout(toInt(redisConfig.getIdleTimeoutMs(), "idleTimeoutMs"));
        serverConfig.setConnectionMinimumIdleSize(redisConfig.getIdleConnectionSize());
        serverConfig.setConnectionPoolSize(redisConfig.getMaxConnectionSize());
        setPassword(serverConfig, redisConfig.getPassword());
    }

    private static void configureSentinel(SentinelServersConfig serverConfig, RedisConfig redisConfig) {
        if (redisConfig.getSentinel() == null) {
            throw new IllegalArgumentException("Redis 哨兵模式配置不能为空");
        }
        if (!org.springframework.util.StringUtils.hasText(redisConfig.getSentinel().getMasterName())) {
            throw new IllegalArgumentException("Redis 哨兵主节点名称不能为空");
        }
        serverConfig.setMasterName(redisConfig.getSentinel().getMasterName());
        if (redisConfig.getSentinel().getSentinels() == null || redisConfig.getSentinel().getSentinels().isEmpty()) {
            throw new IllegalArgumentException("Redis 哨兵节点不能为空");
        }
        redisConfig.getSentinel().getSentinels().forEach(node -> serverConfig.addSentinelAddress(
                toAddress(node.getHost(), node.getPort(), redisConfig.isSsl())));
        serverConfig.setReadMode(defaultValue(redisConfig.getSentinel().getReadMode(), ReadMode.MASTER));
        serverConfig.setSubscriptionMode(defaultValue(redisConfig.getSentinel().getSubscriptionMode(), SubscriptionMode.MASTER));
        serverConfig.setDatabase(redisConfig.getDatabase());
        serverConfig.setTimeout(toInt(redisConfig.getTimeoutMs(), "timeoutMs"));
        serverConfig.setConnectTimeout(toInt(redisConfig.getConnectTimeoutMs(), "connectTimeoutMs"));
        serverConfig.setIdleConnectionTimeout(toInt(redisConfig.getIdleTimeoutMs(), "idleTimeoutMs"));
        serverConfig.setMasterConnectionMinimumIdleSize(redisConfig.getIdleConnectionSize());
        serverConfig.setSlaveConnectionMinimumIdleSize(redisConfig.getIdleConnectionSize());
        serverConfig.setMasterConnectionPoolSize(redisConfig.getMaxConnectionSize());
        serverConfig.setSlaveConnectionPoolSize(redisConfig.getMaxConnectionSize());
        setPassword(serverConfig, redisConfig.getPassword());
        if (org.springframework.util.StringUtils.hasText(redisConfig.getSentinel().getSentinelPassword())) {
            serverConfig.setSentinelPassword(redisConfig.getSentinel().getSentinelPassword());
        }
    }

    private static void configureCluster(ClusterServersConfig serverConfig, RedisConfig redisConfig) {
        if (redisConfig.getCluster() == null || redisConfig.getCluster().getNodes() == null
                || redisConfig.getCluster().getNodes().isEmpty()) {
            throw new IllegalArgumentException("Redis 集群节点不能为空");
        }
        redisConfig.getCluster().getNodes().forEach(node -> serverConfig.addNodeAddress(
                toAddress(node.getHost(), node.getPort(), redisConfig.isSsl())));
        serverConfig.setReadMode(defaultValue(redisConfig.getCluster().getReadMode(), ReadMode.MASTER));
        serverConfig.setSubscriptionMode(defaultValue(redisConfig.getCluster().getSubscriptionMode(), SubscriptionMode.MASTER));
        serverConfig.setTimeout(toInt(redisConfig.getTimeoutMs(), "timeoutMs"));
        serverConfig.setConnectTimeout(toInt(redisConfig.getConnectTimeoutMs(), "connectTimeoutMs"));
        serverConfig.setIdleConnectionTimeout(toInt(redisConfig.getIdleTimeoutMs(), "idleTimeoutMs"));
        serverConfig.setMasterConnectionMinimumIdleSize(redisConfig.getIdleConnectionSize());
        serverConfig.setSlaveConnectionMinimumIdleSize(redisConfig.getIdleConnectionSize());
        serverConfig.setMasterConnectionPoolSize(redisConfig.getMaxConnectionSize());
        serverConfig.setSlaveConnectionPoolSize(redisConfig.getMaxConnectionSize());
        serverConfig.setRetryAttempts(redisConfig.getCluster().getMaxRedirects());
        setPassword(serverConfig, redisConfig.getPassword());
    }

    private static void setPassword(SingleServerConfig config, String password) {
        if (org.springframework.util.StringUtils.hasText(password)) {
            config.setPassword(password);
        }
    }

    private static void setPassword(SentinelServersConfig config, String password) {
        if (org.springframework.util.StringUtils.hasText(password)) {
            config.setPassword(password);
        }
    }

    private static void setPassword(ClusterServersConfig config, String password) {
        if (org.springframework.util.StringUtils.hasText(password)) {
            config.setPassword(password);
        }
    }

    private static String toAddress(String host, Integer port, boolean ssl) {
        if (!org.springframework.util.StringUtils.hasText(host) || port == null || port <= 0) {
            throw new IllegalArgumentException("Redis 节点地址不合法");
        }
        return (ssl ? "rediss://" : "redis://") + host + ":" + port;
    }

    private static int toInt(long value, String fieldName) {
        if (value < 0 || value > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(fieldName + " 超出有效范围: " + value);
        }
        return (int) value;
    }

    private static <T> T defaultValue(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }
}
