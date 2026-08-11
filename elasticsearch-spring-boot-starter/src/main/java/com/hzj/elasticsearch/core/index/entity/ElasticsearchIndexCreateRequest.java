package com.hzj.elasticsearch.core.index.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 索引创建请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticsearchIndexCreateRequest {

    /**
     * 索引名称。
     */
    private String indexName;

    /**
     * 分片数量。
     */
    private Integer numberOfShards;

    /**
     * 副本数量。
     */
    private Integer numberOfReplicas;

    /**
     * 其他索引设置。
     */
    private Map<String, Object> settings;

    /**
     * 文档 Mapping 定义。
     */
    private Map<String, Object> mappings;
}
