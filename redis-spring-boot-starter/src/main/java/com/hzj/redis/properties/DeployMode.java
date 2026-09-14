package com.hzj.redis.properties;

/**
 * Redisson 部署模式。
 */
public enum DeployMode {

    /**
     * 单机节点。
     */
    SINGLE,

    /**
     * 哨兵模式。
     */
    SENTINEL,

    /**
     * Redis Cluster 集群。
     */
    CLUSTER
}
