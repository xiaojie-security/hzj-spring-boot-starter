package com.hzj.elasticsearch.core.document.impl;

import co.elastic.clients.elasticsearch._types.Conflicts;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.CountRequest;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.ExistsRequest;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.MgetRequest;
import co.elastic.clients.elasticsearch.core.MgetResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateByQueryRequest;
import co.elastic.clients.elasticsearch.core.UpdateByQueryResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.mget.MultiGetResponseItem;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.elasticsearch.core.search.TrackHits;
import com.hzj.elasticsearch.core.AbstractElasticsearchService;
import com.hzj.elasticsearch.core.document.ElasticsearchDocumentService;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchBulkItemResponse;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchBulkOperation;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentBulkRequest;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentDeleteByQueryRequest;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentGetRequest;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentHit;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentMgetRequest;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentRequest;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentSearchRequest;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentScript;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentSort;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentUpdateByQueryRequest;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchDocumentUpdateRequest;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchQuery;
import com.hzj.elasticsearch.core.document.entity.enums.ElasticsearchBulkOperationType;
import com.hzj.elasticsearch.core.document.entity.enums.ElasticsearchConflictPolicy;
import com.hzj.elasticsearch.core.document.entity.enums.ElasticsearchDocumentSortOrder;
import com.hzj.elasticsearch.core.document.entity.enums.ElasticsearchDocumentWriteResult;
import com.hzj.elasticsearch.core.document.entity.enums.ElasticsearchScriptLanguage;
import com.hzj.elasticsearch.core.entity.enums.ElasticsearchOperation;
import com.hzj.elasticsearch.core.entity.enums.ElasticsearchRefreshPolicy;
import com.hzj.elasticsearch.core.entity.ElasticsearchResponse;
import com.hzj.elasticsearch.utils.ElasticsearchQueryConverter;
import com.hzj.elasticsearch.utils.ElasticsearchResponseMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Elasticsearch 文档级业务操作默认实现。
 *
 * <p>ES 官方 Request 和 Response 只在该实现内部使用，公开层只返回 Starter 自定义响应。</p>
 */
