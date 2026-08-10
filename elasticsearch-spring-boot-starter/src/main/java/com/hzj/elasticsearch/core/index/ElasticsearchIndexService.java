package com.hzj.elasticsearch.core.index;

import com.hzj.elasticsearch.core.ElasticsearchService;
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

/**
 * Elasticsearch 索引级操作服务。
 *
 * <p>ES8 中索引直接对应业务表，不存在 type 中间层。</p>
 */
public interface ElasticsearchIndexService extends ElasticsearchService {

    /**
     * 创建索引。
     */
    CreateIndexResponse createIndex(String indexName) throws IOException;

    /**
     * 按官方请求创建索引。
     */
    CreateIndexResponse createIndex(CreateIndexRequest request) throws IOException;

    /**
     * 删除索引。
     */
    DeleteIndexResponse deleteIndex(String indexName) throws IOException;

    /**
     * 按官方请求删除索引。
     */
    DeleteIndexResponse deleteIndex(DeleteIndexRequest request) throws IOException;

    /**
     * 判断索引是否存在。
     */
    BooleanResponse existsIndex(String indexName) throws IOException;

    /**
     * 按官方请求判断索引是否存在。
     */
    BooleanResponse existsIndex(ExistsRequest request) throws IOException;

    /**
     * 获取索引信息。
     */
    GetIndexResponse getIndex(String indexName) throws IOException;

    /**
     * 按官方请求获取索引信息。
     */
    GetIndexResponse getIndex(GetIndexRequest request) throws IOException;

    /**
     * 刷新索引。
     */
    RefreshResponse refreshIndex(String indexName) throws IOException;

    /**
     * 按官方请求刷新索引。
     */
    RefreshResponse refreshIndex(RefreshRequest request) throws IOException;

    /**
     * 打开索引。
     */
    OpenResponse openIndex(String indexName) throws IOException;

    /**
     * 按官方请求打开索引。
     */
    OpenResponse openIndex(OpenRequest request) throws IOException;

    /**
     * 关闭索引。
     */
    CloseIndexResponse closeIndex(String indexName) throws IOException;

    /**
     * 按官方请求关闭索引。
     */
    CloseIndexResponse closeIndex(CloseIndexRequest request) throws IOException;

    /**
     * 更新索引 Mapping。
     */
    PutMappingResponse putMapping(PutMappingRequest request) throws IOException;

    /**
     * 获取索引 Mapping。
     */
    GetMappingResponse getMapping(String indexName) throws IOException;

    /**
     * 按官方请求获取索引 Mapping。
     */
    GetMappingResponse getMapping(GetMappingRequest request) throws IOException;

    /**
     * 获取索引 Settings。
     */
    GetIndicesSettingsResponse getSettings(String indexName) throws IOException;

    /**
     * 按官方请求获取索引 Settings。
     */
    GetIndicesSettingsResponse getSettings(GetIndicesSettingsRequest request) throws IOException;

    /**
     * 更新索引 Settings。
     */
    PutIndicesSettingsResponse putSettings(PutIndicesSettingsRequest request) throws IOException;

    /**
     * 批量更新索引别名。
     */
    UpdateAliasesResponse updateAliases(UpdateAliasesRequest request) throws IOException;

    /**
     * 创建或更新单个索引别名。
     */
    PutAliasResponse putAlias(PutAliasRequest request) throws IOException;

    /**
     * 删除索引别名。
     */
    DeleteAliasResponse deleteAlias(DeleteAliasRequest request) throws IOException;

    /**
     * 获取索引别名。
     */
    GetAliasResponse getAlias(GetAliasRequest request) throws IOException;

    /**
     * 判断索引别名是否存在。
     */
    BooleanResponse existsAlias(ExistsAliasRequest request) throws IOException;
}
