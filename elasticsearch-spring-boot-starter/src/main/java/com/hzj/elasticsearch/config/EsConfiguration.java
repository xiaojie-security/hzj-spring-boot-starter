package com.hzj.elasticsearch.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.hzj.elasticsearch.core.client.AbstractElasticsearchClientManager;
import com.hzj.elasticsearch.core.complete.ElasticsearchService;
import com.hzj.elasticsearch.core.complete.impl.DefaultElasticsearchService;
import com.hzj.elasticsearch.core.document.ElasticsearchDocumentService;
import com.hzj.elasticsearch.core.document.impl.DefaultElasticsearchDocumentService;
import com.hzj.elasticsearch.core.index.ElasticsearchIndexService;
import com.hzj.elasticsearch.core.index.impl.DefaultElasticsearchIndexService;
import com.hzj.elasticsearch.properties.ElasticsearchProperties;
import com.hzj.elasticsearch.provider.es.ElasticsearchConfigProvider;
import com.hzj.elasticsearch.provider.es.impl.PropertiesElasticsearchConfigProvider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Elasticsearch 客户端自动配置。
 */
@AutoConfiguration
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class EsConfiguration {

    /**
     * 注册基于配置属性的 Elasticsearch 配置提供者。
     *
     * @param properties Elasticsearch 配置属性
     * @return Elasticsearch 配置提供者
     */
    @Bean
    @ConditionalOnMissingBean(ElasticsearchConfigProvider.class)
    public ElasticsearchConfigProvider elasticsearchConfigProvider(ElasticsearchProperties properties) {
        return new PropertiesElasticsearchConfigProvider(properties);
    }

    /**
     * 注册 Elasticsearch Java 客户端。
     *
     * <p>客户端由 {@link ElasticsearchConfigProvider} 提供初始配置；动态配置变更后，
     * 可通过 {@code ElasticsearchClientService#refreshClient()} 替换该单例。</p>
     *
     * @param configProvider Elasticsearch 动态配置提供者
     * @return Elasticsearch 客户端
     */
    @Bean(name = AbstractElasticsearchClientManager.ES_SERVICE_BEAN_NAME)
    @ConditionalOnMissingBean(name = AbstractElasticsearchClientManager.ES_SERVICE_BEAN_NAME)
    public ElasticsearchClient elasticsearchClient(ElasticsearchConfigProvider configProvider) {
        return AbstractElasticsearchClientManager.assembly(configProvider.getConfig());
    }

    /**
     * 注册索引级操作服务。
     *
     * @param configProvider Elasticsearch 动态配置提供者
     * @return 索引级操作服务
     */
    @Bean
    @ConditionalOnMissingBean(ElasticsearchIndexService.class)
    public ElasticsearchIndexService elasticsearchIndexClientService(
            ElasticsearchConfigProvider configProvider,
            ConfigurableListableBeanFactory beanFactory) {
        return new DefaultElasticsearchIndexService(beanFactory, configProvider);
    }

    /**
     * 注册文档级操作服务。
     *
     * @param configProvider Elasticsearch 动态配置提供者
     * @return 文档级操作服务
     */
    @Bean
    @ConditionalOnMissingBean(ElasticsearchDocumentService.class)
    public ElasticsearchDocumentService elasticsearchDocumentClientService(
            ElasticsearchConfigProvider configProvider,
            ConfigurableListableBeanFactory beanFactory) {
        return new DefaultElasticsearchDocumentService(beanFactory, configProvider);
    }

    /**
     * 注册聚合层操作服务。
     *
     * @param indexService 索引级操作服务
     * @param documentService 文档级操作服务
     * @return 聚合层操作服务。
     */
    @Bean
    @ConditionalOnMissingBean(ElasticsearchService.class)
    public ElasticsearchService elasticsearchCompleteService(
            ElasticsearchIndexService indexService,
            ElasticsearchDocumentService documentService
            ) {
        return new DefaultElasticsearchService(indexService, documentService);
    }
}
