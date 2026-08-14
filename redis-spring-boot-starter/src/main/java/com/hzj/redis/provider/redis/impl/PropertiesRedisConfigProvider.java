package com.hzj.redis.provider.redis.impl;

import com.hzj.redis.provider.redis.RedisConfigProvider;
import com.hzj.redis.provider.redis.entity.RedisClusterConfig;
import com.hzj.redis.provider.redis.entity.RedisConfig;
import com.hzj.redis.provider.redis.entity.RedisSentinelConfig;
import com.hzj.redis.provider.redis.entity.RedisSingleConfig;
import com.hzj.redis.provider.redis.enums.DeployMode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PropertiesRedisConfigProvider implements RedisConfigProvider {

    private final RedisProperties redisProperties;

    @Override
    public RedisConfig getConfig() {
        RedisConfig target = new RedisConfig();
        // 基础公共字段映射
        target.setPassword(redisProperties.getPassword());
        target.setDatabase(redisProperties.getDatabase());
        target.setSsl(redisProperties.getSsl().isEnabled());

        // 超时时间映射
        if (redisProperties.getTimeout() != null) {
            target.setTimeoutMs(redisProperties.getTimeout().toMillis());
        }
        // 连接池参数（Lettuce池配置）
        RedisProperties.Lettuce lettuce = redisProperties.getLettuce();
        if (lettuce != null && lettuce.getPool() != null) {
            RedisProperties.Pool pool = lettuce.getPool();
            target.setIdleConnectionSize(pool.getMaxIdle());
            target.setMaxConnectionSize(pool.getMaxActive());
            target.setIdleConnectionSize(pool.getMinIdle());
        }

        // 判断部署模式并填充对应节点配置
        if (redisProperties.getCluster() != null && !CollectionUtils.isEmpty(redisProperties.getCluster().getNodes())) {
            // 集群模式
            target.setDeployMode(DeployMode.CLUSTER);
            RedisClusterConfig clusterConfig = new RedisClusterConfig();
            RedisProperties.Cluster clusterSource = redisProperties.getCluster();
            List<RedisNode> springNodes = clusterSource.getNodes().stream()
                    .map(this::parseToRedisNode)
                    .collect(Collectors.toList());
            clusterConfig.setNodes(springNodes);
            clusterConfig.setMaxRedirects(clusterSource.getMaxRedirects() == null ? 3 : clusterSource.getMaxRedirects());
            target.setCluster(clusterConfig);
        } else if (redisProperties.getSentinel() != null && StringUtils.hasText(redisProperties.getSentinel().getMaster())) {
            // 哨兵模式
            target.setDeployMode(DeployMode.SENTINEL);
            RedisSentinelConfig sentinelConfig = new RedisSentinelConfig();
            RedisProperties.Sentinel sentinelSource = redisProperties.getSentinel();
            sentinelConfig.setMasterName(sentinelSource.getMaster());
            sentinelConfig.setSentinelPassword(sentinelSource.getPassword());
            List<RedisNode> sentinelNodes = sentinelSource.getNodes().stream()
                    .map(this::parseToRedisNode)
                    .collect(Collectors.toList());
            sentinelConfig.setSentinels(sentinelNodes);
            target.setSentinel(sentinelConfig);
        } else {
            // 默认单机模式
            target.setDeployMode(DeployMode.SINGLE);
            RedisSingleConfig singleConfig = new RedisSingleConfig();
            String host = redisProperties.getHost();
            int port = redisProperties.getPort();
            RedisNode node = new RedisNode(host, port);
            singleConfig.setAddress(node);
            target.setSingle(singleConfig);
        }

        return target;
    }

    /**
     * 解析字符串节点 "host:port" 转为 Spring RedisNode
     */
    private RedisNode parseToRedisNode(String nodeStr) {
        if (!StringUtils.hasText(nodeStr)) {
            throw new IllegalArgumentException("Redis节点地址不能为空");
        }
        String[] split = nodeStr.split(":");
        if (split.length != 2) {
            throw new IllegalArgumentException("Redis节点格式必须为 host:port，非法值：" + nodeStr);
        }
        String host = split[0].trim();
        int port;
        try {
            port = Integer.parseInt(split[1].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Redis节点端口非法：" + nodeStr, e);
        }
        return new RedisNode(host, port);
    }
}