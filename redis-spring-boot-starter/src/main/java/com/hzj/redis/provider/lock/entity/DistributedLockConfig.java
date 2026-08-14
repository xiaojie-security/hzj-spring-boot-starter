package com.hzj.redis.provider.lock.entity;

import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * Redisson分布式锁全局配置
 * 支持部署模式：单机 / 主从 / 哨兵 / Redis集群
 */
@Data
public class DistributedLockConfig {

    /**
     * 默认获取锁最大等待时长
     * 注解/工具类不传参时使用该值
     */
    private long defaultWaitTime = 5;

    /**
     * 默认锁持有时间（leaseTime）
     * -1 = 开启看门狗自动续期（业务执行多久持有多久）
     * 大于0 = 固定过期，不会自动续锁
     */
    private long defaultLeaseTime = -1;

    /**
     * 全局时间单位，waitTime / leaseTime 兜底单位
     */
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    /**
     * 看门狗超时时间(ms)，默认30000；leaseTime=-1时每1/3时间自动续锁
     */
    private long lockWatchdogTimeout = 30000;

    /**
     * 默认是否公平锁：false=普通可重入锁，true=公平锁
     */
    private boolean fairLock = false;

    /**
     * 抢锁自旋间隔ms，减少频繁轮询Redis压力
     */
    private long spinInterval = 100;

    /**
     * 获取锁失败是否直接抛异常；false返回null自行降级
     */
    private boolean failFast = true;

    /**
     * 兼容旧配置项。Redisson 的 unlock 操作本身会校验当前线程持有关系。
     */
    @Deprecated
    private boolean safeUnlockCheck = true;

    /**
     * 是否打印锁获取/等待/释放/失败日志，便于监控击穿
     */
    private boolean enableLockLog = true;

    /**
     * 是否缓存Lua脚本
     * true：Redisson会缓存锁、原子操作等Lua脚本的SHA1哈希，后续执行直接传哈希，不用每次上传完整脚本
     * false：每次执行Lua都全量上传脚本，高并发场景会增加网络IO开销
     * 建议默认开启，性能提升明显
     */
    private boolean useScriptCache = true;

    /**
     * 获取锁后是否校验锁数据同步到从节点
     * 主从/哨兵/集群环境生效，保证锁写入主库后，等待数据复制到从节点再返回加锁成功
     * 防止主库刚写入锁就宕机、从库升级为主后锁丢失，提升分布式锁一致性
     * 对强一致性业务（订单、支付）必须开启；追求极致吞吐可关闭
     */
    private boolean checkLockSyncSlaves = true;

    /**
     * 从节点同步等待超时时间（单位：毫秒）
     * checkLockSyncSlaves=true时生效，主库写入锁后，最多等待该时长等待从库复制完成
     * 超时未同步完成仍判定加锁失败，避免无限阻塞线程
     */
    private long slaveSyncTimeout = 1000;
}
