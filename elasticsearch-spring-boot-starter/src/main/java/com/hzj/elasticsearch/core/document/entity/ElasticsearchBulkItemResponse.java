package com.hzj.elasticsearch.core.document.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Bulk 单项结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticsearchBulkItemResponse {

    /**
     * 操作类型。
     */
    private String operation;

    /**
     * 索引名称。
     */
    private String indexName;

    /**
     * 文档 ID。
     */
    private String documentId;

    /**
     * HTTP 状态码。
     */
    private Integer status;

    /**
     * 是否成功。
     */
    private Boolean success;

    /**
     * Elasticsearch 返回结果。
     */
    private String result;

    /**
     * 错误信息。
     */
    private String error;
}
