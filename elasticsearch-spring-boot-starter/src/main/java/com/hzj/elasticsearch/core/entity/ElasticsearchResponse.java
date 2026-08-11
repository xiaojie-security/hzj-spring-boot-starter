package com.hzj.elasticsearch.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Elasticsearch Starter 统一响应体。
 *
 * @param <T> 业务数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticsearchResponse<T> {

    /**
     * 是否执行成功。
     */
    private boolean success;

    /**
     * 操作名称。
     */
    private String operation;

    /**
     * 可视化提示信息。
     */
    private String message;

    /**
     * 索引名称。
     */
    private String indexName;

    /**
     * 文档 ID。
     */
    private String documentId;

    /**
     * 业务数据。
     */
    private T data;

    /**
     * 请求耗时，单位毫秒。
     */
    private Long tookMillis;

    /**
     * 文档版本。
     */
    private Long version;

    /**
     * 文档序列号。
     */
    private Long sequenceNumber;

    /**
     * 文档主分片任期。
     */
    private Long primaryTerm;

    /**
     * 文档是否存在。
     */
    private Boolean found;

    /**
     * 是否新创建文档。
     */
    private Boolean created;

    /**
     * 是否删除文档。
     */
    private Boolean deleted;

    /**
     * 索引操作是否确认。
     */
    private Boolean acknowledged;

    /**
     * 分片操作是否确认。
     */
    private Boolean shardsAcknowledged;

    /**
     * 匹配或处理的数据总数。
     */
    private Long total;

    /**
     * 失败数量。
     */
    private Long failed;

    /**
     * 可视化错误信息。
     */
    private List<String> errors;
}
