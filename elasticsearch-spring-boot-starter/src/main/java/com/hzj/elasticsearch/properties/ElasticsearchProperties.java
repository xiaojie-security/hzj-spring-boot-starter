package com.hzj.elasticsearch.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Elasticsearch 客户端配置属性。
 * <p>
 * 由本类直接装配 ElasticsearchClient Bean，不再提供动态配置提供者与运行期刷新能力。
 */
@Data
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchProperties {

    /**
     * 节点连接模式，默认单节点。
     */
    private ElasticsearchMode mode = ElasticsearchMode.SINGLE_NODE;

    /**
     * 单节点配置（单节点模式使用），默认 localhost:9200。
     */
    private Node node = new Node();

    /**
     * 集群节点列表（集群模式使用）。
     */
    private List<Node> nodes;

    /**
     * 连接协议，默认 HTTP。
     */
    private ElasticsearchScheme scheme = ElasticsearchScheme.HTTP;

    /**
     * 账号。
     */
    private String username;

    /**
     * 密码。
     */
    private String password;

    /**
     * TCP 连接超时，单位毫秒，默认 5000。
     */
    private Integer connectTimeout = 5000;

    /**
     * Socket 读取超时，单位毫秒，默认 30000。
     */
    private Integer socketTimeout = 30000;

    /**
     * 从连接池获取连接等待超时，单位毫秒，默认 800。
     */
    private Integer connectionRequestTimeout = 800;

    /**
     * HTTP 连接池全局最大连接数，默认 200。
     */
    private Integer maxConnTotal = 200;

    /**
     * 单路由（单个 ES 节点）最大连接数，默认 100。
     */
    private Integer maxConnPerRoute = 100;

    /**
     * 是否开启 SSL，true 表示使用 HTTPS。
     */
    private boolean sslEnabled = false;

    /**
     * 是否跳过证书校验（仅开发测试，生产禁止开启）。
     */
    private boolean sslSkipVerify = false;

    /**
     * 信任证书文件路径，如 classpath:es-ca.crt。
     */
    private String caCertPath;

    /**
     * 最大重试次数。
     */
    private Integer maxRetryCount = 3;

    /**
     * Elasticsearch 节点配置。
     */
    @Data
    public static class Node {

        /**
         * 节点地址（IP 或域名），默认 localhost。
         */
        private String host = "localhost";

        /**
         * 节点端口，默认 9200。
         */
        private Integer port = 9200;

        /**
         * 完整的节点地址（host:port）。
         *
         * @return 完整的节点地址
         */
        public String getAddress() {
            if (!StringUtils.hasText(host)) {
                return null;
            }
            return port != null ? host + ":" + port : host;
        }
    }
}
