package com.hzj.elasticsearch.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.hzj.elasticsearch.core.client.AbstractElasticsearchClientService;
import com.hzj.elasticsearch.core.document.ElasticsearchDocumentClientService;
import com.hzj.elasticsearch.core.document.impl.DefaultElasticsearchDocumentClientService;
import com.hzj.elasticsearch.core.index.ElasticsearchIndexClientService;
import com.hzj.elasticsearch.core.index.impl.DefaultElasticsearchIndexClientService;
import com.hzj.elasticsearch.properties.ElasticsearchProperties;
import com.hzj.elasticsearch.provider.es.ElasticsearchConfigProvider;
import com.hzj.elasticsearch.provider.es.impl.PropertiesElasticsearchConfigProvider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Elasticsearch 索引和文档服务自动配置。
 *
 * <p>客户端仍由 {@code AbstractElasticsearchService.CLIENT} 统一持有，本配置不注册
 * {@code ElasticsearchClient} Spring Bean。</p>
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

    @Bean(name = AbstractElasticsearchClientService.ES_SERVICE_BEAN_NAME)
    public ElasticsearchClient elasticsearchClient(ElasticsearchConfigProvider configProvider) {
        return AbstractElasticsearchClientService.assembly(configProvider.getConfig());
    }

    /**
     * 注册索引级操作服务。
     *
     * @param configProvider Elasticsearch 动态配置提供者
     * @return 索引级操作服务
     */
    @Bean
    @ConditionalOnMissingBean(ElasticsearchIndexClientService.class)
    public ElasticsearchIndexClientService elasticsearchIndexService(
            ElasticsearchConfigProvider configProvider,
            ConfigurableListableBeanFactory beanFactory) {
        return new DefaultElasticsearchIndexClientService( beanFactory, configProvider);
    }

    /**
     * 注册文档级操作服务。
     *
     * @param configProvider Elasticsearch 动态配置提供者
     * @return 文档级操作服务
     */
    @Bean
    @ConditionalOnMissingBean(ElasticsearchDocumentClientService.class)
    public ElasticsearchDocumentClientService elasticsearchDocumentService(
            ElasticsearchConfigProvider configProvider,
            ConfigurableListableBeanFactory beanFactory) {
        return new DefaultElasticsearchDocumentClientService( beanFactory, configProvider);
    }
}
