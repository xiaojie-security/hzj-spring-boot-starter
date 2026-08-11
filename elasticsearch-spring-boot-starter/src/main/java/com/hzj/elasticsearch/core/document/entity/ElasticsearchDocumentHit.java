package com.hzj.elasticsearch.core.document.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 搜索结果中的文档命中项。
 *
 * @param <T> 文档类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticsearchDocumentHit<T> {

    /**
     * 索引名称。
     */
    private String indexName;

    /**
     * 文档 ID。
     */
    private String documentId;

    /**
     * 相关性得分。
     */
    private Double score;

    /**
     * 文档完整实体。
     */
    private T document;

    /**
     * 高亮字段。
     */
    private Map<String, List<String>> highlights;
}
