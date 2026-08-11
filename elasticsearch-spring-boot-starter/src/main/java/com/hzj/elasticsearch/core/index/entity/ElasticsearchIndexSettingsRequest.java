package com.hzj.elasticsearch.core.index.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 索引 Settings 更新请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticsearchIndexSettingsRequest {

    /**
     * 索引名称。
     */
    private String indexName;

    /**
     * Settings 内容。
     */
    private Map<String, Object> settings;
}
