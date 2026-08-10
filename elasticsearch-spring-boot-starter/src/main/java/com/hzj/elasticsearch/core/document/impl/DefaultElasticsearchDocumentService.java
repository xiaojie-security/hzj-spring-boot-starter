package com.hzj.elasticsearch.core.document.impl;

import co.elastic.clients.elasticsearch._types.OpType;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.ClearScrollRequest;
import co.elastic.clients.elasticsearch.core.ClearScrollResponse;
import co.elastic.clients.elasticsearch.core.CountRequest;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.ExistsRequest;
import co.elastic.clients.elasticsearch.core.ExistsSourceRequest;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.GetSourceRequest;
import co.elastic.clients.elasticsearch.core.GetSourceResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.MgetRequest;
import co.elastic.clients.elasticsearch.core.MgetResponse;
import co.elastic.clients.elasticsearch.core.ReindexRequest;
import co.elastic.clients.elasticsearch.core.ReindexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.ScrollRequest;
import co.elastic.clients.elasticsearch.core.ScrollResponse;
import co.elastic.clients.elasticsearch.core.UpdateByQueryRequest;
import co.elastic.clients.elasticsearch.core.UpdateByQueryResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.hzj.elasticsearch.core.AbstractElasticsearchService;
import com.hzj.elasticsearch.core.document.ElasticsearchDocumentService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Elasticsearch 文档级操作默认实现。
 *
 * <p>实现只使用索引名称和文档 ID，不引入 ES8 已删除的 type 参数。</p>
 */
