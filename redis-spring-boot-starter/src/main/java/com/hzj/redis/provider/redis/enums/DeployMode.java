package com.hzj.redis.provider.redis.enums;

/**
 * 枚举定义
 */
public enum DeployMode {
    /**
     * 单机节点
     */
    SINGLE,
    /**
     * 主从复制
     */
    MASTER_SLAVE,
    /**
     * 哨兵模式
     */
    SENTINEL,
    /**
     * Redis Cluster集群
     */
    CLUSTER
}