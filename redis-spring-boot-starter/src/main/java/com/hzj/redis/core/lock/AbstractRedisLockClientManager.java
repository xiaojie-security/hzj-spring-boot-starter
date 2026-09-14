package com.hzj.redis.core.lock;

import com.hzj.redis.properties.DeployMode;
import com.hzj.redis.properties.RedissonProperties;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.config.SubscriptionMode;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Redis 分布式锁客户端管理器基类。
 * <p>
 * Redisson 客户端在启动时由 {@link #assembly(RedissonProperties)} 一次性装配后注入，
 * 不再保留运行期动态刷新能力；锁行为参数同样直接读取 {@link RedissonProperties}，
 * 不再依赖动态配置提供者。
 * </p>
 */
public abstract class AbstractRedisLockClientManager implements RedisLockService {

    /**
     * RedissonClient Bean 名称。
     */
    public static final String REDISSON_SERVICE_BEAN_NAME = "RedissonClient";

    /**
     * Redisson 配置属性。
     */
    protected final RedissonProperties properties;

    /**
     * 已装配的 Redisson 客户端。
     */
    protected final RedissonClient redissonClient;

    /**
     * 创建 Redis 分布式锁客户端管理器。
     *
     * @param redissonClient Redisson 客户端
     * @param properties     Redisson 配置属性
     */
    public AbstractRedisLockClientManager(RedissonClient redissonClient,
                                          RedissonProperties properties) {
        this.redissonClient = Objects.requireNonNull(redissonClient, "Redisson 客户端不能为空");
        this.properties = Objects.requireNonNull(properties, "Redisson 配置不能为空");
    }

    /**
     * 获取 Redisson 客户端。
     *
     * @return Redisson 客户端
     */
    @Override
    public RedissonClient getClient() {
        return redissonClient;
    }

    @Override
    public void lock(String lockName) {
        RLock lock = getLock(lockName);
        RedissonProperties config = lockProperties();
        if (config.getDefaultLeaseTime() > 0) {
            lock.lock(config.getDefaultLeaseTime(), getTimeUnit(config));
        } else {
            lock.lock();
        }
    }

    @Override
    public void lock(String lockName, long leaseTime, TimeUnit timeUnit) {
        validatePositiveDuration(leaseTime, "leaseTime");
        Objects.requireNonNull(timeUnit, "timeUnit 不能为空");
        getLock(lockName).lock(leaseTime, timeUnit);
    }

    @Override
    public boolean tryLock(String lockName) throws InterruptedException {
        RedissonProperties config = lockProperties();
        long waitTime = config.getDefaultWaitTime();
        validateDuration(waitTime, "defaultWaitTime");
        TimeUnit timeUnit = getTimeUnit(config);
        RLock lock = getLock(lockName);
        boolean acquired = config.getDefaultLeaseTime() > 0
                ? lock.tryLock(waitTime, config.getDefaultLeaseTime(), timeUnit)
                : lock.tryLock(waitTime, timeUnit);
        return handleLockResult(lockName, acquired, config);
    }

    @Override
    public boolean tryLock(String lockName, long waitTime, TimeUnit timeUnit) throws InterruptedException {
        validateDuration(waitTime, "waitTime");
        Objects.requireNonNull(timeUnit, "timeUnit 不能为空");
        RLock lock = getLock(lockName);
        boolean acquired = lock.tryLock(waitTime, timeUnit);
        return handleLockResult(lockName, acquired, lockProperties());
    }

    @Override
    public boolean tryLock(String lockName, long waitTime, long leaseTime, TimeUnit timeUnit)
            throws InterruptedException {
        validateDuration(waitTime, "waitTime");
        validateDuration(leaseTime, "leaseTime");
        Objects.requireNonNull(timeUnit, "timeUnit 不能为空");
        RLock lock = getLock(lockName);
        boolean acquired = leaseTime > 0
                ? lock.tryLock(waitTime, leaseTime, timeUnit)
                : lock.tryLock(waitTime, timeUnit);
        return handleLockResult(lockName, acquired, lockProperties());
    }

    @Override
    public void unlock(String lockName) {
        getLock(lockName).unlock();
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
        if (!StringUtils.hasText(lockName)) {
            throw new IllegalArgumentException("锁名称不能为空");
        }
        return lockProperties().isFairLock() ? getClient().getFairLock(lockName) : getClient().getLock(lockName);
    }

    private RedissonProperties lockProperties() {
        if (properties == null) {
            throw new IllegalStateException("Redisson 配置不能为空");
        }
        return properties;
    }

    private TimeUnit getTimeUnit(RedissonProperties config) {
        return Objects.requireNonNull(config.getTimeUnit(), "分布式锁时间单位不能为空");
    }

    private boolean handleLockResult(String lockName, boolean acquired, RedissonProperties config) {
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

    private void validatePositiveDuration(long duration, String fieldName) {
        if (duration <= 0) {
            throw new IllegalArgumentException(fieldName + " 必须大于0");
        }
    }

    /**
     * 根据 Redisson 配置属性装配 Redisson 客户端。
     *
     * @param properties Redisson 配置属性
     * @return Redisson 客户端
     */
    public static RedissonClient assembly(RedissonProperties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("Redisson 配置不能为空");
        }
        DeployMode deployMode = properties.getDeployMode();
        if (deployMode == null) {
            throw new IllegalArgumentException("Redis 部署模式不能为空");
        }

        Config config = new Config();
        config.setLockWatchdogTimeout(properties.getLockWatchdogTimeout());
        switch (deployMode) {
            case SINGLE -> configureSingle(config.useSingleServer(), properties);
            case SENTINEL -> configureSentinel(config.useSentinelServers(), properties);
            case CLUSTER -> configureCluster(config.useClusterServers(), properties);
            default -> throw new IllegalArgumentException("不支持的 Redis 部署模式: " + deployMode);
        }
        return Redisson.create(config);
    }

    private static void configureSingle(SingleServerConfig serverConfig, RedissonProperties properties) {
        RedissonProperties.Single single = properties.getSingle();
        if (single == null) {
            throw new IllegalArgumentException("Redis 单机模式配置不能为空");
        }
        serverConfig.setAddress(toAddress(single.getHost(), single.getPort(), properties.isSsl()));
        serverConfig.setDatabase(properties.getDatabase());
        serverConfig.setTimeout(toInt(properties.getTimeoutMs(), "timeoutMs"));
        serverConfig.setConnectTimeout(toInt(properties.getConnectTimeoutMs(), "connectTimeoutMs"));
        serverConfig.setIdleConnectionTimeout(toInt(properties.getIdleTimeoutMs(), "idleTimeoutMs"));
        serverConfig.setConnectionMinimumIdleSize(properties.getIdleConnectionSize());
        serverConfig.setConnectionPoolSize(properties.getMaxConnectionSize());
        setPassword(serverConfig, properties.getPassword());
    }

    private static void configureSentinel(SentinelServersConfig serverConfig, RedissonProperties properties) {
        RedissonProperties.Sentinel sentinel = properties.getSentinel();
        if (sentinel == null) {
            throw new IllegalArgumentException("Redis 哨兵模式配置不能为空");
        }
        if (!StringUtils.hasText(sentinel.getMasterName())) {
            throw new IllegalArgumentException("Redis 哨兵主节点名称不能为空");
        }
        if (sentinel.getSentinels() == null || sentinel.getSentinels().isEmpty()) {
            throw new IllegalArgumentException("Redis 哨兵节点不能为空");
        }
        serverConfig.setMasterName(sentinel.getMasterName());
        sentinel.getSentinels().forEach(node -> serverConfig.addSentinelAddress(
                toAddress(node.getHost(), node.getPort(), properties.isSsl())));
        serverConfig.setReadMode(defaultValue(sentinel.getReadMode(), ReadMode.MASTER));
        serverConfig.setSubscriptionMode(defaultValue(sentinel.getSubscriptionMode(), SubscriptionMode.MASTER));
        serverConfig.setDatabase(properties.getDatabase());
        serverConfig.setTimeout(toInt(properties.getTimeoutMs(), "timeoutMs"));
        serverConfig.setConnectTimeout(toInt(properties.getConnectTimeoutMs(), "connectTimeoutMs"));
        serverConfig.setIdleConnectionTimeout(toInt(properties.getIdleTimeoutMs(), "idleTimeoutMs"));
        serverConfig.setMasterConnectionMinimumIdleSize(properties.getIdleConnectionSize());
        serverConfig.setSlaveConnectionMinimumIdleSize(properties.getIdleConnectionSize());
        serverConfig.setMasterConnectionPoolSize(properties.getMaxConnectionSize());
        serverConfig.setSlaveConnectionPoolSize(properties.getMaxConnectionSize());
        setPassword(serverConfig, properties.getPassword());
        if (StringUtils.hasText(sentinel.getSentinelPassword())) {
            serverConfig.setSentinelPassword(sentinel.getSentinelPassword());
        }
    }

    private static void configureCluster(ClusterServersConfig serverConfig, RedissonProperties properties) {
        RedissonProperties.Cluster cluster = properties.getCluster();
        if (cluster == null || cluster.getNodes() == null || cluster.getNodes().isEmpty()) {
            throw new IllegalArgumentException("Redis 集群节点不能为空");
        }
        cluster.getNodes().forEach(node -> serverConfig.addNodeAddress(
                toAddress(node.getHost(), node.getPort(), properties.isSsl())));
        serverConfig.setReadMode(defaultValue(cluster.getReadMode(), ReadMode.MASTER));
        serverConfig.setSubscriptionMode(defaultValue(cluster.getSubscriptionMode(), SubscriptionMode.MASTER));
        serverConfig.setTimeout(toInt(properties.getTimeoutMs(), "timeoutMs"));
        serverConfig.setConnectTimeout(toInt(properties.getConnectTimeoutMs(), "connectTimeoutMs"));
        serverConfig.setIdleConnectionTimeout(toInt(properties.getIdleTimeoutMs(), "idleTimeoutMs"));
        serverConfig.setMasterConnectionMinimumIdleSize(properties.getIdleConnectionSize());
        serverConfig.setSlaveConnectionMinimumIdleSize(properties.getIdleConnectionSize());
        serverConfig.setMasterConnectionPoolSize(properties.getMaxConnectionSize());
        serverConfig.setSlaveConnectionPoolSize(properties.getMaxConnectionSize());
        serverConfig.setRetryAttempts(cluster.getMaxRedirects());
        setPassword(serverConfig, properties.getPassword());
    }

    private static void setPassword(SingleServerConfig config, String password) {
        if (StringUtils.hasText(password)) {
            config.setPassword(password);
        }
    }

    private static void setPassword(SentinelServersConfig config, String password) {
        if (StringUtils.hasText(password)) {
            config.setPassword(password);
        }
    }

    private static void setPassword(ClusterServersConfig config, String password) {
        if (StringUtils.hasText(password)) {
            config.setPassword(password);
        }
    }

    private static String toAddress(String host, Integer port, boolean ssl) {
        if (!StringUtils.hasText(host) || port == null || port <= 0) {
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
