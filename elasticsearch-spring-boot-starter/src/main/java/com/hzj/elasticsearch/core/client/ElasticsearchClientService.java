package com.hzj.elasticsearch.core.client;

import co.elastic.clients.elasticsearch.ElasticsearchClient;


/**
 * Elasticsearch 客户端服务接口

 */
public interface ElasticsearchClientService {


    /**
     * 获取 ElasticsearchClient 客户端
     * @return ElasticsearchClient 客户端
     */
    ElasticsearchClient getClient();
}
