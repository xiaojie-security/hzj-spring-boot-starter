package com.hzj.elasticsearch.core.document.entity;

import com.hzj.elasticsearch.core.entity.enums.ElasticsearchRefreshPolicy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文档写入和创建请求。
 *
 * @param <T> 文档类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticsearchDocumentRequest<T> {

    /**
     * 索引名称。
     */
    private String indexName;

    /**
     * 文档 ID；为空时由 Elasticsearch 自动生成。
     */
    private String documentId;

    /**
     * 文档完整实体。
     */
    private T document;

    /**
     * 文档类型，用于写入后回读完整实体。
     */
    private Class<T> documentClass;

    /**
     * 写入后刷新策略。
     */
    @Builder.Default
    private ElasticsearchRefreshPolicy refreshPolicy = ElasticsearchRefreshPolicy.NONE;

    /**
     * 路由值。
     */
    private String routing;

    /**
     * Ingest Pipeline 名称。
     */
    private String pipeline;

    /**
     * 条件写入序列号。
     */
    private Long ifSequenceNumber;

    /**
     * 条件写入主分片任期。
     */
    private Long ifPrimaryTerm;

    /**
     * 是否要求目标必须是 Alias。
     */
    private Boolean requireAlias;
}
