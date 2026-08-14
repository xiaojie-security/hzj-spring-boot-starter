package com.hzj.redis.provider.redis.entity;

import lombok.Data;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.springframework.data.redis.connection.RedisNode;

import java.util.List;

/**
 * Redis Cluster集群模式
 */
@Data
public class RedisClusterConfig {
    /**
     * 集群节点地址列表
     */
    private List<RedisNode> nodes;
    /**
     * 集群读取模式
     */
    private ReadMode readMode = ReadMode.MASTER;
    /**
     * 集群最大重定向次数
     */
    private int maxRedirects = 3;
    /**
     * 订阅模式
     */
    private SubscriptionMode subscriptionMode = SubscriptionMode.MASTER;
}