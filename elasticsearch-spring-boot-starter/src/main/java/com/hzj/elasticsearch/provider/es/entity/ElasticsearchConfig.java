package com.hzj.elasticsearch.provider.es.entity;

import com.hzj.elasticsearch.provider.es.enums.ElasticsearchMode;
import com.hzj.elasticsearch.provider.es.enums.ElasticsearchScheme;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Elasticsearch 客户端配置实体
 * 绑定配置前缀：elasticsearch
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElasticsearchConfig {

    /**
     * 节点连接模式。
     */
    private ElasticsearchMode mode = ElasticsearchMode.SINGLE_NODE;

    /**
     * 单节点配置（单节点模式使用）
     */
    private ElasticsearchNode node;

    /**
     * 集群节点列表（集群模式使用）
     */
    private List<ElasticsearchNode> nodes;

    /**
     * 连接协议。
     */
    private ElasticsearchScheme scheme = ElasticsearchScheme.HTTP;

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * TCP连接超时，默认5000ms
     */
    private Integer connectTimeout = 5000;

    /**
     * Socket读取超时，默认30000ms
     */
    private Integer socketTimeout = 30000;

    /**
     * 从连接池获取连接等待超时，默认800ms
     */
    private Integer connectionRequestTimeout = 800;

    /**
     * http连接池全局最大连接数，默认200
     */
    private Integer maxConnTotal = 200;

    /**
     * 单路由（单个ES节点）最大连接，默认100
     */
    private Integer maxConnPerRoute = 100;

    /**
     * 是否开启ssl，true=https
     */
    private boolean sslEnabled = false;

    /**
     * 是否跳过证书校验（仅开发测试，生产禁止开启）
     */
    private boolean sslSkipVerify = false;

    /**
     * 信任证书文件路径，如 classpath:es-ca.crt
     */
    private String caCertPath;

    /**
     * 最大重试次数
     */
    private Integer maxRetryCount = 3;

    /**
     * 是否启动时校验ES连通性，失败直接抛出异常阻止应用启动
     */
    private boolean healthCheckAtStartup = true;


    /**
     * Elasticsearch 节点配置
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ElasticsearchNode {

        /**
         * 节点地址（IP或域名）
         */
        private String host;

        /**
         * 节点端口
         */
        private Integer port;

        /**
         * 完整的节点地址（host:port）
         */
        public String getAddress() {
            if (!StringUtils.hasText(host)) {
                return null;
            }
            return port != null ? host + ":" + port : host;
        }
    }

    /**
     * 创建节点对象
     */
    public static ElasticsearchNode of(String host, Integer port) {
        return new ElasticsearchNode(host, port);
    }

    /**
     * 从 "host:port" 字符串解析
     */
    public ElasticsearchNode parse(String nodeStr) {
        if (!StringUtils.hasText(nodeStr)) {
            return null;
        }
        String[] parts = nodeStr.trim().split(":");
        if (parts.length == 2) {
            return new ElasticsearchNode(parts[0], Integer.parseInt(parts[1]));
        } else if (parts.length == 1) {
            return new ElasticsearchNode(parts[0], null);
        }
        throw new IllegalArgumentException("无效的节点配置: " + nodeStr + "，格式应为 host:port");
    }


}
