package com.hzj.elasticsearch.core.index.impl;

import com.hzj.elasticsearch.core.AbstractElasticsearchService;
import com.hzj.elasticsearch.core.index.ElasticsearchIndexService;
import co.elastic.clients.elasticsearch.indices.CloseIndexRequest;
import co.elastic.clients.elasticsearch.indices.CloseIndexResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteAliasRequest;
import co.elastic.clients.elasticsearch.indices.DeleteAliasResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsAliasRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.GetAliasRequest;
import co.elastic.clients.elasticsearch.indices.GetAliasResponse;
import co.elastic.clients.elasticsearch.indices.GetIndexRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.elasticsearch.indices.GetIndicesSettingsRequest;
import co.elastic.clients.elasticsearch.indices.GetIndicesSettingsResponse;
import co.elastic.clients.elasticsearch.indices.GetMappingRequest;
import co.elastic.clients.elasticsearch.indices.GetMappingResponse;
import co.elastic.clients.elasticsearch.indices.OpenRequest;
import co.elastic.clients.elasticsearch.indices.OpenResponse;
import co.elastic.clients.elasticsearch.indices.PutAliasRequest;
import co.elastic.clients.elasticsearch.indices.PutAliasResponse;
import co.elastic.clients.elasticsearch.indices.PutIndicesSettingsRequest;
import co.elastic.clients.elasticsearch.indices.PutIndicesSettingsResponse;
import co.elastic.clients.elasticsearch.indices.PutMappingRequest;
import co.elastic.clients.elasticsearch.indices.PutMappingResponse;
import co.elastic.clients.elasticsearch.indices.RefreshRequest;
import co.elastic.clients.elasticsearch.indices.RefreshResponse;
import co.elastic.clients.elasticsearch.indices.UpdateAliasesRequest;
import co.elastic.clients.elasticsearch.indices.UpdateAliasesResponse;
import co.elastic.clients.transport.endpoints.BooleanResponse;

import java.io.IOException;
import java.util.Objects;

/**
 * Elasticsearch 索引级操作默认实现。
 */
public class DefaultElasticsearchIndexService extends AbstractElasticsearchService
        implements ElasticsearchIndexService {

    @Override
    public CreateIndexResponse createIndex(String indexName) throws IOException {
        requireIndexName(indexName);
        return createIndex(CreateIndexRequest.of(request -> request.index(indexName)));
    }

    @Override
    public CreateIndexResponse createIndex(CreateIndexRequest request) throws IOException {
        return requireClient().indices().create(Objects.requireNonNull(request, "创建索引请求不能为空"));
    }

    @Override
    public DeleteIndexResponse deleteIndex(String indexName) throws IOException {
        requireIndexName(indexName);
        return deleteIndex(DeleteIndexRequest.of(request -> request.index(indexName)));
    }

    @Override
    public DeleteIndexResponse deleteIndex(DeleteIndexRequest request) throws IOException {
        return requireClient().indices().delete(Objects.requireNonNull(request, "删除索引请求不能为空"));
    }

    @Override
    public BooleanResponse existsIndex(String indexName) throws IOException {
        requireIndexName(indexName);
        return existsIndex(ExistsRequest.of(request -> request.index(indexName)));
    }

    @Override
    public BooleanResponse existsIndex(ExistsRequest request) throws IOException {
        return requireClient().indices().exists(Objects.requireNonNull(request, "判断索引存在性请求不能为空"));
    }

    @Override
    public GetIndexResponse getIndex(String indexName) throws IOException {
        requireIndexName(indexName);
        return getIndex(GetIndexRequest.of(request -> request.index(indexName)));
    }

    @Override
    public GetIndexResponse getIndex(GetIndexRequest request) throws IOException {
        return requireClient().indices().get(Objects.requireNonNull(request, "获取索引请求不能为空"));
    }

    @Override
    public RefreshResponse refreshIndex(String indexName) throws IOException {
        requireIndexName(indexName);
        return refreshIndex(RefreshRequest.of(request -> request.index(indexName)));
    }

    @Override
    public RefreshResponse refreshIndex(RefreshRequest request) throws IOException {
        return requireClient().indices().refresh(Objects.requireNonNull(request, "刷新索引请求不能为空"));
    }

    @Override
    public OpenResponse openIndex(String indexName) throws IOException {
        requireIndexName(indexName);
        return openIndex(OpenRequest.of(request -> request.index(indexName)));
    }

    @Override
    public OpenResponse openIndex(OpenRequest request) throws IOException {
        return requireClient().indices().open(Objects.requireNonNull(request, "打开索引请求不能为空"));
    }

    @Override
    public CloseIndexResponse closeIndex(String indexName) throws IOException {
        requireIndexName(indexName);
        return closeIndex(CloseIndexRequest.of(request -> request.index(indexName)));
    }

    @Override
    public CloseIndexResponse closeIndex(CloseIndexRequest request) throws IOException {
        return requireClient().indices().close(Objects.requireNonNull(request, "关闭索引请求不能为空"));
    }

    @Override
    public PutMappingResponse putMapping(PutMappingRequest request) throws IOException {
        return requireClient().indices().putMapping(Objects.requireNonNull(request, "更新索引 Mapping 请求不能为空"));
    }

    @Override
    public GetMappingResponse getMapping(String indexName) throws IOException {
        requireIndexName(indexName);
        return getMapping(GetMappingRequest.of(request -> request.index(indexName)));
    }

    @Override
    public GetMappingResponse getMapping(GetMappingRequest request) throws IOException {
        return requireClient().indices().getMapping(Objects.requireNonNull(request, "获取索引 Mapping 请求不能为空"));
    }

    @Override
    public GetIndicesSettingsResponse getSettings(String indexName) throws IOException {
        requireIndexName(indexName);
        return getSettings(GetIndicesSettingsRequest.of(request -> request.index(indexName)));
    }

    @Override
    public GetIndicesSettingsResponse getSettings(GetIndicesSettingsRequest request) throws IOException {
        return requireClient().indices().getSettings(Objects.requireNonNull(request, "获取索引 Settings 请求不能为空"));
    }

    @Override
    public PutIndicesSettingsResponse putSettings(PutIndicesSettingsRequest request) throws IOException {
        return requireClient().indices().putSettings(Objects.requireNonNull(request, "更新索引 Settings 请求不能为空"));
    }

    @Override
    public UpdateAliasesResponse updateAliases(UpdateAliasesRequest request) throws IOException {
        return requireClient().indices().updateAliases(Objects.requireNonNull(request, "批量更新索引别名请求不能为空"));
    }

    @Override
    public PutAliasResponse putAlias(PutAliasRequest request) throws IOException {
        return requireClient().indices().putAlias(Objects.requireNonNull(request, "创建索引别名请求不能为空"));
    }

    @Override
    public DeleteAliasResponse deleteAlias(DeleteAliasRequest request) throws IOException {
        return requireClient().indices().deleteAlias(Objects.requireNonNull(request, "删除索引别名请求不能为空"));
    }

    @Override
    public GetAliasResponse getAlias(GetAliasRequest request) throws IOException {
        return requireClient().indices().getAlias(Objects.requireNonNull(request, "获取索引别名请求不能为空"));
    }

    @Override
    public BooleanResponse existsAlias(ExistsAliasRequest request) throws IOException {
        return requireClient().indices().existsAlias(Objects.requireNonNull(request, "判断索引别名存在性请求不能为空"));
    }

    private void requireIndexName(String indexName) {
        if (indexName == null || indexName.trim().isEmpty()) {
            throw new IllegalArgumentException("索引名称不能为空");
        }
    }
}
