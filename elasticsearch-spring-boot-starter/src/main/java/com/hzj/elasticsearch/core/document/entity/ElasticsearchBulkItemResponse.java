package com.hzj.elasticsearch.core.document.entity;

import com.hzj.elasticsearch.core.document.enums.ElasticsearchBulkOperationType;
import com.hzj.elasticsearch.core.document.enums.ElasticsearchDocumentWriteResult;
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
    private ElasticsearchBulkOperationType operation;

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
    private ElasticsearchDocumentWriteResult result;

    /**
     * 错误信息。
     */
    private String error;
}
