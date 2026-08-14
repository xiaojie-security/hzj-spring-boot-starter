package com.hzj.redis.provider.redis.entity;

import com.hzj.redis.provider.redis.enums.DeployMode;
import lombok.Data;

/**
 * Redis配置管理
 */
@Data
public class RedisConfig {

    /**
     * Redis部署模式
     */
    private DeployMode deployMode = DeployMode.SINGLE;

    /**
     * Redis密码
     */
    private String password;
    /**
     * 数据库编号（集群模式下该配置无效，集群统一使用db0）
     */
    private int database = 0;
    /**
     * 连接超时毫秒
     */
    private long connectTimeoutMs = 10000;
    /**
     * 命令等待响应超时毫秒
     */
    private long timeoutMs = 3000;
    /**
     * 最小空闲连接
     */
    private int idleConnectionSize = 5;
    /**
     * 连接池最大连接数
     */
    private int maxConnectionSize = 64;
    /**
     * 空闲连接超时时间ms
     */
    private long idleTimeoutMs = 300000;
    /**
     * 是否开启SSL
     */
    private boolean ssl = false;

    /**
     * 单机模式配置
     */
    private RedisSingleConfig single;

    /**
     * 主从模式配置
     */
    private RedisMasterSlaveConfig masterSlave;

    /**
     * 哨兵模式配置
     */
    private RedisSentinelConfig sentinel;

    /**
     * 集群模式配置
     */
    private RedisClusterConfig cluster;

}