public class DefaultElasticsearchDocumentService extends AbstractElasticsearchService
        implements ElasticsearchDocumentService {

    /**
     * 写入或覆盖文档。
     *
     * @param request 官方索引请求
     * @param <TDocument> 文档类型
     * @return 写入响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public <TDocument> IndexResponse index(IndexRequest<TDocument> request) throws IOException {
        return requireClient().index(Objects.requireNonNull(request, "写入文档请求不能为空"));
    }

    /**
     * 按索引、文档 ID 和文档内容写入文档。
     *
     * @param indexName 索引名称
     * @param documentId 文档 ID
     * @param document 文档内容
     * @param <TDocument> 文档类型
     * @return 写入响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public <TDocument> IndexResponse index(String indexName, String documentId, TDocument document)
            throws IOException {
        return index(IndexRequest.of(request -> request
                .index(requireIndexName(indexName))
                .id(requireDocumentId(documentId))
                .document(Objects.requireNonNull(document, "文档内容不能为空"))));
    }

    /**
     * 仅在文档不存在时创建文档。
     *
     * @param indexName 索引名称
     * @param documentId 文档 ID
     * @param document 文档内容
     * @param <TDocument> 文档类型
     * @return 创建响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public <TDocument> IndexResponse createDocument(String indexName, String documentId, TDocument document)
            throws IOException {
        return index(IndexRequest.of(request -> request
                .index(requireIndexName(indexName))
                .id(requireDocumentId(documentId))
                .opType(OpType.Create)
                .document(Objects.requireNonNull(document, "文档内容不能为空"))));
    }

    /**
     * 按官方请求获取文档。
     *
     * @param request 官方获取请求
     * @param documentClass 文档类型
     * @param <TDocument> 文档类型
     * @return 获取响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public <TDocument> GetResponse<TDocument> get(GetRequest request, Class<TDocument> documentClass)
            throws IOException {
        return requireClient().get(Objects.requireNonNull(request, "获取文档请求不能为空"),
                Objects.requireNonNull(documentClass, "文档类型不能为空"));
    }

    /**
     * 获取未绑定具体 Java 类型的文档。
     *
     * @param request 官方获取请求
     * @return 获取响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public GetResponse<JsonData> get(GetRequest request) throws IOException {
        return requireClient().get(Objects.requireNonNull(request, "获取文档请求不能为空"), JsonData.class);
    }

    /**
     * 按索引和文档 ID 获取文档。
     *
     * @param indexName 索引名称
     * @param documentId 文档 ID
     * @param documentClass 文档类型
     * @param <TDocument> 文档类型
     * @return 获取响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public <TDocument> GetResponse<TDocument> get(String indexName, String documentId, Class<TDocument> documentClass)
            throws IOException {
        return get(GetRequest.of(request -> request
                .index(requireIndexName(indexName))
                .id(requireDocumentId(documentId))), documentClass);
    }

    /**
     * 按官方请求判断文档是否存在。
     *
     * @param request 官方存在性请求
     * @return 存在性响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public BooleanResponse exists(ExistsRequest request) throws IOException {
        return requireClient().exists(Objects.requireNonNull(request, "判断文档存在性请求不能为空"));
    }

    /**
     * 按索引和文档 ID 判断文档是否存在。
     *
     * @param indexName 索引名称
     * @param documentId 文档 ID
     * @return 存在性响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public BooleanResponse exists(String indexName, String documentId) throws IOException {
        return exists(ExistsRequest.of(request -> request
                .index(requireIndexName(indexName))
                .id(requireDocumentId(documentId))));
    }

    /**
     * 按官方请求判断文档 source 是否存在。
     *
     * @param request 官方 source 存在性请求
     * @return 存在性响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public BooleanResponse existsSource(ExistsSourceRequest request) throws IOException {
        return requireClient().existsSource(Objects.requireNonNull(request, "判断文档 source 存在性请求不能为空"));
    }

    /**
     * 按官方请求获取文档 source。
     *
     * @param request 官方 source 获取请求
     * @param documentClass 文档类型
     * @param <TDocument> 文档类型
     * @return source 响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public <TDocument> GetSourceResponse<TDocument> getSource(GetSourceRequest request, Class<TDocument> documentClass)
            throws IOException {
        return requireClient().getSource(Objects.requireNonNull(request, "获取文档 source 请求不能为空"),
                Objects.requireNonNull(documentClass, "文档类型不能为空"));
    }

    /**
     * 获取未绑定具体 Java 类型的文档 source。
     *
     * @param request 官方 source 获取请求
     * @return source 响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public GetSourceResponse<JsonData> getSource(GetSourceRequest request) throws IOException {
        return requireClient().getSource(Objects.requireNonNull(request, "获取文档 source 请求不能为空"), JsonData.class);
    }

    /**
     * 按官方请求删除文档。
     *
     * @param request 官方删除请求
     * @return 删除响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public DeleteResponse delete(DeleteRequest request) throws IOException {
        return requireClient().delete(Objects.requireNonNull(request, "删除文档请求不能为空"));
    }

    /**
     * 按索引和文档 ID 删除文档。
     *
     * @param indexName 索引名称
     * @param documentId 文档 ID
     * @return 删除响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public DeleteResponse delete(String indexName, String documentId) throws IOException {
        return delete(DeleteRequest.of(request -> request
                .index(requireIndexName(indexName))
                .id(requireDocumentId(documentId))));
    }

    /**
     * 按官方请求更新文档。
     *
     * @param request 官方更新请求
     * @param documentClass 返回文档类型
     * @param <TDocument> 返回文档类型
     * @param <TPartialDocument> 局部更新类型
     * @return 更新响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public <TDocument, TPartialDocument> UpdateResponse<TDocument> update(
            UpdateRequest<TDocument, TPartialDocument> request, Class<TDocument> documentClass) throws IOException {
        return requireClient().update(Objects.requireNonNull(request, "更新文档请求不能为空"),
                Objects.requireNonNull(documentClass, "文档类型不能为空"));
    }

    /**
     * 更新未绑定具体 Java 类型的文档。
     *
     * @param request 官方更新请求
     * @return 更新响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public UpdateResponse<JsonData> update(UpdateRequest<JsonData, JsonData> request) throws IOException {
        return requireClient().update(Objects.requireNonNull(request, "更新文档请求不能为空"), JsonData.class);
    }

    /**
     * 按索引、文档 ID 和局部文档更新文档。
     *
     * @param indexName 索引名称
     * @param documentId 文档 ID
     * @param partialDocument 局部文档
     * @param documentClass 返回文档类型
     * @param <TDocument> 返回文档类型
     * @param <TPartialDocument> 局部更新类型
     * @return 更新响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public <TDocument, TPartialDocument> UpdateResponse<TDocument> update(
            String indexName, String documentId, TPartialDocument partialDocument, Class<TDocument> documentClass)
            throws IOException {
        UpdateRequest<TDocument, TPartialDocument> request = UpdateRequest.of(builder -> builder
                .index(requireIndexName(indexName))
                .id(requireDocumentId(documentId))
                .doc(Objects.requireNonNull(partialDocument, "局部文档不能为空")));
        return update(request, documentClass);
    }

    /**
     * 按官方请求搜索文档。
     *
     * @param request 官方搜索请求
     * @param documentClass 文档类型
     * @param <TDocument> 文档类型
     * @return 搜索响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public <TDocument> SearchResponse<TDocument> search(SearchRequest request, Class<TDocument> documentClass)
            throws IOException {
        return requireClient().search(Objects.requireNonNull(request, "搜索文档请求不能为空"),
                Objects.requireNonNull(documentClass, "文档类型不能为空"));
    }

    /**
     * 搜索未绑定具体 Java 类型的文档。
     *
     * @param request 官方搜索请求
     * @return 搜索响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public SearchResponse<JsonData> search(SearchRequest request) throws IOException {
        return requireClient().search(Objects.requireNonNull(request, "搜索文档请求不能为空"), JsonData.class);
    }

    /**
     * 按索引和查询条件搜索文档。
     *
     * @param indexName 索引名称
     * @param query 查询条件，可以为空
     * @param documentClass 文档类型
     * @param <TDocument> 文档类型
     * @return 搜索响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public <TDocument> SearchResponse<TDocument> search(String indexName, Query query, Class<TDocument> documentClass)
            throws IOException {
        SearchRequest.Builder builder = new SearchRequest.Builder().index(requireIndexName(indexName));
        if (query != null) {
            builder.query(query);
        }
        return search(builder.build(), documentClass);
    }

    /**
     * 按官方请求统计文档数量。
     *
     * @param request 官方统计请求
     * @return 统计响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public CountResponse count(CountRequest request) throws IOException {
        return requireClient().count(Objects.requireNonNull(request, "统计文档请求不能为空"));
    }

    /**
     * 统计指定索引的文档数量。
     *
     * @param indexName 索引名称
     * @return 统计响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public CountResponse count(String indexName) throws IOException {
        return count(CountRequest.of(request -> request.index(requireIndexName(indexName))));
    }

    /**
     * 按官方请求执行 Bulk 批量操作。
     *
     * @param request 官方 Bulk 请求
     * @return 批量操作响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public BulkResponse bulk(BulkRequest request) throws IOException {
        return requireClient().bulk(Objects.requireNonNull(request, "Bulk 请求不能为空"));
    }

    /**
     * 按索引和批量操作列表执行 Bulk 操作。
     *
     * @param indexName 默认索引名称
     * @param operations 批量操作列表
     * @return 批量操作响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public BulkResponse bulk(String indexName, List<BulkOperation> operations) throws IOException {
        if (operations == null || operations.isEmpty()) {
            throw new IllegalArgumentException("Bulk 操作列表不能为空");
        }
        return bulk(BulkRequest.of(request -> request
                .index(requireIndexName(indexName))
                .operations(operations)));
    }

    /**
     * 按官方请求批量删除匹配文档。
     *
     * @param request 官方按查询删除请求
     * @return 删除响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public DeleteByQueryResponse deleteByQuery(DeleteByQueryRequest request) throws IOException {
        return requireClient().deleteByQuery(Objects.requireNonNull(request, "按查询删除文档请求不能为空"));
    }

    /**
     * 按索引和查询条件批量删除文档。
     *
     * @param indexName 索引名称
     * @param query 查询条件
     * @return 删除响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public DeleteByQueryResponse deleteByQuery(String indexName, Query query) throws IOException {
        return deleteByQuery(DeleteByQueryRequest.of(request -> request
                .index(requireIndexName(indexName))
                .query(Objects.requireNonNull(query, "按查询删除条件不能为空"))));
    }

    /**
     * 按官方请求批量更新匹配文档。
     *
     * @param request 官方按查询更新请求
     * @return 更新响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public UpdateByQueryResponse updateByQuery(UpdateByQueryRequest request) throws IOException {
        return requireClient().updateByQuery(Objects.requireNonNull(request, "按查询更新文档请求不能为空"));
    }

    /**
     * 按官方请求批量获取文档。
     *
     * @param request 官方批量获取请求
     * @param documentClass 文档类型
     * @param <TDocument> 文档类型
     * @return 批量获取响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public <TDocument> MgetResponse<TDocument> mget(MgetRequest request, Class<TDocument> documentClass)
            throws IOException {
        return requireClient().mget(Objects.requireNonNull(request, "批量获取文档请求不能为空"),
                Objects.requireNonNull(documentClass, "文档类型不能为空"));
    }

    /**
     * 批量获取未绑定具体 Java 类型的文档。
     *
     * @param request 官方批量获取请求
     * @return 批量获取响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public MgetResponse<JsonData> mget(MgetRequest request) throws IOException {
        return requireClient().mget(Objects.requireNonNull(request, "批量获取文档请求不能为空"), JsonData.class);
    }

    /**
     * 按索引和文档 ID 列表批量获取文档。
     *
     * @param indexName 索引名称
     * @param documentIds 文档 ID 列表
     * @param documentClass 文档类型
     * @param <TDocument> 文档类型
     * @return 批量获取响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public <TDocument> MgetResponse<TDocument> mget(String indexName, List<String> documentIds,
                                                     Class<TDocument> documentClass) throws IOException {
        if (documentIds == null || documentIds.isEmpty()) {
            throw new IllegalArgumentException("批量获取文档 ID 列表不能为空");
        }
        return mget(MgetRequest.of(request -> request
                .index(requireIndexName(indexName))
                .ids(documentIds)), documentClass);
    }

    /**
     * 按官方请求执行滚动查询。
     *
     * @param request 官方滚动请求
     * @param documentClass 文档类型
     * @param <TDocument> 文档类型
     * @return 滚动响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public <TDocument> ScrollResponse<TDocument> scroll(ScrollRequest request, Class<TDocument> documentClass)
            throws IOException {
        return requireClient().scroll(Objects.requireNonNull(request, "滚动查询请求不能为空"),
                Objects.requireNonNull(documentClass, "文档类型不能为空"));
    }

    /**
     * 清理滚动查询上下文。
     *
     * @param request 官方清理滚动请求
     * @return 清理响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ClearScrollResponse clearScroll(ClearScrollRequest request) throws IOException {
        return requireClient().clearScroll(Objects.requireNonNull(request, "清理滚动查询请求不能为空"));
    }

    /**
     * 按官方请求执行重建索引。
     *
     * @param request 官方重建索引请求
     * @return 重建索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ReindexResponse reindex(ReindexRequest request) throws IOException {
        return requireClient().reindex(Objects.requireNonNull(request, "重建索引请求不能为空"));
    }

    /**
     * 校验索引名称。
     *
     * @param indexName 索引名称
     * @return 原始索引名称
     */
    private String requireIndexName(String indexName) {
        if (indexName == null || indexName.trim().isEmpty()) {
            throw new IllegalArgumentException("索引名称不能为空");
        }
        return indexName;
    }

    /**
     * 校验文档 ID。
     *
     * @param documentId 文档 ID
     * @return 原始文档 ID
     */
    private String requireDocumentId(String documentId) {
        if (documentId == null || documentId.trim().isEmpty()) {
            throw new IllegalArgumentException("文档 ID 不能为空");
        }
        return documentId;
    }
}
