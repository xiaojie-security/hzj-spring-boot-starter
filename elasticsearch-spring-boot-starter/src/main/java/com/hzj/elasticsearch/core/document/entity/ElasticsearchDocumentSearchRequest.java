package com.hzj.elasticsearch.core.document.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文档搜索请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticsearchDocumentSearchRequest {

    /**
     * 查询索引列表。
     */
    private List<String> indexNames;

    /**
     * 查询条件。
     */
    @Builder.Default
    private ElasticsearchQuery query = ElasticsearchQuery.matchAll();

    /**
     * 分页起始位置。
     */
    @Builder.Default
    private Integer from = 0;

    /**
     * 分页大小。
     */
    @Builder.Default
    private Integer size = 10;

    /**
     * 排序条件。
     */
    @Builder.Default
    private List<ElasticsearchDocumentSort> sorts = List.of();

    /**
     * 返回 source 字段白名单。
     */
    private List<String> sourceIncludes;

    /**
     * 排除的 source 字段。
     */
    private List<String> sourceExcludes;

    /**
     * 是否追踪精确总数。
     */
    @Builder.Default
    private Boolean trackTotalHits = true;
}
