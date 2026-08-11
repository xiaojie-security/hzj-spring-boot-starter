package com.hzj.elasticsearch.core.document.entity;

import com.hzj.elasticsearch.core.enums.ElasticsearchRefreshPolicy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文档局部更新请求。
 *
 * @param <T> 局部文档类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticsearchDocumentUpdateRequest<T> {

    /**
     * 索引名称。
     */
    private String indexName;

    /**
     * 文档 ID。
     */
    private String documentId;

    /**
     * 局部更新实体。
     */
    private T document;

    /**
     * 文档完整实体类型，用于更新后回读。
     */
    private Class<?> documentClass;

    /**
     * 不存在时使用的新增实体。
     */
    private Object upsert;

    /**
     * 是否将局部文档作为新增实体。
     */
    private Boolean documentAsUpsert;

    /**
     * 是否检测无变化更新。
     */
    private Boolean detectNoop;

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
     * 冲突重试次数。
     */
    private Integer retryOnConflict;
}
