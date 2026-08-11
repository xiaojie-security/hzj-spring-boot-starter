package com.hzj.elasticsearch.core.document.entity;

import com.hzj.elasticsearch.core.document.enums.ElasticsearchDocumentSortOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文档搜索排序条件。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticsearchDocumentSort {

    /**
     * 排序字段。
     */
    private String field;

    /**
     * 排序方向。
     */
    @Builder.Default
    private ElasticsearchDocumentSortOrder order = ElasticsearchDocumentSortOrder.ASC;
}
