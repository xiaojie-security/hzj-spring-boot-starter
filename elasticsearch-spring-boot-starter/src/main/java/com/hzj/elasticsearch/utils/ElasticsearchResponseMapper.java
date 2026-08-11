package com.hzj.elasticsearch.utils;

import co.elastic.clients.elasticsearch._types.WriteResponseBase;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentHit;
import com.hzj.elasticsearch.core.document.enums.ElasticsearchDocumentWriteResult;
import com.hzj.elasticsearch.core.enums.ElasticsearchOperation;
import com.hzj.elasticsearch.core.entity.ElasticsearchResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 将 Elasticsearch 官方响应转换为 Starter 统一响应。
 */
public final class ElasticsearchResponseMapper {

    private ElasticsearchResponseMapper() {
    }

    /**
     * 映射写入响应。
     *
     * @param response 官方写入响应
     * @param document 业务文档
     * @param operation 操作名称
     * @param <T> 文档类型
     * @return Starter 响应
     */
    public static <T> ElasticsearchResponse<T> write(WriteResponseBase response, T document,
                                                       ElasticsearchOperation operation) {
        ElasticsearchDocumentWriteResult result = ElasticsearchDocumentWriteResult.fromValue(
                response.result() == null ? null : response.result().jsonValue());
        boolean created = result == ElasticsearchDocumentWriteResult.CREATED;
        return ElasticsearchResponse.<T>builder()
                .success(true)
                .operation(operation)
                .message(operation.getValue() + "成功")
                .indexName(response.index())
                .documentId(response.id())
                .data(document)
                .version(response.version())
                .sequenceNumber(response.seqNo())
                .primaryTerm(response.primaryTerm())
                .created(created)
                .build();
    }

    /**
     * 映射获取响应。
     *
     * @param response 官方获取响应
     * @param operation 操作名称
     * @param <T> 文档类型
     * @return Starter 响应
     */
    public static <T> ElasticsearchResponse<T> get(GetResponse<T> response, ElasticsearchOperation operation) {
        return ElasticsearchResponse.<T>builder()
                .success(response.found())
                .operation(operation)
                .message(response.found() ? "获取文档成功" : "文档不存在")
                .indexName(response.index())
                .documentId(response.id())
                .data(response.source())
                .found(response.found())
                .version(response.version())
                .sequenceNumber(response.seqNo())
                .primaryTerm(response.primaryTerm())
                .build();
    }

    /**
     * 映射搜索响应。
     *
     * @param response 官方搜索响应
     * @param operation 操作名称
     * @param <T> 文档类型
     * @return Starter 响应
     */
    public static <T> ElasticsearchResponse<List<ElasticsearchDocumentHit<T>>> search(
            SearchResponse<T> response, ElasticsearchOperation operation) {
        List<ElasticsearchDocumentHit<T>> hits = new ArrayList<>();
        for (Hit<T> hit : response.hits().hits()) {
            hits.add(ElasticsearchDocumentHit.<T>builder()
                    .indexName(hit.index())
                    .documentId(hit.id())
                    .score(hit.score())
                    .document(hit.source())
                    .highlights(hit.highlight())
                    .build());
        }
        long total = response.hits().total() == null ? hits.size() : response.hits().total().value();
        return ElasticsearchResponse.<List<ElasticsearchDocumentHit<T>>>builder()
                .success(true)
                .operation(operation)
                .message("搜索文档成功")
                .data(hits)
                .tookMillis(response.took())
                .total(total)
                .build();
    }

    /**
     * 映射删除响应。
     *
     * @param response 官方删除响应
     * @return Starter 响应
     */
    public static ElasticsearchResponse<Void> delete(DeleteResponse response) {
        ElasticsearchDocumentWriteResult result = ElasticsearchDocumentWriteResult.fromValue(
                response.result() == null ? null : response.result().jsonValue());
        boolean deleted = result == ElasticsearchDocumentWriteResult.DELETED;
        return ElasticsearchResponse.<Void>builder()
                .success(deleted)
                .operation(ElasticsearchOperation.DELETE_DOCUMENT)
                .message(deleted ? "删除文档成功" : "文档不存在")
                .indexName(response.index())
                .documentId(response.id())
                .deleted(deleted)
                .version(response.version())
                .sequenceNumber(response.seqNo())
                .primaryTerm(response.primaryTerm())
                .build();
    }

    /**
     * 创建基础响应。
     *
     * @param operation 操作名称
     * @param message 提示消息
     * @param <T> 数据类型
     * @return 响应构建器
     */
    public static <T> ElasticsearchResponse.ElasticsearchResponseBuilder<T> builder(ElasticsearchOperation operation,
                                                                                        String message) {
        return ElasticsearchResponse.<T>builder()
                .success(true)
                .operation(operation)
                .message(message);
    }
}
