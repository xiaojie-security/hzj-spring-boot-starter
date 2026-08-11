package com.hzj.elasticsearch.core.index.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 索引别名请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticsearchIndexAliasRequest {

    /**
     * 索引名称。
     */
    private String indexName;

    /**
     * 别名名称。
     */
    private String aliasName;
}
