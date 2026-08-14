package com.hzj.redis.provider.redis.entity;

import lombok.Data;
import org.redisson.config.ReadMode;
import org.springframework.data.redis.connection.RedisNode;

import java.util.List;

@Data
public class RedisMasterSlaveConfig {
    /**
     * 主节点地址 127.0.0.1:6379
     */
    private RedisNode master;
    /**
     * 从节点地址列表
     */
    private List<RedisNode> slaves;
    /**
     * 读取模式：MASTER / SLAVE / MASTER_SLAVE
     */
    private ReadMode readMode = ReadMode.MASTER;
}