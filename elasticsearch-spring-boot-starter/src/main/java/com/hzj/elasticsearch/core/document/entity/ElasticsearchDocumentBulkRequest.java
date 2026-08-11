package com.hzj.elasticsearch.core.document.entity;

import com.hzj.elasticsearch.core.entity.ElasticsearchRefreshPolicy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 自定义 Bulk 文档请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticsearchDocumentBulkRequest {

    /**
     * 默认索引名称。
     */
    private String indexName;

    /**
     * Bulk 操作列表。
     */
    private List<ElasticsearchBulkOperation> operations;

    /**
     * 批量写入后的刷新策略。
     */
    @Builder.Default
    private ElasticsearchRefreshPolicy refreshPolicy = ElasticsearchRefreshPolicy.NONE;
}
