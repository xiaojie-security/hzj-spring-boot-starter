package com.hzj.elasticsearch.core.document.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文档获取请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticsearchDocumentGetRequest {

    /**
     * 索引名称。
     */
    private String indexName;

    /**
     * 文档 ID。
     */
    private String documentId;

    /**
     * 返回 source 字段白名单。
     */
    private List<String> sourceIncludes;

    /**
     * 排除的 source 字段。
     */
    private List<String> sourceExcludes;

    /**
     * 路由值。
     */
    private String routing;

    /**
     * 是否实时读取未刷新数据。
     */
    @Builder.Default
    private Boolean realtime = true;
}
