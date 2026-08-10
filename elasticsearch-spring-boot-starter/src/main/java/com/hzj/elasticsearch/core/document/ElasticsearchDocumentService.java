package com.hzj.elasticsearch.core.document;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.hzj.elasticsearch.core.ElasticsearchService;

import java.io.IOException;
import java.util.List;

/**
 * Elasticsearch 文档级操作服务。
 *
 * <p>ES8 中索引直接对应业务表，文档直接对应索引中的一条记录，不存在 type 参数。</p>
 */
public interface ElasticsearchDocumentService extends ElasticsearchService {

    /**
     * 写入或覆盖文档。
     *
     * @param request 官方索引请求
     * @param <TDocument> 文档类型
     * @return 写入响应
     * @throws IOException Elasticsearch 请求异常
     */
    <TDocument> IndexResponse index(IndexRequest<TDocument> request) throws IOException;

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
    <TDocument> IndexResponse index(String indexName, String documentId, TDocument document) throws IOException;

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
    <TDocument> IndexResponse createDocument(String indexName, String documentId, TDocument document) throws IOException;

    /**
     * 按官方请求获取文档。
     *
     * @param request 官方获取请求
     * @param documentClass 文档类型
     * @param <TDocument> 文档类型
     * @return 获取响应
     * @throws IOException Elasticsearch 请求异常
     */
    <TDocument> GetResponse<TDocument> get(GetRequest request, Class<TDocument> documentClass) throws IOException;

    /**
     * 获取未绑定具体 Java 类型的文档。
     *
     * @param request 官方获取请求
     * @return 获取响应
     * @throws IOException Elasticsearch 请求异常
     */
    GetResponse<JsonData> get(GetRequest request) throws IOException;

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
    <TDocument> GetResponse<TDocument> get(String indexName, String documentId, Class<TDocument> documentClass)
            throws IOException;

    /**
     * 按官方请求判断文档是否存在。
     *
     * @param request 官方存在性请求
     * @return 存在性响应
     * @throws IOException Elasticsearch 请求异常
     */
    BooleanResponse exists(ExistsRequest request) throws IOException;

    /**
     * 按索引和文档 ID 判断文档是否存在。
     *
     * @param indexName 索引名称
     * @param documentId 文档 ID
     * @return 存在性响应
     * @throws IOException Elasticsearch 请求异常
     */
    BooleanResponse exists(String indexName, String documentId) throws IOException;

    /**
     * 按官方请求判断文档 source 是否存在。
     *
     * @param request 官方 source 存在性请求
     * @return 存在性响应
     * @throws IOException Elasticsearch 请求异常
     */
    BooleanResponse existsSource(ExistsSourceRequest request) throws IOException;

    /**
     * 按官方请求获取文档 source。
     *
     * @param request 官方 source 获取请求
     * @param documentClass 文档类型
     * @param <TDocument> 文档类型
     * @return source 响应
     * @throws IOException Elasticsearch 请求异常
     */
    <TDocument> GetSourceResponse<TDocument> getSource(GetSourceRequest request, Class<TDocument> documentClass)
            throws IOException;

    /**
     * 获取未绑定具体 Java 类型的文档 source。
     *
     * @param request 官方 source 获取请求
     * @return source 响应
     * @throws IOException Elasticsearch 请求异常
     */
    GetSourceResponse<JsonData> getSource(GetSourceRequest request) throws IOException;

    /**
     * 按官方请求删除文档。
     *
     * @param request 官方删除请求
     * @return 删除响应
     * @throws IOException Elasticsearch 请求异常
     */
    DeleteResponse delete(DeleteRequest request) throws IOException;

    /**
     * 按索引和文档 ID 删除文档。
     *
     * @param indexName 索引名称
     * @param documentId 文档 ID
     * @return 删除响应
     * @throws IOException Elasticsearch 请求异常
     */
    DeleteResponse delete(String indexName, String documentId) throws IOException;

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
    <TDocument, TPartialDocument> UpdateResponse<TDocument> update(
            UpdateRequest<TDocument, TPartialDocument> request, Class<TDocument> documentClass) throws IOException;

    /**
     * 更新未绑定具体 Java 类型的文档。
     *
     * @param request 官方更新请求
     * @return 更新响应
     * @throws IOException Elasticsearch 请求异常
     */
    UpdateResponse<JsonData> update(UpdateRequest<JsonData, JsonData> request) throws IOException;

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
    <TDocument, TPartialDocument> UpdateResponse<TDocument> update(
            String indexName, String documentId, TPartialDocument partialDocument, Class<TDocument> documentClass)
            throws IOException;

