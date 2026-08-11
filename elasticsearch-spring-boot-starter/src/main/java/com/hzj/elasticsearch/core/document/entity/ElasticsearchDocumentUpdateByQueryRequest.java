package com.hzj.elasticsearch.core.document.entity;

import com.hzj.elasticsearch.core.entity.ElasticsearchRefreshPolicy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 按查询更新文档请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticsearchDocumentUpdateByQueryRequest {

    /**
     * 索引名称。
     */
    private String indexName;

    /**
     * 更新条件。
     */
    private ElasticsearchQuery query;

    /**
     * 更新脚本。
     */
    private ElasticsearchDocumentScript script;

    /**
     * 写入后刷新策略。
     */
    @Builder.Default
    private ElasticsearchRefreshPolicy refreshPolicy = ElasticsearchRefreshPolicy.NONE;

    /**
     * 冲突处理策略，默认 abort。
     */
    @Builder.Default
    private String conflicts = "abort";

    /**
     * 是否等待任务完成。
     */
    @Builder.Default
    private Boolean waitForCompletion = true;
}
