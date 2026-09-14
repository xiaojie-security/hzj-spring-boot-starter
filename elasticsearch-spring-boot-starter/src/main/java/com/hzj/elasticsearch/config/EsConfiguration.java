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
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Elasticsearch 客户端自动配置。
 *
 * <p>客户端由 {@link ElasticsearchProperties} 直接装配，不再依赖动态配置提供者，
 * 也不保留运行期动态刷新能力。使用方可通过自行注册 {@link ElasticsearchClient} Bean
 * 覆盖默认装配结果。</p>
 */
@AutoConfiguration
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class EsConfiguration {

    /**
     * 注册 Elasticsearch Java 客户端。
     *
     * @param properties Elasticsearch 配置属性
     * @return Elasticsearch 客户端
     */
    @Bean(name = AbstractElasticsearchClientManager.ES_SERVICE_BEAN_NAME)
    @ConditionalOnMissingBean(name = AbstractElasticsearchClientManager.ES_SERVICE_BEAN_NAME)
    public ElasticsearchClient elasticsearchClient(ElasticsearchProperties properties) {
        return AbstractElasticsearchClientManager.assembly(properties);
    }

    /**
     * 注册索引级操作服务。
     *
     * @param client Elasticsearch 客户端
     * @return 索引级操作服务
     */
    @Bean
    @ConditionalOnMissingBean(ElasticsearchIndexService.class)
    public ElasticsearchIndexService elasticsearchIndexClientService(ElasticsearchClient client) {
        return new DefaultElasticsearchIndexService(client);
    }

    /**
     * 注册文档级操作服务。
     *
     * @param client Elasticsearch 客户端
     * @return 文档级操作服务
     */
    @Bean
    @ConditionalOnMissingBean(ElasticsearchDocumentService.class)
    public ElasticsearchDocumentService elasticsearchDocumentClientService(ElasticsearchClient client) {
        return new DefaultElasticsearchDocumentService(client);
    }

    /**
     * 注册聚合层操作服务。
     *
     * @param indexService    索引级操作服务
     * @param documentService 文档级操作服务
     * @return 聚合层操作服务
     */
    @Bean
    @ConditionalOnMissingBean(ElasticsearchService.class)
    public ElasticsearchService elasticsearchCompleteService(ElasticsearchIndexService indexService,
                                                             ElasticsearchDocumentService documentService) {
        return new DefaultElasticsearchService(indexService, documentService);
    }
}
