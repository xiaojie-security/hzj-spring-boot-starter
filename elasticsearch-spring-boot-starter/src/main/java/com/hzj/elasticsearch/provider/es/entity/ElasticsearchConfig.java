package com.hzj.elasticsearch.provider.es.entity;

import com.hzj.elasticsearch.provider.es.enums.ElasticsearchMode;
import com.hzj.elasticsearch.provider.es.enums.ElasticsearchScheme;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Elasticsearch 客户端配置实体
 * 绑定配置前缀：elasticsearch
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElasticsearchConfig {

    /** 节点连接模式。 */
    private ElasticsearchMode mode = ElasticsearchMode.SINGLE_NODE;

    /**
     * 单节点-IP，集群模式优先使用 nodes
     */
    private String address;

    /**
     * 单节点-端口
     */
    private Integer port;

    /** 连接协议。 */
    private ElasticsearchScheme scheme = ElasticsearchScheme.HTTP;

    /**
     * 集群节点列表，格式示例：["http://127.0.0.1:9200","http://127.0.0.2:9200"]
     * 集群模式下使用此节点列表。
     */
    private List<String> nodes;

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
}
