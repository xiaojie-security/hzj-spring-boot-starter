package com.hzj.redis.provider.redis.entity;

import lombok.Data;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.springframework.data.redis.connection.RedisNode;

import java.util.List;

@Data
public class RedisSentinelConfig {
    /**
     * 哨兵监控主名称
     */
    private String masterName;
    /**
     * 哨兵节点地址列表 127.0.0.1:26379
     */
    private List<RedisNode> sentinels;
    /**
     * 哨兵密码（哨兵集群独立密码，和redis实例密码可不一致）
     */
    private String sentinelPassword;
    /**
     * 读取模式
     */
    private ReadMode readMode = ReadMode.MASTER;
    /**
     * 订阅模式
     */
    private SubscriptionMode subscriptionMode = SubscriptionMode.MASTER;
}
