package com.hzj.elasticsearch.core.client;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.hzj.common.utils.ConfigStringLoader;
import com.hzj.elasticsearch.provider.es.ElasticsearchConfigProvider;
import com.hzj.elasticsearch.provider.es.entity.ElasticsearchConfig;
import com.hzj.elasticsearch.provider.es.enums.ElasticsearchMode;
import com.hzj.elasticsearch.provider.es.enums.ElasticsearchScheme;
import lombok.Setter;
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
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractElasticsearchClientManager implements ElasticsearchClientService, ApplicationContextAware {

    @Setter
    protected ApplicationContext applicationContext;

    protected final ElasticsearchConfigProvider configProvider;

    protected final DefaultListableBeanFactory beanFactory;

    public static final String ES_SERVICE_BEAN_NAME = "ElasticsearchClient";

    private static final ReentrantLock REFRESH_LOCK = new ReentrantLock(true);

    protected static final Logger log = LoggerFactory.getLogger(AbstractElasticsearchClientManager.class);

    public AbstractElasticsearchClientManager(ConfigurableListableBeanFactory beanFactory, ElasticsearchConfigProvider configProvider) {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
        this.configProvider = configProvider;
    }

    /**
     * 获取已装配的 Elasticsearch 客户端。
     *
     * @return Elasticsearch 客户端
     */
    public ElasticsearchClient getClient() {
        if (applicationContext == null) {
            log.error("AbstractElasticsearchService.getClient ApplicationContext容器不存在");
            throw new RuntimeException("获取客户端失败");
        }
        return applicationContext.getBean(ES_SERVICE_BEAN_NAME, ElasticsearchClient.class);
    }


    /**
     * 装配的 Elasticsearch 客户端。
     * @param config ES配置
     * @return Elasticsearch 客户端
     */
    public static ElasticsearchClient assembly(ElasticsearchConfig config) {
        Objects.requireNonNull(config, "Elasticsearch 配置不能为空");
        // 1. 构建 RestClientBuilder
        RestClientBuilder restClientBuilder;
        ElasticsearchMode mode = config.getMode() == null ? ElasticsearchMode.SINGLE_NODE : config.getMode();
        ElasticsearchScheme scheme = config.getScheme() == null ? ElasticsearchScheme.HTTP : config.getScheme();
        if (mode.isCluster()) {
            List<ElasticsearchConfig.ElasticsearchNode> nodes = config.getNodes();
            if (CollUtil.isEmpty(nodes)) {
                throw new IllegalArgumentException("Elasticsearch 集群模式必须配置 nodes");
            }
            HttpHost[] httpHosts = nodes.stream().map(node -> buildHttpHost(node, scheme)).toArray(HttpHost[]::new);
            restClientBuilder = RestClient.builder(httpHosts);
        } else {
            ElasticsearchConfig.ElasticsearchNode node = config.getNode();
            if (ObjUtil.isNull(node)) {
                throw new IllegalArgumentException("Elasticsearch 单节点模式必须配置 node");
            }
            restClientBuilder = RestClient.builder(buildHttpHost(node, scheme));
        }

        // 2. 设置超时
        restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(config.getConnectTimeout()).setSocketTimeout(config.getSocketTimeout()).setConnectionRequestTimeout(config.getConnectionRequestTimeout()));

        // 3. http客户端自定义配置：鉴权、连接池、SSL
        restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder -> {
            // 连接池参数
            httpAsyncClientBuilder.setMaxConnTotal(config.getMaxConnTotal());
            httpAsyncClientBuilder.setMaxConnPerRoute(config.getMaxConnPerRoute());

            // Basic Auth
            if (StrUtil.isNotBlank(config.getUsername())) {
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(config.getUsername(), config.getPassword()));
                httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                log.debug("AbstractElasticsearchService.configureBasicAuth 已配置 Basic Auth，用户: {}", config.getUsername());
            }

            // SSL处理
            try {
                if (config.isSslEnabled()) {
                    SSLContext sslContext = null;

                    // 1. 处理跳过证书验证
                    if (config.isSslSkipVerify()) {
                        httpAsyncClientBuilder.setSSLHostnameVerifier((hostname, session) -> true);
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
                        httpAsyncClientBuilder.setSSLContext(sslContext);
                    } else if (!config.isSslSkipVerify()) {
                        log.info("AbstractElasticsearchService.configureSsl 使用系统默认信任库验证SSL证书");
                        // 显式设置系统默认 SSLContext
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

    @Override
    public void refreshClient() throws IOException {
        if (!REFRESH_LOCK.tryLock()) {
            throw new RuntimeException("正在执行ES客户端刷新操作，请稍后重试");
        }
        ElasticsearchClient newClient = null;
        try {
            ElasticsearchConfig config = configProvider.getConfig();
            newClient = assembly(config);

            // 候选客户端验证通过前，持续保留当前客户端。
            if (config.isHealthCheckAtStartup()) {
                boolean ping = newClient.ping().value();
                if (!ping) {
                    throw new RuntimeException("ES连接健康检查失败，请检查ES服务与配置");
                }
                log.info("AbstractElasticsearchService.refreshClient  Elasticsearch 连接成功！");
            }

            ElasticsearchClient oldClient = null;
            if (beanFactory.containsSingleton(ES_SERVICE_BEAN_NAME)) {
                oldClient = beanFactory.getBean(ES_SERVICE_BEAN_NAME, ElasticsearchClient.class);
                beanFactory.destroySingleton(ES_SERVICE_BEAN_NAME);
            }
            beanFactory.registerSingleton(ES_SERVICE_BEAN_NAME, newClient);
            newClient = null;

            if (oldClient != null) {
                oldClient.close();
            }
        } finally {
            if (newClient != null) {
                newClient.close();
            }
            REFRESH_LOCK.unlock();
        }
    }

    /**
     * 将节点配置转换为 HTTP 主机，避免把 host:port 当作纯 host 传入底层客户端。
     *
     * @param node   Elasticsearch 节点
     * @param scheme 连接协议
     * @return HTTP 主机
     */
    private static HttpHost buildHttpHost(ElasticsearchConfig.ElasticsearchNode node, ElasticsearchScheme scheme) {
        if (node == null || StrUtil.isBlank(node.getHost())) {
            throw new IllegalArgumentException("Elasticsearch 节点 host 不能为空");
        }
        return HttpHost.create(scheme.getValue() + "://" + node.getAddress());
    }


    /**
     * 创建信任所有证书的 SSLContext（仅用于开发测试）
     */
    private static SSLContext createTrustAllSslContext() throws Exception {
        return SSLContexts.custom().loadTrustMaterial(null, (chain, authType) -> true).build();
    }

    /**
     * 加载自定义证书
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
            log.error("AbstractElasticsearchService.loadCustomCertificate 加载证书失败: {}", certPath, e);
            throw e;
        }
    }
}
