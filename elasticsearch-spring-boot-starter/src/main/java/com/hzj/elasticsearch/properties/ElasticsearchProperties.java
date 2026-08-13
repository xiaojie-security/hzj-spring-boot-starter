package com.hzj.elasticsearch.properties;

import com.hzj.elasticsearch.provider.es.entity.ElasticsearchConfig;
import com.hzj.elasticsearch.provider.es.enums.ElasticsearchMode;
import com.hzj.elasticsearch.provider.es.enums.ElasticsearchScheme;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Elasticsearch 配置属性。
 */
@Data
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchProperties {

    /** 节点连接模式。 */
    private ElasticsearchMode mode = ElasticsearchMode.SINGLE_NODE;
    /** 单节点配置。 */
    private ElasticsearchConfig.ElasticsearchNode node;
    /** 集群节点列表。 */
    private List<ElasticsearchConfig.ElasticsearchNode> nodes;
    /** 连接协议。 */
    private ElasticsearchScheme scheme = ElasticsearchScheme.HTTP;
    /** 账号。 */
    private String username;
    /** 密码。 */
    private String password;
    /** TCP 连接超时，单位毫秒。 */
    private Integer connectTimeout = 5000;
    /** Socket 读取超时，单位毫秒。 */
    private Integer socketTimeout = 30000;
    /** 获取连接等待超时，单位毫秒。 */
    private Integer connectionRequestTimeout = 800;
    /** 连接池全局最大连接数。 */
    private Integer maxConnTotal = 200;
    /** 单节点最大连接数。 */
    private Integer maxConnPerRoute = 100;
    /** 是否启用 SSL。 */
    private boolean sslEnabled;
    /** 是否跳过 SSL 证书校验。 */
    private boolean sslSkipVerify;
    /** 信任证书文件路径。 */
    private String caCertPath;
    /** 最大重试次数。 */
    private Integer maxRetryCount = 3;
    /** 是否启动时进行连通性检查。 */
    private boolean healthCheckAtStartup = true;
}
