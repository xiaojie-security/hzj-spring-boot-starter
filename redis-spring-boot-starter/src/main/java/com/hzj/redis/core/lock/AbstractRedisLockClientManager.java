package com.hzj.redis.core.lock;

import com.hzj.redis.provider.lock.DistributedLockConfigProvider;
import com.hzj.redis.provider.lock.entity.DistributedLockConfig;
import com.hzj.redis.provider.redis.entity.RedisConfig;
import com.hzj.redis.provider.redis.entity.RedisClusterConfig;
import com.hzj.redis.provider.redis.entity.RedisSentinelConfig;
import com.hzj.redis.provider.redis.entity.RedisSingleConfig;
import com.hzj.redis.provider.redis.enums.DeployMode;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.config.SubscriptionMode;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Redis 分布式锁客户端管理器基类。
 */
public abstract class AbstractRedisLockClientManager implements RedisLockService {

    protected final DistributedLockConfigProvider configProvider;

    protected final RedissonClient redissonClient;

    public static final String REDISSON_SERVICE_BEAN_NAME = "RedissonClient";

    /**
     * 创建 Redis 分布式锁客户端管理器。
     *
     * @param redissonClient Redisson 客户端
     * @param configProvider 分布式锁配置提供者
     */
    public AbstractRedisLockClientManager(RedissonClient redissonClient,
                                         DistributedLockConfigProvider configProvider) {
        this.redissonClient = Objects.requireNonNull(redissonClient, "Redisson 客户端不能为空");
        this.configProvider = configProvider;
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
        DistributedLockConfig config = getLockConfig();
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
        DistributedLockConfig config = getLockConfig();
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
        return handleLockResult(lockName, acquired, getLockConfig());
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
        return handleLockResult(lockName, acquired, getLockConfig());
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

    private void validatePositiveDuration(long duration, String fieldName) {
        if (duration <= 0) {
            throw new IllegalArgumentException(fieldName + " 必须大于0");
        }
    }

    /**
     * 根据 Spring Boot RedisProperties 组装 Redisson 客户端。
     *
     * @param distributedLockConfig 分布式锁配置
     * @param redisProperties Spring Boot Redis 配置属性
     * @return Redisson 客户端
     */
    public static RedissonClient assembly(DistributedLockConfig distributedLockConfig,
                                          RedisProperties redisProperties) {
        if (redisProperties == null) {
            throw new IllegalArgumentException("Redis 配置属性不能为空");
        }
        return assembly(distributedLockConfig, convertRedisConfig(redisProperties));
    }

    private static RedisConfig convertRedisConfig(RedisProperties properties) {
        RedisConfig config = new RedisConfig();
        config.setPassword(properties.getPassword());
        config.setDatabase(properties.getDatabase());
        config.setSsl(properties.getSsl().isEnabled());
        config.setTimeoutMs(getDurationMillis(properties.getTimeout(), config.getTimeoutMs()));
        config.setConnectTimeoutMs(getDurationMillis(properties.getConnectTimeout(), config.getConnectTimeoutMs()));

        RedisProperties.Lettuce lettuce = properties.getLettuce();
        if (lettuce != null && lettuce.getPool() != null) {
            RedisProperties.Pool pool = lettuce.getPool();
            config.setIdleConnectionSize(pool.getMinIdle());
            config.setMaxConnectionSize(pool.getMaxActive());
        }

        if (properties.getCluster() != null && !CollectionUtils.isEmpty(properties.getCluster().getNodes())) {
            config.setDeployMode(DeployMode.CLUSTER);
            RedisClusterConfig clusterConfig = new RedisClusterConfig();
            clusterConfig.setNodes(properties.getCluster().getNodes().stream()
                    .map(AbstractRedisLockClientManager::parseNode)
                    .toList());
            clusterConfig.setMaxRedirects(defaultValue(properties.getCluster().getMaxRedirects(), 3));
            config.setCluster(clusterConfig);
            return config;
        }

        if (properties.getSentinel() != null && StringUtils.hasText(properties.getSentinel().getMaster())) {
            config.setDeployMode(DeployMode.SENTINEL);
            RedisSentinelConfig sentinelConfig = new RedisSentinelConfig();
            sentinelConfig.setMasterName(properties.getSentinel().getMaster());
            sentinelConfig.setSentinelPassword(properties.getSentinel().getPassword());
            sentinelConfig.setSentinels(properties.getSentinel().getNodes().stream()
                    .map(AbstractRedisLockClientManager::parseNode)
                    .toList());
            config.setSentinel(sentinelConfig);
            return config;
        }

        config.setDeployMode(DeployMode.SINGLE);
        RedisSingleConfig singleConfig = new RedisSingleConfig();
        singleConfig.setAddress(new RedisNode(properties.getHost(), properties.getPort()));
        config.setSingle(singleConfig);
        return config;
    }

    private static RedisNode parseNode(String node) {
        if (!StringUtils.hasText(node)) {
            throw new IllegalArgumentException("Redis 节点地址不能为空");
        }
        String[] parts = node.split(":", -1);
        if (parts.length != 2 || !StringUtils.hasText(parts[0])) {
            throw new IllegalArgumentException("Redis 节点格式必须为 host:port，非法值: " + node);
        }
        try {
            int port = Integer.parseInt(parts[1].trim());
            if (port <= 0) {
                throw new IllegalArgumentException("Redis 节点端口必须大于0: " + node);
            }
            return new RedisNode(parts[0].trim(), port);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Redis 节点端口非法: " + node, exception);
        }
    }

    private static long getDurationMillis(Duration duration, long defaultValue) {
        return duration == null ? defaultValue : duration.toMillis();
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
