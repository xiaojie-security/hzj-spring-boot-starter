package com.hzj.elasticsearch.core.document.entity;

import com.hzj.elasticsearch.core.document.enums.ElasticsearchBulkOperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义 Bulk 文档操作。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticsearchBulkOperation {

    /**
     * 操作类型。
     */
    private ElasticsearchBulkOperationType type;

    /**
     * 操作目标索引；为空时使用 Bulk 请求默认索引。
     */
    private String indexName;

    /**
     * 文档 ID。
     */
    private String documentId;

    /**
     * 新增或覆盖的完整文档。
     */
    private Object document;

    /**
     * 更新使用的局部文档。
     */
    private Object partialDocument;

    /**
     * 更新不存在文档时使用的新增文档。
     */
    private Object upsert;

    /**
     * 是否把局部文档作为新增文档。
     */
    private Boolean documentAsUpsert;

}
