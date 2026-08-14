package com.hzj.redis.provider.connection.impl;

import com.hzj.redis.provider.connection.RedisConnectionFactoryProvider;
import com.hzj.redis.provider.redis.RedisConfigProvider;
import com.hzj.redis.provider.redis.entity.RedisClusterConfig;
import com.hzj.redis.provider.redis.entity.RedisConfig;
import com.hzj.redis.provider.redis.entity.RedisSentinelConfig;
import com.hzj.redis.provider.redis.entity.RedisSingleConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class LettuceRedisConnectionFactoryProvider implements RedisConnectionFactoryProvider {

    private final RedisConfigProvider configProvider;

    @Override
    public RedisConnectionFactory getRedisConnectionFactory() {
        RedisConfig config = configProvider.getConfig();
        LettuceClientConfiguration.LettuceClientConfigurationBuilder clientBuilder = LettuceClientConfiguration.builder();

        clientBuilder.commandTimeout(Duration.ofMillis(config.getTimeoutMs()));
        if (config.isSsl()) {
            clientBuilder.useSsl();
        }

        org.springframework.data.redis.connection.RedisConfiguration configuration = switch (config.getDeployMode()) {
            case SINGLE -> createStandalone(config);
            case SENTINEL -> createSentinel(config);
            case CLUSTER -> createCluster(config);
            case MASTER_SLAVE ->
                    throw new UnsupportedOperationException("Lettuce 不支持单独主从部署模式，请使用哨兵模式");
        };

        LettuceClientConfiguration clientConfiguration = clientBuilder.build();
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration, clientConfiguration);
        factory.afterPropertiesSet();
        return factory;
    }


    /**
     * 单机模式
     */
    private RedisConfiguration createStandalone(RedisConfig config) {
        RedisSingleConfig single = config.getSingle();
        // 校验单机配置与节点不能为空
        if (single == null) {
            throw new IllegalArgumentException("【Redis单机模式】deployMode=SINGLE，single配置不能为空");
        }
        RedisNode node = single.getAddress();
        if (node == null || !StringUtils.hasText(node.getHost())) {
            throw new IllegalArgumentException("【Redis单机模式】single.address.host不能为空");
        }
        if (node.getPort() == null || node.getPort() <= 0) {
            throw new IllegalArgumentException("【Redis单机模式】single.address.port必须大于0");
        }

        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
        standaloneConfiguration.setHostName(node.getHost());
        standaloneConfiguration.setPort(node.getPort());
        standaloneConfiguration.setDatabase(config.getDatabase());
        if (StringUtils.hasText(config.getPassword())) {
            standaloneConfiguration.setPassword(RedisPassword.of(config.getPassword()));
        } else {
            standaloneConfiguration.setPassword(RedisPassword.none());
        }
        return standaloneConfiguration;
    }

    /**
     * 哨兵模式
     */
    private RedisConfiguration createSentinel(RedisConfig config) {
        RedisSentinelConfig configSentinel = config.getSentinel();
        if (configSentinel == null) {
            throw new IllegalArgumentException("【Redis哨兵模式】deployMode=SENTINEL，sentinel配置不能为空");
        }
        if (!StringUtils.hasText(configSentinel.getMasterName())) {
            throw new IllegalArgumentException("【Redis哨兵模式】sentinel.masterName不能为空");
        }
        List<RedisNode> sentinelNodes = configSentinel.getSentinels();
        if (sentinelNodes == null || sentinelNodes.isEmpty()) {
            throw new IllegalArgumentException("【Redis哨兵模式】sentinel.sentinels节点列表不能为空");
        }

        RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration();
        sentinelConfiguration.setMaster(configSentinel.getMasterName());
        sentinelConfiguration.setDatabase(config.getDatabase());
        if (StringUtils.hasText(config.getPassword())) {
            sentinelConfiguration.setPassword(RedisPassword.of(config.getPassword()));
        } else {
            sentinelConfiguration.setPassword(RedisPassword.none());
        }

        // 填充哨兵节点，逐个校验host/port
        for (RedisNode node : sentinelNodes) {
            if (node == null || !StringUtils.hasText(node.getHost()) || node.getPort() == null || node.getPort() <= 0) {
                throw new IllegalArgumentException("【Redis哨兵模式】存在非法哨兵节点，host或port无效");
            }
            sentinelConfiguration.sentinel(node.getHost(), node.getPort());
        }

        // 哨兵独立密码
        if (StringUtils.hasText(configSentinel.getSentinelPassword())) {
            sentinelConfiguration.setSentinelPassword(RedisPassword.of(configSentinel.getSentinelPassword()));
        } else {
            sentinelConfiguration.setSentinelPassword(RedisPassword.none());
        }
        return sentinelConfiguration;
    }

    /**
     * Cluster集群模式
     */
    private RedisConfiguration createCluster(RedisConfig config) {
        RedisClusterConfig configCluster = config.getCluster();
        if (configCluster == null) {
            throw new IllegalArgumentException("【Redis集群模式】deployMode=CLUSTER，cluster配置不能为空");
        }
        List<RedisNode> clusterNodes = configCluster.getNodes();
        if (clusterNodes == null || clusterNodes.isEmpty()) {
            throw new IllegalArgumentException("【Redis集群模式】cluster.nodes节点列表不能为空");
        }

        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
        // 校验每个集群节点合法性
        for (RedisNode node : clusterNodes) {
            if (node == null || !StringUtils.hasText(node.getHost()) || node.getPort() == null || node.getPort() <= 0) {
                throw new IllegalArgumentException("【Redis集群模式】存在非法集群节点，host或port无效");
            }
        }
        clusterConfiguration.setClusterNodes(clusterNodes);
        clusterConfiguration.setMaxRedirects(configCluster.getMaxRedirects());

        if (StringUtils.hasText(config.getPassword())) {
            clusterConfiguration.setPassword(RedisPassword.of(config.getPassword()));
        } else {
            clusterConfiguration.setPassword(RedisPassword.none());
        }
        return clusterConfiguration;
    }
}
