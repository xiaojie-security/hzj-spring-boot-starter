package com.hzj.elasticsearch.config;

import com.hzj.elasticsearch.core.document.ElasticsearchDocumentService;
import com.hzj.elasticsearch.core.document.impl.DefaultElasticsearchDocumentService;
import com.hzj.elasticsearch.core.index.ElasticsearchIndexService;
import com.hzj.elasticsearch.core.index.impl.DefaultElasticsearchIndexService;
import com.hzj.elasticsearch.properties.ElasticsearchProperties;
import com.hzj.elasticsearch.provider.es.ElasticsearchConfigProvider;
import com.hzj.elasticsearch.provider.es.impl.PropertiesElasticsearchConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

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

    /**
     * 注册索引级操作服务。
     *
     * @param configProvider Elasticsearch 动态配置提供者
     * @return 索引级操作服务
     * @throws IOException Elasticsearch 客户端装配异常
     */
    @Bean
    @ConditionalOnMissingBean(ElasticsearchIndexService.class)
    public ElasticsearchIndexService elasticsearchIndexService(ElasticsearchConfigProvider configProvider)
            throws IOException {
        DefaultElasticsearchIndexService service = new DefaultElasticsearchIndexService();
        service.assembly(configProvider.getConfig());
        return service;
    }

    /**
     * 注册文档级操作服务。
     *
     * @param configProvider Elasticsearch 动态配置提供者
     * @return 文档级操作服务
     * @throws IOException Elasticsearch 客户端装配异常
     */
    @Bean
    @ConditionalOnMissingBean(ElasticsearchDocumentService.class)
    public ElasticsearchDocumentService elasticsearchDocumentService(ElasticsearchConfigProvider configProvider)
            throws IOException {
        DefaultElasticsearchDocumentService service = new DefaultElasticsearchDocumentService();
        service.assembly(configProvider.getConfig());
        return service;
    }
}
