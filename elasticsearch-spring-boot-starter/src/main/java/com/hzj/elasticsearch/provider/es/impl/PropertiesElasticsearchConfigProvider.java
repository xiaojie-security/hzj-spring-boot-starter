package com.hzj.elasticsearch.provider.es.impl;

import cn.hutool.core.bean.BeanUtil;
import com.hzj.elasticsearch.properties.ElasticsearchProperties;
import com.hzj.elasticsearch.provider.es.ElasticsearchConfigProvider;
import com.hzj.elasticsearch.provider.es.entity.ElasticsearchConfig;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring 配置属性的 Elasticsearch 配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesElasticsearchConfigProvider implements ElasticsearchConfigProvider {

    /** Elasticsearch 配置属性。 */
    private final ElasticsearchProperties properties;

    /**
     * 获取 Elasticsearch 配置。
     *
     * @return Elasticsearch 配置
     */
    @Override
    public ElasticsearchConfig getConfig() {
        return BeanUtil.copyProperties(properties, ElasticsearchConfig.class);
    }
}