public class DefaultElasticsearchDocumentService extends AbstractElasticsearchService
        implements ElasticsearchDocumentService {

    /**
     * 新增或覆盖文档，并回读完整实体。
     *
     * @param request 自定义写入请求
     * @param <T> 文档类型
     * @return 统一文档响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public <T> ElasticsearchResponse<T> saveDocument(ElasticsearchDocumentRequest<T> request) throws IOException {
        IndexResponse response = requireClient().index(buildIndexRequest(request, false));
        T document = readAfterWrite(response.index(), response.id(), request.getDocumentClass(), request.getDocument());
        return ElasticsearchResponseMapper.write(response, document, ElasticsearchOperation.SAVE_DOCUMENT);
    }

    /**
     * 仅在文档不存在时创建文档。
     *
     * @param request 自定义创建请求
     * @param <T> 文档类型
     * @return 统一文档响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public <T> ElasticsearchResponse<T> createDocument(ElasticsearchDocumentRequest<T> request) throws IOException {
        IndexResponse response = requireClient().index(buildIndexRequest(request, true));
        T document = readAfterWrite(response.index(), response.id(), request.getDocumentClass(), request.getDocument());
        return ElasticsearchResponseMapper.write(response, document, ElasticsearchOperation.CREATE_DOCUMENT);
    }

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
    @Override
    public <T> ElasticsearchResponse<T> saveDocument(String indexName, String documentId, T document,
                                                       Class<T> documentClass) throws IOException {
        return saveDocument(ElasticsearchDocumentRequest.<T>builder()
                .indexName(indexName)
                .documentId(documentId)
                .document(document)
                .documentClass(documentClass)
                .build());
    }

    /**
     * 获取文档。
     *
     * @param request 自定义读取请求
     * @param documentClass 文档类型
     * @param <T> 文档类型
     * @return 统一文档响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public <T> ElasticsearchResponse<T> getDocument(ElasticsearchDocumentGetRequest request, Class<T> documentClass)
            throws IOException {
        GetResponse<T> response = requireClient().get(buildGetRequest(request), requireDocumentClass(documentClass));
        return ElasticsearchResponseMapper.get(response, ElasticsearchOperation.GET_DOCUMENT);
    }

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
    @Override
    public <T> ElasticsearchResponse<T> getDocument(String indexName, String documentId, Class<T> documentClass)
            throws IOException {
        return getDocument(ElasticsearchDocumentGetRequest.builder()
                .indexName(indexName)
                .documentId(documentId)
                .build(), documentClass);
    }

    /**
     * 判断文档是否存在。
     *
     * @param indexName 索引名称
     * @param documentId 文档 ID
     * @return 统一存在性响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<Void> existsDocument(String indexName, String documentId) throws IOException {
        boolean exists = requireClient().exists(ExistsRequest.of(request -> request
                .index(requireIndexName(indexName))
                .id(requireDocumentId(documentId)))).value();
        return ElasticsearchResponse.<Void>builder()
                .success(true)
                .operation(ElasticsearchOperation.EXISTS_DOCUMENT)
                .message(exists ? "文档存在" : "文档不存在")
                .indexName(indexName)
                .documentId(documentId)
                .found(exists)
                .build();
    }

    /**
     * 删除文档。
     *
     * @param indexName 索引名称
     * @param documentId 文档 ID
     * @return 统一删除响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<Void> deleteDocument(String indexName, String documentId) throws IOException {
        DeleteResponse response = requireClient().delete(DeleteRequest.of(request -> request
                .index(requireIndexName(indexName))
                .id(requireDocumentId(documentId))));
        return ElasticsearchResponseMapper.delete(response);
    }

    /**
     * 更新文档，并回读完整实体。
     *
     * @param request 自定义更新请求
     * @param documentClass 完整文档类型
     * @param <T> 局部文档类型
     * @param <R> 完整文档类型
     * @return 统一文档响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public <T, R> ElasticsearchResponse<R> updateDocument(ElasticsearchDocumentUpdateRequest<T> request,
                                                            Class<R> documentClass) throws IOException {
        UpdateResponse<R> response = requireClient().update(buildUpdateRequest(request), requireDocumentClass(documentClass));
        R document = readAfterWrite(response.index(), response.id(), documentClass, null);
        return ElasticsearchResponseMapper.write(response, document, ElasticsearchOperation.UPDATE_DOCUMENT);
    }

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
    @Override
    public <T, R> ElasticsearchResponse<R> updateDocument(String indexName, String documentId, T partialDocument,
                                                            Class<R> documentClass) throws IOException {
        return updateDocument(ElasticsearchDocumentUpdateRequest.<T>builder()
                .indexName(indexName)
                .documentId(documentId)
                .document(partialDocument)
                .documentClass(documentClass)
                .build(), documentClass);
    }

    /**
     * 搜索文档。
     *
     * @param request 自定义搜索请求
     * @param documentClass 文档类型
     * @param <T> 文档类型
     * @return 统一搜索响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public <T> ElasticsearchResponse<List<ElasticsearchDocumentHit<T>>> searchDocuments(
            ElasticsearchDocumentSearchRequest request, Class<T> documentClass) throws IOException {
        SearchResponse<T> response = requireClient().search(buildSearchRequest(request), requireDocumentClass(documentClass));
        return ElasticsearchResponseMapper.search(response, ElasticsearchOperation.SEARCH_DOCUMENTS);
    }

    /**
     * 统计文档数量。
     *
     * @param request 自定义搜索条件
     * @return 统一计数响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<Long> countDocuments(ElasticsearchDocumentSearchRequest request) throws IOException {
        CountRequest.Builder builder = new CountRequest.Builder();
        List<String> indexes = requireIndexes(request);
        builder.index(indexes);
        builder.query(ElasticsearchQueryConverter.convert(request.getQuery()));
        CountResponse response = requireClient().count(builder.build());
        return ElasticsearchResponse.<Long>builder()
                .success(true)
                .operation(ElasticsearchOperation.COUNT_DOCUMENTS)
                .message("统计文档成功")
                .data(response.count())
                .total(response.count())
                .build();
    }

    /**
     * 批量执行文档操作。
     *
     * @param request 自定义 Bulk 请求
     * @return 统一批量响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<List<ElasticsearchBulkItemResponse>> bulkDocuments(
            ElasticsearchDocumentBulkRequest request) throws IOException {
        if (request == null || request.getOperations() == null || request.getOperations().isEmpty()) {
            throw new IllegalArgumentException("Bulk 操作不能为空");
        }
        List<BulkOperation> operations = request.getOperations().stream()
                .map(operation -> buildBulkOperation(request.getIndexName(), operation))
                .toList();
        BulkRequest.Builder builder = new BulkRequest.Builder().operations(operations);
        if (hasText(request.getIndexName())) {
            builder.index(request.getIndexName());
        }
        builder.refresh(ElasticsearchQueryConverter.convertRefresh(request.getRefreshPolicy()));
        BulkResponse response = requireClient().bulk(builder.build());
        List<ElasticsearchBulkItemResponse> items = response.items().stream()
                .map(this::convertBulkItem)
                .toList();
        long failed = items.stream().filter(item -> !Boolean.TRUE.equals(item.getSuccess())).count();
        return ElasticsearchResponse.<List<ElasticsearchBulkItemResponse>>builder()
                .success(!response.errors())
                .operation(ElasticsearchOperation.BULK_DOCUMENTS)
                .message(response.errors() ? "Bulk 执行存在失败项" : "Bulk 执行成功")
                .data(items)
                .total((long) items.size())
                .failed(failed)
                .tookMillis(response.took())
                .build();
    }

    /**
     * 按条件批量删除文档。
     *
     * @param request 自定义按查询删除请求
     * @return 统一删除响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<Long> deleteDocumentsByQuery(ElasticsearchDocumentDeleteByQueryRequest request)
            throws IOException {
        DeleteByQueryRequest.Builder builder = new DeleteByQueryRequest.Builder()
                .index(requireIndexName(request.getIndexName()))
                .query(ElasticsearchQueryConverter.convert(request.getQuery()))
                .refresh(isRefreshEnabled(request.getRefreshPolicy()))
                .waitForCompletion(request.getWaitForCompletion());
        if (request.getConflicts() == ElasticsearchConflictPolicy.PROCEED) {
            builder.conflicts(Conflicts.Proceed);
        } else {
            builder.conflicts(Conflicts.Abort);
        }
        DeleteByQueryResponse response = requireClient().deleteByQuery(builder.build());
        return ElasticsearchResponse.<Long>builder()
                .success(response.failures() == null || response.failures().isEmpty())
                .operation(ElasticsearchOperation.DELETE_DOCUMENTS_BY_QUERY)
                .message("按条件删除文档完成")
                .data(response.deleted())
                .total(response.total())
                .failed(response.failures() == null ? 0L : (long) response.failures().size())
                .tookMillis(response.took())
                .build();
    }

    /**
     * 按条件批量更新文档。
     *
     * @param request 自定义按查询更新请求
     * @return 统一更新响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<Long> updateDocumentsByQuery(ElasticsearchDocumentUpdateByQueryRequest request)
            throws IOException {
        UpdateByQueryRequest.Builder builder = new UpdateByQueryRequest.Builder()
                .index(requireIndexName(request.getIndexName()))
                .query(ElasticsearchQueryConverter.convert(request.getQuery()))
                .refresh(isRefreshEnabled(request.getRefreshPolicy()))
                .waitForCompletion(request.getWaitForCompletion());
        if (request.getConflicts() == ElasticsearchConflictPolicy.PROCEED) {
            builder.conflicts(Conflicts.Proceed);
        } else {
            builder.conflicts(Conflicts.Abort);
        }
        builder.script(buildScript(request.getScript()));
        UpdateByQueryResponse response = requireClient().updateByQuery(builder.build());
        return ElasticsearchResponse.<Long>builder()
                .success(response.failures() == null || response.failures().isEmpty())
                .operation(ElasticsearchOperation.UPDATE_DOCUMENTS_BY_QUERY)
                .message("按条件更新文档完成")
                .data(response.updated())
                .total(response.total())
                .failed(response.failures() == null ? 0L : (long) response.failures().size())
                .tookMillis(response.took())
                .build();
    }

    /**
     * 批量获取文档。
     *
     * @param request 自定义批量获取请求
     * @param documentClass 文档类型
     * @param <T> 文档类型
     * @return 统一批量获取响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public <T> ElasticsearchResponse<List<ElasticsearchDocumentHit<T>>> multiGetDocuments(
            ElasticsearchDocumentMgetRequest request, Class<T> documentClass) throws IOException {
        if (request == null || request.getDocumentIds() == null || request.getDocumentIds().isEmpty()) {
            throw new IllegalArgumentException("批量获取文档 ID 不能为空");
        }
        MgetRequest.Builder builder = new MgetRequest.Builder()
                .index(requireIndexName(request.getIndexName()))
                .ids(request.getDocumentIds());
        if (request.getSourceIncludes() != null) {
            builder.sourceIncludes(request.getSourceIncludes());
        }
        if (request.getSourceExcludes() != null) {
            builder.sourceExcludes(request.getSourceExcludes());
        }
        MgetResponse<T> response = requireClient().mget(builder.build(), requireDocumentClass(documentClass));
        List<ElasticsearchDocumentHit<T>> documents = new ArrayList<>();
        for (MultiGetResponseItem<T> item : response.docs()) {
            if (item.isResult()) {
                documents.add(ElasticsearchDocumentHit.<T>builder()
                        .indexName(item.result().index())
                        .documentId(item.result().id())
                        .document(item.result().source())
                        .build());
            }
        }
        return ElasticsearchResponse.<List<ElasticsearchDocumentHit<T>>>builder()
                .success(true)
                .operation(ElasticsearchOperation.MULTI_GET_DOCUMENTS)
                .message("批量获取文档成功")
                .data(documents)
                .total((long) documents.size())
                .build();
    }

    private <T> IndexRequest<T> buildIndexRequest(ElasticsearchDocumentRequest<T> request, boolean createOnly) {
        Objects.requireNonNull(request, "文档请求不能为空");
        IndexRequest.Builder<T> builder = new IndexRequest.Builder<T>()
                .index(requireIndexName(request.getIndexName()))
                .document(Objects.requireNonNull(request.getDocument(), "文档实体不能为空"));
        if (hasText(request.getDocumentId())) {
            builder.id(request.getDocumentId());
        }
        if (createOnly) {
            builder.opType(co.elastic.clients.elasticsearch._types.OpType.Create);
        }
        if (request.getRefreshPolicy() != null) {
            builder.refresh(ElasticsearchQueryConverter.convertRefresh(request.getRefreshPolicy()));
        }
        if (hasText(request.getRouting())) {
            builder.routing(request.getRouting());
        }
        if (hasText(request.getPipeline())) {
            builder.pipeline(request.getPipeline());
        }
        if (request.getIfSequenceNumber() != null) {
            builder.ifSeqNo(request.getIfSequenceNumber());
        }
        if (request.getIfPrimaryTerm() != null) {
            builder.ifPrimaryTerm(request.getIfPrimaryTerm());
        }
        if (request.getRequireAlias() != null) {
            builder.requireAlias(request.getRequireAlias());
        }
        return builder.build();
    }

    private GetRequest buildGetRequest(ElasticsearchDocumentGetRequest request) {
        Objects.requireNonNull(request, "文档读取请求不能为空");
        GetRequest.Builder builder = new GetRequest.Builder()
                .index(requireIndexName(request.getIndexName()))
                .id(requireDocumentId(request.getDocumentId()));
        if (request.getSourceIncludes() != null) {
            builder.sourceIncludes(request.getSourceIncludes());
        }
        if (request.getSourceExcludes() != null) {
            builder.sourceExcludes(request.getSourceExcludes());
        }
        if (hasText(request.getRouting())) {
            builder.routing(request.getRouting());
        }
        if (request.getRealtime() != null) {
            builder.realtime(request.getRealtime());
        }
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private <T, R> UpdateRequest<R, T> buildUpdateRequest(ElasticsearchDocumentUpdateRequest<T> request) {
        Objects.requireNonNull(request, "文档更新请求不能为空");
        UpdateRequest.Builder<R, T> builder = new UpdateRequest.Builder<R, T>()
                .index(requireIndexName(request.getIndexName()))
                .id(requireDocumentId(request.getDocumentId()))
                .doc(Objects.requireNonNull(request.getDocument(), "局部文档不能为空"));
        if (request.getUpsert() != null) {
            builder.upsert((R) request.getUpsert());
        }
        if (request.getDocumentAsUpsert() != null) {
            builder.docAsUpsert(request.getDocumentAsUpsert());
        }
        if (request.getDetectNoop() != null) {
            builder.detectNoop(request.getDetectNoop());
        }
        if (request.getRefreshPolicy() != null) {
            builder.refresh(ElasticsearchQueryConverter.convertRefresh(request.getRefreshPolicy()));
        }
        if (hasText(request.getRouting())) {
            builder.routing(request.getRouting());
        }
        if (request.getRetryOnConflict() != null) {
            builder.retryOnConflict(request.getRetryOnConflict());
        }
        return builder.build();
    }

    private SearchRequest buildSearchRequest(ElasticsearchDocumentSearchRequest request) {
        Objects.requireNonNull(request, "文档搜索请求不能为空");
        SearchRequest.Builder builder = new SearchRequest.Builder()
                .index(requireIndexes(request))
                .query(ElasticsearchQueryConverter.convert(request.getQuery()));
        if (request.getFrom() != null) {
            builder.from(request.getFrom());
        }
        if (request.getSize() != null) {
            builder.size(request.getSize());
        }
        if (request.getTrackTotalHits() != null) {
            builder.trackTotalHits(TrackHits.of(trackHits -> trackHits.enabled(request.getTrackTotalHits())));
        }
        if (request.getSourceIncludes() != null || request.getSourceExcludes() != null) {
            builder.source(source -> source.filter(filter -> {
                if (request.getSourceIncludes() != null) {
                    filter.includes(request.getSourceIncludes());
                }
                if (request.getSourceExcludes() != null) {
                    filter.excludes(request.getSourceExcludes());
                }
                return filter;
            }));
        }
        if (request.getSorts() != null) {
            request.getSorts().stream()
                    .filter(Objects::nonNull)
                    .forEach(sort -> addSort(builder, sort));
        }
        return builder.build();
    }

    private void addSort(SearchRequest.Builder builder, ElasticsearchDocumentSort sort) {
        SortOrder sortOrder = sort.getOrder() == ElasticsearchDocumentSortOrder.DESC
                ? SortOrder.Desc : SortOrder.Asc;
        builder.sort(sortBuilder -> sortBuilder.field(fieldOption -> fieldOption
                .field(requireText(sort.getField(), "排序字段不能为空"))
                .order(sortOrder)));
    }

    private BulkOperation buildBulkOperation(String defaultIndex, ElasticsearchBulkOperation operation) {
        Objects.requireNonNull(operation, "Bulk 操作项不能为空");
        String indexName = hasText(operation.getIndexName()) ? operation.getIndexName() : requireIndexName(defaultIndex);
        return switch (Objects.requireNonNull(operation.getType(), "Bulk 操作类型不能为空")) {
            case INDEX -> BulkOperation.of(builder -> builder.index(index -> {
                index.index(indexName).document(Objects.requireNonNull(operation.getDocument(), "Bulk 文档不能为空"));
                if (hasText(operation.getDocumentId())) {
                    index.id(operation.getDocumentId());
                }
                return index;
            }));
            case CREATE -> BulkOperation.of(builder -> builder.create(create -> {
                create.index(indexName).document(Objects.requireNonNull(operation.getDocument(), "Bulk 文档不能为空"));
                if (hasText(operation.getDocumentId())) {
                    create.id(operation.getDocumentId());
                }
                return create;
            }));
            case UPDATE -> BulkOperation.of(builder -> builder.update(update -> {
                update.index(indexName).id(requireDocumentId(operation.getDocumentId()));
                update.action(action -> {
                    action.doc(Objects.requireNonNull(operation.getPartialDocument(), "Bulk 局部文档不能为空"));
                    if (operation.getUpsert() != null) {
                        action.upsert(operation.getUpsert());
                    }
                    if (operation.getDocumentAsUpsert() != null) {
                        action.docAsUpsert(operation.getDocumentAsUpsert());
                    }
                    return action;
                });
                return update;
            }));
            case DELETE -> BulkOperation.of(builder -> builder.delete(delete -> delete
                    .index(indexName)
                    .id(requireDocumentId(operation.getDocumentId()))));
        };
    }

    private ElasticsearchBulkItemResponse convertBulkItem(BulkResponseItem item) {
        String error = item.error() == null ? null : item.error().reason();
        return ElasticsearchBulkItemResponse.builder()
                .operation(ElasticsearchBulkOperationType.fromValue(
                        item.operationType() == null ? null : item.operationType().jsonValue()))
                .indexName(item.index())
                .documentId(item.id())
                .status(item.status())
                .success(error == null && item.status() < 300)
                .result(ElasticsearchDocumentWriteResult.fromValue(item.result()))
                .error(error)
                .build();
    }

    private Script buildScript(ElasticsearchDocumentScript source) {
        if (source == null || !hasText(source.getSource())) {
            throw new IllegalArgumentException("更新脚本不能为空");
        }
        Script.Builder builder = new Script.Builder()
                .source(source.getSource())
                .lang((source.getLanguage() == null ? ElasticsearchScriptLanguage.PAINLESS
                        : source.getLanguage()).getValue());
        if (source.getParameters() != null) {
            Map<String, JsonData> parameters = source.getParameters().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> JsonData.of(entry.getValue())));
            builder.params(parameters);
        }
        return builder.build();
    }

    private <T> T readAfterWrite(String indexName, String documentId, Class<T> documentClass, T fallback)
            throws IOException {
        if (documentClass == null || !hasText(indexName) || !hasText(documentId)) {
            return fallback;
        }
        GetResponse<T> response = requireClient().get(GetRequest.of(request -> request
                .index(indexName)
                .id(documentId)), documentClass);
        return response.found() ? response.source() : fallback;
    }

    private <T> Class<T> requireDocumentClass(Class<T> documentClass) {
        return Objects.requireNonNull(documentClass, "文档类型不能为空");
    }

    private List<String> requireIndexes(ElasticsearchDocumentSearchRequest request) {
        Objects.requireNonNull(request, "文档搜索请求不能为空");
        if (request.getIndexNames() == null || request.getIndexNames().isEmpty()) {
            throw new IllegalArgumentException("搜索索引不能为空");
        }
        return request.getIndexNames();
    }

    private boolean isRefreshEnabled(ElasticsearchRefreshPolicy policy) {
        return policy != null && policy != ElasticsearchRefreshPolicy.NONE;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String requireText(String value, String message) {
        if (!hasText(value)) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private String requireIndexName(String indexName) {
        return requireText(indexName, "索引名称不能为空");
    }

    private String requireDocumentId(String documentId) {
        return requireText(documentId, "文档 ID 不能为空");
    }
}
