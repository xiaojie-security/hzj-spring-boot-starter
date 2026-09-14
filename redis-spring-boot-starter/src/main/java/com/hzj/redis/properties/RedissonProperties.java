package com.hzj.redis.properties;

import lombok.Data;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redisson 配置属性。
 * <p>
 * 与 Spring Boot 自带的 Redis 连接配置（{@code spring.data.redis.*}）相互独立：
 * 前者供 {@code RedisTemplate} 使用，本类只服务于 {@link org.redisson.api.RedissonClient}
 * 及其上的分布式锁、延迟队列能力。
 * </p>
 * <p>
 * 由本类直接装配 RedissonClient Bean，不再提供动态配置提供者与运行期刷新能力。
 * </p>
 */
@Data
@ConfigurationProperties(prefix = "spring.redis.redisson")
public class RedissonProperties {

    // ------------------------------------------------------------------
    // 连接配置
    // ------------------------------------------------------------------

    /**
     * 是否启用 Redisson 自动装配，默认 false。
     * <p>Redisson 在启动时会立即与 Redis 建立连接，因此默认不装配；只有显式配置
     * {@code spring.redis.redisson.enable=true} 时才装配 RedissonClient 及其上的
     * 分布式锁、缓存与延迟队列能力。</p>
     */
    private boolean enable = false;

    /**
     * 部署模式：单机 / 哨兵 / 集群，默认单机。
     */
    private DeployMode deployMode = DeployMode.SINGLE;

    /**
     * Redis 密码。
     */
    private String password;

    /**
     * 数据库编号（集群模式下无效，集群统一使用 db0）。
     */
    private int database = 0;

    /**
     * 连接超时毫秒，默认 10000。
     */
    private long connectTimeoutMs = 10000;

    /**
     * 命令等待响应超时毫秒，默认 3000。
     */
    private long timeoutMs = 3000;

    /**
     * 最小空闲连接数，默认 5。
     */
    private int idleConnectionSize = 5;

    /**
     * 连接池最大连接数，默认 64。
     */
    private int maxConnectionSize = 64;

    /**
     * 空闲连接超时毫秒，默认 300000。
     */
    private long idleTimeoutMs = 300000;

    /**
     * 是否开启 SSL（true 时使用 rediss:// 协议）。
     */
    private boolean ssl = false;

    /**
     * 单机模式配置。
     */
    private Single single = new Single();

    /**
     * 哨兵模式配置。
     */
    private Sentinel sentinel;

    /**
     * 集群模式配置。
     */
    private Cluster cluster;

    // ------------------------------------------------------------------
    // 分布式锁行为配置
    // ------------------------------------------------------------------

    /**
     * 默认获取锁最大等待时长，默认 5；调用方不传参时使用。
     */
    private long defaultWaitTime = 5;

    /**
     * 默认锁持有时间（leaseTime）。
     * <p>-1 表示开启看门狗自动续期（业务执行多久持有多久）；大于 0 表示固定过期，不自动续锁。</p>
     */
    private long defaultLeaseTime = -1;

    /**
     * 全局时间单位，作为 waitTime / leaseTime 的兜底单位，默认秒。
     */
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    /**
     * 看门狗超时时间（毫秒），默认 30000；leaseTime=-1 时每 1/3 时间自动续锁。
     */
    private long lockWatchdogTimeout = 30000;

    /**
     * 默认是否使用公平锁：false=普通可重入锁，true=公平锁。
     */
    private boolean fairLock = false;

    /**
     * 抢锁自旋间隔毫秒，减少频繁轮询 Redis 的压力，默认 100。
     */
    private long spinInterval = 100;

    /**
     * 获取锁失败是否直接抛异常；false 时返回 null 由调用方自行降级。
     */
    private boolean failFast = true;

    /**
     * 兼容旧配置项。Redisson 的 unlock 操作本身会校验当前线程持有关系。
     */
    @Deprecated
    private boolean safeUnlockCheck = true;

    /**
     * 是否打印锁获取/等待/释放/失败日志，便于监控锁击穿。
     */
    private boolean enableLockLog = true;

    /**
     * 是否缓存 Lua 脚本。
     * <p>true 时 Redisson 会缓存锁、原子操作等 Lua 脚本的 SHA1 哈希，后续直接传哈希，
     * 不必每次上传完整脚本；建议开启。</p>
     */
    private boolean useScriptCache = true;

    /**
     * 获取锁后是否校验锁数据已同步到从节点。
     * <p>主从/哨兵/集群环境生效，对强一致性业务（订单、支付）建议开启。</p>
     */
    private boolean checkLockSyncSlaves = true;

    /**
     * 从节点同步等待超时时间（毫秒）。
     * <p>checkLockSyncSlaves=true 时生效，超时未同步完成仍判定加锁失败，避免无限阻塞线程。</p>
     */
    private long slaveSyncTimeout = 1000;

    /**
     * 单机模式配置。
     */
    @Data
    public static class Single {

        /**
         * 节点地址，默认 localhost。
         */
        private String host = "localhost";

        /**
         * 节点端口，默认 6379。
         */
        private Integer port = 6379;
    }

    /**
     * 哨兵模式配置。
     */
    @Data
    public static class Sentinel {

        /**
         * 哨兵监控的主节点名称。
         */
        private String masterName;

        /**
         * 哨兵节点地址列表，例如 127.0.0.1:26379。
         */
        private List<Node> sentinels;

        /**
         * 哨兵密码（哨兵集群独立密码，可与 Redis 实例密码不同）。
         */
        private String sentinelPassword;

        /**
         * 读取模式，默认 MASTER。
         */
        private ReadMode readMode = ReadMode.MASTER;

        /**
         * 订阅模式，默认 MASTER。
         */
        private SubscriptionMode subscriptionMode = SubscriptionMode.MASTER;
    }

    /**
     * 集群模式配置。
     */
    @Data
    public static class Cluster {

        /**
         * 集群节点地址列表。
         */
        private List<Node> nodes;

        /**
         * 集群读取模式，默认 MASTER。
         */
        private ReadMode readMode = ReadMode.MASTER;

        /**
         * 集群最大重定向次数，默认 3。
         */
        private int maxRedirects = 3;

        /**
         * 订阅模式，默认 MASTER。
         */
        private SubscriptionMode subscriptionMode = SubscriptionMode.MASTER;
    }

    /**
     * Redis 节点地址。
     */
    @Data
    public static class Node {

        /**
         * 节点地址，默认 localhost。
         */
        private String host = "localhost";

        /**
         * 节点端口。
         */
        private Integer port;
    }
}
