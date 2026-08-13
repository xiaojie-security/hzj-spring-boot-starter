package com.hzj.elasticsearch.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.hzj.elasticsearch.provider.es.enums.ElasticsearchMode;
import com.hzj.elasticsearch.provider.es.enums.ElasticsearchScheme;
import com.hzj.elasticsearch.provider.es.entity.ElasticsearchConfig;
import com.hzj.common.utils.ConfigStringLoader;
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
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;

public abstract class AbstractElasticsearchService implements ElasticsearchService {

    protected static volatile ElasticsearchClient CLIENT;

    protected static final Logger log = LoggerFactory.getLogger(AbstractElasticsearchService.class);

    /**
     * 获取已装配的 Elasticsearch 客户端。
     *
     * @return Elasticsearch 客户端
     */
    protected ElasticsearchClient requireClient() {
        if (CLIENT == null) {
            throw new IllegalStateException("Elasticsearch 客户端尚未装配");
        }
        return CLIENT;
    }

    public void assembly(ElasticsearchConfig config) throws IOException {
        Objects.requireNonNull(config, "Elasticsearch 配置不能为空");
        if (CLIENT != null) {
            return;
        }
        synchronized (AbstractElasticsearchService.class) {
            if (CLIENT != null) {
                return;
            }

            // 1. 构建 RestClientBuilder
            RestClientBuilder restClientBuilder = buildRestClientBuilder(config);

            // 2. 设置超时
            restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(config.getConnectTimeout()).setSocketTimeout(config.getSocketTimeout()).setConnectionRequestTimeout(config.getConnectionRequestTimeout()));

            // 3. http客户端自定义配置：鉴权、连接池、SSL
            restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder -> {
                // 连接池参数
                httpAsyncClientBuilder.setMaxConnTotal(config.getMaxConnTotal());
                httpAsyncClientBuilder.setMaxConnPerRoute(config.getMaxConnPerRoute());

                // Basic Auth
                configureBasicAuth(httpAsyncClientBuilder, config);

                // SSL处理
                try {
                    configureSsl(httpAsyncClientBuilder, config);
                } catch (Exception e) {
                    throw new RuntimeException("构建SSL上下文失败", e);
                }

                return httpAsyncClientBuilder;
            });

            // 4. 构建底层RestClient、Transport、高层Client
            RestClient restClient = restClientBuilder.build();
            RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
            CLIENT = new ElasticsearchClient(transport);