    /**
     * 按官方请求搜索文档。
     *
     * @param request 官方搜索请求
     * @param documentClass 文档类型
     * @param <TDocument> 文档类型
     * @return 搜索响应
     * @throws IOException Elasticsearch 请求异常
     */
    <TDocument> SearchResponse<TDocument> search(SearchRequest request, Class<TDocument> documentClass)
            throws IOException;

    /**
     * 搜索未绑定具体 Java 类型的文档。
     *
     * @param request 官方搜索请求
     * @return 搜索响应
     * @throws IOException Elasticsearch 请求异常
     */
    SearchResponse<JsonData> search(SearchRequest request) throws IOException;

    /**
     * 按索引和查询条件搜索文档。
     *
     * @param indexName 索引名称
     * @param query 查询条件，可以为空；为空时由 Elasticsearch 执行默认查询
     * @param documentClass 文档类型
     * @param <TDocument> 文档类型
     * @return 搜索响应
     * @throws IOException Elasticsearch 请求异常
     */
    <TDocument> SearchResponse<TDocument> search(String indexName, Query query, Class<TDocument> documentClass)
            throws IOException;

    /**
     * 按官方请求统计文档数量。
     *
     * @param request 官方统计请求
     * @return 统计响应
     * @throws IOException Elasticsearch 请求异常
     */
    CountResponse count(CountRequest request) throws IOException;

    /**
     * 统计指定索引的文档数量。
     *
     * @param indexName 索引名称
     * @return 统计响应
     * @throws IOException Elasticsearch 请求异常
     */
    CountResponse count(String indexName) throws IOException;

    /**
     * 按官方请求执行 Bulk 批量操作。
     *
     * @param request 官方 Bulk 请求
     * @return 批量操作响应
     * @throws IOException Elasticsearch 请求异常
     */
    BulkResponse bulk(BulkRequest request) throws IOException;

    /**
     * 按索引和批量操作列表执行 Bulk 操作。
     *
     * @param indexName 默认索引名称
     * @param operations 批量操作列表
     * @return 批量操作响应
     * @throws IOException Elasticsearch 请求异常
     */
    BulkResponse bulk(String indexName, List<BulkOperation> operations) throws IOException;

    /**
     * 按官方请求批量删除匹配文档。
     *
     * @param request 官方按查询删除请求
     * @return 删除响应
     * @throws IOException Elasticsearch 请求异常
     */
    DeleteByQueryResponse deleteByQuery(DeleteByQueryRequest request) throws IOException;

    /**
     * 按索引和查询条件批量删除文档。
     *
     * @param indexName 索引名称
     * @param query 查询条件
     * @return 删除响应
     * @throws IOException Elasticsearch 请求异常
     */
    DeleteByQueryResponse deleteByQuery(String indexName, Query query) throws IOException;

    /**
     * 按官方请求批量更新匹配文档。
     *
     * @param request 官方按查询更新请求
     * @return 更新响应
     * @throws IOException Elasticsearch 请求异常
     */
    UpdateByQueryResponse updateByQuery(UpdateByQueryRequest request) throws IOException;

    /**
     * 按官方请求批量获取文档。
     *
     * @param request 官方批量获取请求
     * @param documentClass 文档类型
     * @param <TDocument> 文档类型
     * @return 批量获取响应
     * @throws IOException Elasticsearch 请求异常
     */
    <TDocument> MgetResponse<TDocument> mget(MgetRequest request, Class<TDocument> documentClass)
            throws IOException;

    /**
     * 批量获取未绑定具体 Java 类型的文档。
     *
     * @param request 官方批量获取请求
     * @return 批量获取响应
     * @throws IOException Elasticsearch 请求异常
     */
    MgetResponse<JsonData> mget(MgetRequest request) throws IOException;

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
    <TDocument> MgetResponse<TDocument> mget(String indexName, List<String> documentIds,
                                               Class<TDocument> documentClass) throws IOException;

    /**
     * 按官方请求执行滚动查询。
     *
     * @param request 官方滚动请求
     * @param documentClass 文档类型
     * @param <TDocument> 文档类型
     * @return 滚动响应
     * @throws IOException Elasticsearch 请求异常
     */
    <TDocument> ScrollResponse<TDocument> scroll(ScrollRequest request, Class<TDocument> documentClass)
            throws IOException;

    /**
     * 清理滚动查询上下文。
     *
     * @param request 官方清理滚动请求
     * @return 清理响应
     * @throws IOException Elasticsearch 请求异常
     */
    ClearScrollResponse clearScroll(ClearScrollRequest request) throws IOException;

    /**
     * 按官方请求执行重建索引。
     *
     * @param request 官方重建索引请求
     * @return 重建索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    ReindexResponse reindex(ReindexRequest request) throws IOException;
}
