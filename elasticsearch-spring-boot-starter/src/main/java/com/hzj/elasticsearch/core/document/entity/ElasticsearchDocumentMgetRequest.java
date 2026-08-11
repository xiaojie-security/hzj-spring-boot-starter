package com.hzj.elasticsearch.core.document.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量获取文档请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticsearchDocumentMgetRequest {

    /**
     * 索引名称。
     */
    private String indexName;

    /**
     * 文档 ID 列表。
     */
    private List<String> documentIds;

    /**
     * 返回 source 字段白名单。
     */
    private List<String> sourceIncludes;

    /**
     * 排除的 source 字段。
     */
    private List<String> sourceExcludes;
}
