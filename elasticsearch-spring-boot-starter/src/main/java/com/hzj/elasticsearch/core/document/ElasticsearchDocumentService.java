package com.hzj.elasticsearch.core.document;

import com.hzj.elasticsearch.core.ElasticsearchService;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentBulkRequest;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentDeleteByQueryRequest;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentGetRequest;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentHit;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentMgetRequest;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentRequest;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentSearchRequest;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentUpdateByQueryRequest;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentUpdateRequest;
import com.hzj.elasticsearch.core.entity.ElasticsearchResponse;

import java.io.IOException;
import java.util.List;

/**
 * Elasticsearch 文档级业务操作服务。
 *
 * <p>公开接口只使用 Starter 自定义请求和响应，不暴露 ES Java Client 的 Request 或 Response。</p>
 */
public interface ElasticsearchDocumentService extends ElasticsearchService {

    /**
     * 新增或覆盖文档，并回读 ES 中的完整实体。
     *
     * @param request 自定义文档写入请求
     * @param <T> 文档类型
     * @return 统一文档响应
     * @throws IOException Elasticsearch 请求异常
     */
    <T> ElasticsearchResponse<T> saveDocument(ElasticsearchDocumentRequest<T> request) throws IOException;

    /**
     * 仅在文档不存在时创建文档。
     *
     * @param request 自定义文档创建请求
     * @param <T> 文档类型
     * @return 统一文档响应
     * @throws IOException Elasticsearch 请求异常
     */
    <T> ElasticsearchResponse<T> createDocument(ElasticsearchDocumentRequest<T> request) throws IOException;

    /**
     * 使用便捷参数新增或覆盖文档。
     *
     * @param indexName 索引名称
     * @param documentId 文档 ID
     * @param document 文档实体
     * @param documentClass 文档类型
     * @param <T> 文档类型
     * @return 统一文档响应
     * @throws IOException Elasticsearch 请求异常
     */
    <T> ElasticsearchResponse<T> saveDocument(String indexName, String documentId, T document,
                                                Class<T> documentClass) throws IOException;

    /**
     * 获取文档。
     *
     * @param request 自定义文档读取请求
     * @param documentClass 文档类型
     * @param <T> 文档类型
     * @return 统一文档响应
     * @throws IOException Elasticsearch 请求异常
     */
    <T> ElasticsearchResponse<T> getDocument(ElasticsearchDocumentGetRequest request, Class<T> documentClass)
            throws IOException;

    /**
     * 使用索引和 ID 获取文档。
     *
     * @param indexName 索引名称
     * @param documentId 文档 ID
     * @param documentClass 文档类型
     * @param <T> 文档类型
     * @return 统一文档响应
     * @throws IOException Elasticsearch 请求异常
     */
    <T> ElasticsearchResponse<T> getDocument(String indexName, String documentId, Class<T> documentClass)
            throws IOException;

    /**
     * 判断文档是否存在。
     *
     * @param indexName 索引名称
     * @param documentId 文档 ID
     * @return 统一存在性响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<Void> existsDocument(String indexName, String documentId) throws IOException;

    /**
     * 删除文档。
     *
     * @param indexName 索引名称
     * @param documentId 文档 ID
     * @return 统一删除响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<Void> deleteDocument(String indexName, String documentId) throws IOException;

    /**
     * 更新文档，并回读 ES 中的完整实体。
     *
     * @param request 自定义局部更新请求
     * @param documentClass 完整文档类型
     * @param <T> 局部文档类型
     * @param <R> 完整文档类型
     * @return 统一文档响应
     * @throws IOException Elasticsearch 请求异常
     */
    <T, R> ElasticsearchResponse<R> updateDocument(ElasticsearchDocumentUpdateRequest<T> request,
                                                    Class<R> documentClass) throws IOException;

    /**
     * 使用便捷参数更新文档。
     *
     * @param indexName 索引名称
     * @param documentId 文档 ID
     * @param partialDocument 局部文档
     * @param documentClass 完整文档类型
     * @param <T> 局部文档类型
     * @param <R> 完整文档类型
     * @return 统一文档响应
     * @throws IOException Elasticsearch 请求异常
     */
    <T, R> ElasticsearchResponse<R> updateDocument(String indexName, String documentId, T partialDocument,
                                                    Class<R> documentClass) throws IOException;

    /**
     * 搜索文档。
     *
     * @param request 自定义搜索请求
     * @param documentClass 文档类型
     * @param <T> 文档类型
     * @return 统一搜索响应，data 中包含命中 ID、索引、得分和完整实体
     * @throws IOException Elasticsearch 请求异常
     */
    <T> ElasticsearchResponse<List<ElasticsearchDocumentHit<T>>> searchDocuments(
            ElasticsearchDocumentSearchRequest request, Class<T> documentClass) throws IOException;

    /**
     * 统计文档数量。
     *
     * @param request 自定义搜索条件
     * @return 统一计数响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<Long> countDocuments(ElasticsearchDocumentSearchRequest request) throws IOException;

    /**
     * 批量执行文档操作。
     *
     * @param request 自定义 Bulk 请求
     * @return 统一批量响应，data 中包含每一项的状态和错误信息
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<List<com.hzj.elasticsearch.core.document.entity.ElasticsearchBulkItemResponse>>
    bulkDocuments(ElasticsearchDocumentBulkRequest request) throws IOException;

    /**
     * 按条件批量删除文档。
     *
     * @param request 自定义按查询删除请求
     * @return 统一删除响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<Long> deleteDocumentsByQuery(ElasticsearchDocumentDeleteByQueryRequest request)
            throws IOException;

    /**
     * 按条件批量更新文档。
     *
     * @param request 自定义按查询更新请求
     * @return 统一更新响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<Long> updateDocumentsByQuery(ElasticsearchDocumentUpdateByQueryRequest request)
            throws IOException;

    /**
     * 批量获取文档。
     *
     * @param request 自定义批量获取请求
     * @param documentClass 文档类型
     * @param <T> 文档类型
     * @return 统一批量获取响应
     * @throws IOException Elasticsearch 请求异常
     */
    <T> ElasticsearchResponse<List<ElasticsearchDocumentHit<T>>> multiGetDocuments(
            ElasticsearchDocumentMgetRequest request, Class<T> documentClass) throws IOException;
}
