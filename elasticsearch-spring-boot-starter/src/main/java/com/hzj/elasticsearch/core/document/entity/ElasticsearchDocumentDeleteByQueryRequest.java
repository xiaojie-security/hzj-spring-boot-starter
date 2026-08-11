package com.hzj.elasticsearch.core.document.entity;

import com.hzj.elasticsearch.core.document.enums.ElasticsearchConflictPolicy;
import com.hzj.elasticsearch.core.enums.ElasticsearchRefreshPolicy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 按查询删除文档请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticsearchDocumentDeleteByQueryRequest {

    /**
     * 索引名称。
     */
    private String indexName;

    /**
     * 删除条件。
     */
    private ElasticsearchQuery query;

    /**
     * 写入后刷新策略。
     */
    @Builder.Default
    private ElasticsearchRefreshPolicy refreshPolicy = ElasticsearchRefreshPolicy.NONE;

    /**
     * 冲突处理策略，默认 abort。
     */
    @Builder.Default
    private ElasticsearchConflictPolicy conflicts = ElasticsearchConflictPolicy.ABORT;

    /**
     * 是否等待任务完成。
     */
    @Builder.Default
    private Boolean waitForCompletion = true;
}
