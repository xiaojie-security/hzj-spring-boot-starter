package com.hzj.elasticsearch.core;

import com.hzj.elasticsearch.provider.es.entity.ElasticsearchConfig;

import java.io.IOException;


/**
 * Elasticsearch 客户端服务接口

 */
public interface ElasticsearchService {

    /**
     * 装配 Elasticsearch 客户端
     * 根据传入的配置信息构建 Elasticsearch 客户端实例。
     */
    void assembly(ElasticsearchConfig config) throws IOException;

    /**
     * 刷新 Elasticsearch 客户端
     * 根据传入的配置信息刷新 Elasticsearch 客户端实例。
     */
    void refresh(ElasticsearchConfig config) throws IOException;
}
