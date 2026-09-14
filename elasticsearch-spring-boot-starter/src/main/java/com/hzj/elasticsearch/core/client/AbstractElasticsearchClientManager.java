package com.hzj.elasticsearch.core.client;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.hzj.common.utils.ConfigStringLoader;
import com.hzj.elasticsearch.properties.ElasticsearchMode;
import com.hzj.elasticsearch.properties.ElasticsearchProperties;
import com.hzj.elasticsearch.properties.ElasticsearchScheme;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;

/**
 * Elasticsearch 客户端管理器基类。
 * <p>
 * 客户端在启动时由 {@link #assembly(ElasticsearchProperties)} 一次性装配后注入，
 * 不再保留运行期动态刷新能力。
 */
public abstract class AbstractElasticsearchClientManager implements ElasticsearchClientService {

    /**
     * ElasticsearchClient Bean 名称。
     */
    public static final String ES_SERVICE_BEAN_NAME = "ElasticsearchClient";

    protected static final Logger log = LoggerFactory.getLogger(AbstractElasticsearchClientManager.class);

    /**
     * 已装配的 Elasticsearch 客户端。
     */
    protected final ElasticsearchClient client;

    /**
     * 创建 Elasticsearch 客户端管理器。
     *
     * @param client Elasticsearch 客户端
     */
    protected AbstractElasticsearchClientManager(ElasticsearchClient client) {
        this.client = Objects.requireNonNull(client, "Elasticsearch 客户端不能为空");
    }

    /**
     * 获取已装配的 Elasticsearch 客户端。
     *
     * @return Elasticsearch 客户端
     */
    @Override
    public ElasticsearchClient getClient() {
        return client;
    }

    /**
     * 根据配置属性装配 Elasticsearch 客户端。
     *
     * @param properties Elasticsearch 配置属性
     * @return Elasticsearch 客户端
     */
    public static ElasticsearchClient assembly(ElasticsearchProperties properties) {
        Objects.requireNonNull(properties, "Elasticsearch 配置不能为空");
        // 1. 构建 RestClientBuilder
        RestClientBuilder restClientBuilder;
        ElasticsearchMode mode = properties.getMode() == null ? ElasticsearchMode.SINGLE_NODE : properties.getMode();
        ElasticsearchScheme scheme = properties.getScheme() == null ? ElasticsearchScheme.HTTP : properties.getScheme();
        if (mode.isCluster()) {
            List<ElasticsearchProperties.Node> nodes = properties.getNodes();
            if (CollUtil.isEmpty(nodes)) {
                throw new IllegalArgumentException("Elasticsearch 集群模式必须配置 nodes");
            }
            HttpHost[] httpHosts = nodes.stream().map(node -> buildHttpHost(node, scheme)).toArray(HttpHost[]::new);
            restClientBuilder = RestClient.builder(httpHosts);
        } else {
            ElasticsearchProperties.Node node = properties.getNode();
            if (ObjUtil.isNull(node)) {
                throw new IllegalArgumentException("Elasticsearch 单节点模式必须配置 node");
            }
            restClientBuilder = RestClient.builder(buildHttpHost(node, scheme));
        }

        // 2. 设置超时
        restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                .setConnectTimeout(properties.getConnectTimeout())
                .setSocketTimeout(properties.getSocketTimeout())
                .setConnectionRequestTimeout(properties.getConnectionRequestTimeout()));

        // 3. http客户端自定义配置：鉴权、连接池、SSL
        restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder -> {
            // 连接池参数
            httpAsyncClientBuilder.setMaxConnTotal(properties.getMaxConnTotal());
            httpAsyncClientBuilder.setMaxConnPerRoute(properties.getMaxConnPerRoute());

            // Basic Auth
            if (StrUtil.isNotBlank(properties.getUsername())) {
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials(properties.getUsername(), properties.getPassword()));
                httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                log.debug("AbstractElasticsearchClientManager.assembly 已配置 Basic Auth，用户: {}",
                        properties.getUsername());
            }

            // SSL处理
            try {
                if (properties.isSslEnabled()) {
                    SSLContext sslContext = null;

                    // 1. 处理跳过证书验证
                    if (properties.isSslSkipVerify()) {
                        httpAsyncClientBuilder.setSSLHostnameVerifier((hostname, session) -> true);
                        sslContext = createTrustAllSslContext();
                        log.warn("AbstractElasticsearchClientManager.assembly SSL证书验证已禁用，仅用于开发测试环境！");
                    }

                    // 2. 加载自定义证书（会覆盖 skipVerify 的配置）
                    String caCertPath = properties.getCaCertPath();
                    if (StrUtil.isNotEmpty(caCertPath)) {
                        String absolutePath = ConfigStringLoader.getAbsolutePathString(caCertPath);
                        if (StrUtil.isEmpty(absolutePath)) {
                            String errorMsg = String.format("证书文件不存在或无法解析: %s", caCertPath);
                            if (properties.isSslSkipVerify()) {
                                log.warn("AbstractElasticsearchClientManager.assembly {}, 已跳过证书验证", errorMsg);
                            } else {
                                throw new RuntimeException(errorMsg);
                            }
                        } else {
                            sslContext = loadCustomCertificate(absolutePath);
                            log.info("AbstractElasticsearchClientManager.assembly 成功加载自定义证书: {}", absolutePath);
                        }
                    }

                    // 3. 设置 SSLContext
                    if (sslContext != null) {
                        httpAsyncClientBuilder.setSSLContext(sslContext);
                    } else if (!properties.isSslSkipVerify()) {
                        log.info("AbstractElasticsearchClientManager.assembly 使用系统默认信任库验证SSL证书");
                        httpAsyncClientBuilder.setSSLContext(SSLContext.getDefault());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("构建SSL上下文失败", e);
            }

            return httpAsyncClientBuilder;
        });

        // 4. 构建底层RestClient、Transport、高层Client
        RestClient restClient = restClientBuilder.build();
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }

    /**
     * 将节点配置转换为 HTTP 主机，避免把 host:port 当作纯 host 传入底层客户端。
     *
     * @param node   Elasticsearch 节点
     * @param scheme 连接协议
     * @return HTTP 主机
     */
    private static HttpHost buildHttpHost(ElasticsearchProperties.Node node, ElasticsearchScheme scheme) {
        if (node == null || StrUtil.isBlank(node.getHost())) {
            throw new IllegalArgumentException("Elasticsearch 节点 host 不能为空");
        }
        return HttpHost.create(scheme.getValue() + "://" + node.getAddress());
    }

    /**
     * 创建信任所有证书的 SSLContext（仅用于开发测试）。
     *
     * @return 信任所有证书的 SSLContext
     * @throws Exception 构建失败
     */
    private static SSLContext createTrustAllSslContext() throws Exception {
        return SSLContexts.custom().loadTrustMaterial(null, (chain, authType) -> true).build();
    }

    /**
     * 加载自定义证书。
     *
     * @param certPath 证书路径
     * @return 基于该证书的 SSLContext
     * @throws Exception 加载失败
     */
    private static SSLContext loadCustomCertificate(String certPath) throws Exception {
        try (FileInputStream fis = new FileInputStream(certPath)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(fis);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("es-ca", cert);

            return SSLContexts.custom().loadTrustMaterial(keyStore, null).build();
        } catch (Exception e) {
            log.error("AbstractElasticsearchClientManager.loadCustomCertificate 加载证书失败: {}", certPath, e);
            throw e;
        }
    }
}
