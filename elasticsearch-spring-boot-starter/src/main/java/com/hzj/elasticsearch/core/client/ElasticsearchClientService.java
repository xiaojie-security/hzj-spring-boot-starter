package com.hzj.elasticsearch.core.client;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

import java.io.IOException;


/**
 * Elasticsearch 客户端服务接口

 */
public interface ElasticsearchClientService {


    /**
     * 获取 ElasticsearchClient 客户端
     * @return ElasticsearchClient 客户端
     */
    ElasticsearchClient getClient();

    /**
     * 刷新 Elasticsearch 客户端
     * 根据传入的配置信息刷新 Elasticsearch 客户端实例。
     */
    void refreshClient() throws IOException;
}