            // 5. 启动健康检查
            if (config.isHealthCheckAtStartup()) {
                boolean ping = CLIENT.ping().value();
                if (!ping) {
                    throw new RuntimeException("ES连接健康检查失败，ping返回false，请检查ES服务与配置");
                }
                log.info("AbstractElasticsearchService.assembly  Elasticsearch 连接成功！");
            }
        }
    }

    @Override
    public void refresh(ElasticsearchConfig config) throws IOException {
        synchronized (AbstractElasticsearchService.class) {
            if (CLIENT != null) {
                CLIENT.close();
                CLIENT = null;
            }
        }
        assembly(config);
    }

    /**
     * 构建 RestClientBuilder
     */
    private RestClientBuilder buildRestClientBuilder(ElasticsearchConfig config) {
        ElasticsearchMode mode = config.getMode() == null ? ElasticsearchMode.SINGLE_NODE : config.getMode();
        ElasticsearchScheme scheme = config.getScheme() == null ? ElasticsearchScheme.HTTP : config.getScheme();
        if (mode.isCluster()) {
            List<ElasticsearchConfig.ElasticsearchNode> nodes = config.getNodes();
            if (CollUtil.isEmpty(nodes)) {
                throw new IllegalArgumentException("Elasticsearch 集群模式必须配置 nodes");
            }
            HttpHost[] httpHosts = nodes.stream()
                    .map(node -> buildHttpHost(node, scheme))
                    .toArray(HttpHost[]::new);
            return RestClient.builder(httpHosts);
        } else {
            ElasticsearchConfig.ElasticsearchNode node = config.getNode();
            if (ObjUtil.isNull(node)) {
                throw new IllegalArgumentException("Elasticsearch 单节点模式必须配置 node");
            }
            return RestClient.builder(buildHttpHost(node, scheme));
        }
    }

    /**
     * 将节点配置转换为 HTTP 主机，避免把 host:port 当作纯 host 传入底层客户端。
     *
     * @param node Elasticsearch 节点
     * @param scheme 连接协议
     * @return HTTP 主机
     */
    private HttpHost buildHttpHost(ElasticsearchConfig.ElasticsearchNode node, ElasticsearchScheme scheme) {
        if (node == null || StrUtil.isBlank(node.getHost())) {
            throw new IllegalArgumentException("Elasticsearch 节点 host 不能为空");
        }
        return HttpHost.create(scheme.getValue() + "://" + node.getAddress());
    }

    /**
     * 配置 Basic Auth
     */
    private void configureBasicAuth(org.apache.http.impl.nio.client.HttpAsyncClientBuilder builder, ElasticsearchConfig config) {
        if (StrUtil.isNotBlank(config.getUsername())) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(config.getUsername(), config.getPassword()));
            builder.setDefaultCredentialsProvider(credentialsProvider);
            log.debug("AbstractElasticsearchService.configureBasicAuth 已配置 Basic Auth，用户: {}", config.getUsername());
        }
    }

    /**
     * 配置 SSL
     */
    private void configureSsl(org.apache.http.impl.nio.client.HttpAsyncClientBuilder builder, ElasticsearchConfig config) throws Exception {
        if (!config.isSslEnabled()) {
            return;
        }

        SSLContext sslContext = null;

        // 1. 处理跳过证书验证
        if (config.isSslSkipVerify()) {
            builder.setSSLHostnameVerifier((hostname, session) -> true);
            sslContext = createTrustAllSslContext();
            log.warn("AbstractElasticsearchService.configureSsl ️ SSL证书验证已禁用，仅用于开发测试环境！");
        }

        // 2. 加载自定义证书（会覆盖 skipVerify 的配置）
        String caCertPath = config.getCaCertPath();
        if (StrUtil.isNotEmpty(caCertPath)) {
            String absolutePath = ConfigStringLoader.getAbsolutePathString(caCertPath);
            if (StrUtil.isEmpty(absolutePath)) {
                String errorMsg = String.format("证书文件不存在或无法解析: %s", caCertPath);
                if (config.isSslSkipVerify()) {
                    log.warn("AbstractElasticsearchService.configureSsl {}, 已跳过证书验证", errorMsg);
                } else {
                    throw new RuntimeException(errorMsg);
                }
            } else {
                sslContext = loadCustomCertificate(absolutePath);
                log.info("AbstractElasticsearchService.configureSsl  成功加载自定义证书: {}", absolutePath);
            }
        }

        // 3. 设置 SSLContext
        if (sslContext != null) {
            builder.setSSLContext(sslContext);
        } else if (!config.isSslSkipVerify()) {
            log.info("AbstractElasticsearchService.configureSsl 使用系统默认信任库验证SSL证书");
            // 显式设置系统默认 SSLContext
            builder.setSSLContext(SSLContext.getDefault());
        }
    }

    /**
     * 创建信任所有证书的 SSLContext（仅用于开发测试）
     */
    private SSLContext createTrustAllSslContext() throws Exception {
        return SSLContexts.custom().loadTrustMaterial(null, (chain, authType) -> true).build();
    }

    /**
     * 加载自定义证书
     */
    private SSLContext loadCustomCertificate(String certPath) throws Exception {
        try (FileInputStream fis = new FileInputStream(certPath)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(fis);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("es-ca", cert);

            return SSLContexts.custom().loadTrustMaterial(keyStore, null).build();
        } catch (Exception e) {
            log.error("AbstractElasticsearchService.loadCustomCertificate 加载证书失败: {}", certPath, e);
            throw e;
        }
    }
}
