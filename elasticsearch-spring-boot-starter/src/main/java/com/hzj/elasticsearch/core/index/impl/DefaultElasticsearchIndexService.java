package com.hzj.elasticsearch.core.index.impl;

import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.util.WithJsonObjectBuilderBase;
import com.hzj.elasticsearch.core.entity.ElasticsearchResponse;
import com.hzj.elasticsearch.core.enums.ElasticsearchOperation;
import com.hzj.elasticsearch.core.client.AbstractElasticsearchClientService;
import com.hzj.elasticsearch.core.index.ElasticsearchIndexService;
import com.hzj.elasticsearch.core.index.entity.ElasticsearchIndexAliasRequest;
import com.hzj.elasticsearch.core.index.entity.ElasticsearchIndexCreateRequest;
import com.hzj.elasticsearch.core.index.entity.ElasticsearchIndexMappingRequest;
import com.hzj.elasticsearch.core.index.entity.ElasticsearchIndexSettingsRequest;
import com.hzj.elasticsearch.provider.es.ElasticsearchConfigProvider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Elasticsearch 索引级业务操作默认实现。
 *
 * <p>官方请求和响应只在实现内部使用，公开层统一使用 Starter 自定义对象。</p>
 */
public class DefaultElasticsearchIndexService
    extends AbstractElasticsearchClientService
        implements ElasticsearchIndexService {


    public DefaultElasticsearchIndexService(ConfigurableListableBeanFactory beanFactory, ElasticsearchConfigProvider configProvider) {
        super(beanFactory, configProvider);
    }

    /**
     * 创建索引。
     *
     * @param request 自定义索引创建请求
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<Void> createIndex(ElasticsearchIndexCreateRequest request) throws IOException {
        Objects.requireNonNull(request, "创建索引请求不能为空");
        String indexName = requireIndexName(request.getIndexName());
        Map<String, Object> body = new LinkedHashMap<>();
        Map<String, Object> settings = new LinkedHashMap<>();
        if (request.getSettings() != null) {
            settings.putAll(request.getSettings());
        }
        if (request.getNumberOfShards() != null) {
            settings.put("number_of_shards", request.getNumberOfShards());
        }
        if (request.getNumberOfReplicas() != null) {
            settings.put("number_of_replicas", request.getNumberOfReplicas());
        }
        if (!settings.isEmpty()) {
            body.put("settings", settings);
        }
        if (request.getMappings() != null && !request.getMappings().isEmpty()) {
            body.put("mappings", request.getMappings());
        }
        CreateIndexRequest.Builder builder = new CreateIndexRequest.Builder().index(indexName);
        applyJsonBody(builder, body);
        CreateIndexResponse response = getClient().indices().create(builder.build());
        return acknowledgedResponse(response.acknowledged(), response.shardsAcknowledged(),
                ElasticsearchOperation.CREATE_INDEX, "创建索引成功", indexName);
    }

    /**
     * 使用索引名称创建索引。
     *
     * @param indexName 索引名称
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<Void> createIndex(String indexName) throws IOException {
        return createIndex(ElasticsearchIndexCreateRequest.builder().indexName(indexName).build());
    }

    /**
     * 删除索引。
     *
     * @param indexName 索引名称
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<Void> deleteIndex(String indexName) throws IOException {
        final String validatedIndexName = requireIndexName(indexName);
        DeleteIndexResponse response = getClient().indices().delete(DeleteIndexRequest.of(request -> request
                .index(validatedIndexName)));
        return acknowledgedResponse(response.acknowledged(), null, ElasticsearchOperation.DELETE_INDEX,
                "删除索引成功", validatedIndexName);
    }

    /**
     * 判断索引是否存在。
     *
     * @param indexName 索引名称
     * @return 统一索引存在性响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<Boolean> existsIndex(String indexName) throws IOException {
        final String validatedIndexName = requireIndexName(indexName);
        BooleanResponse response = getClient().indices().exists(ExistsRequestFactory.of(validatedIndexName));
        return ElasticsearchResponse.<Boolean>builder()
                .success(true)
                .operation(ElasticsearchOperation.EXISTS_INDEX)
                .message(response.value() ? "索引存在" : "索引不存在")
                .indexName(validatedIndexName)
                .data(response.value())
                .found(response.value())
                .build();
    }

    /**
     * 获取索引基本信息的可视化 JSON。
     *
     * @param indexName 索引名称
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<String> getIndex(String indexName) throws IOException {
        final String validatedIndexName = requireIndexName(indexName);
        GetIndexResponse response = getClient().indices().get(GetIndexRequest.of(request -> request
                .index(validatedIndexName)));
        return ElasticsearchResponse.<String>builder()
                .success(true)
                .operation(ElasticsearchOperation.GET_INDEX)
                .message("获取索引信息成功")
                .indexName(validatedIndexName)
                .data(toJson(response.result()))
                .build();
    }

    /**
     * 刷新索引。
     *
     * @param indexName 索引名称
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<Void> refreshIndex(String indexName) throws IOException {
        final String validatedIndexName = requireIndexName(indexName);
        RefreshResponse response = getClient().indices().refresh(RefreshRequest.of(request -> request
                .index(validatedIndexName)));
        return shardResponse(response.shards().successful().intValue() > 0, ElasticsearchOperation.REFRESH_INDEX,
                "刷新索引完成", validatedIndexName);
    }

    /**
     * 打开索引。
     *
     * @param indexName 索引名称
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<Void> openIndex(String indexName) throws IOException {
        final String validatedIndexName = requireIndexName(indexName);
        OpenResponse response = getClient().indices().open(OpenRequest.of(request -> request
                .index(validatedIndexName)));
        return acknowledgedResponse(response.acknowledged(), response.shardsAcknowledged(),
                ElasticsearchOperation.OPEN_INDEX, "打开索引成功", validatedIndexName);
    }

    /**
     * 关闭索引。
     *
     * @param indexName 索引名称
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<Void> closeIndex(String indexName) throws IOException {
        final String validatedIndexName = requireIndexName(indexName);
        CloseIndexResponse response = getClient().indices().close(CloseIndexRequest.of(request -> request
                .index(validatedIndexName)));
        return acknowledgedResponse(response.acknowledged(), response.shardsAcknowledged(),
                ElasticsearchOperation.CLOSE_INDEX, "关闭索引成功", validatedIndexName);
    }

    /**
     * 更新索引 Mapping。
     *
     * @param request 自定义 Mapping 请求
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<Void> putMapping(ElasticsearchIndexMappingRequest request) throws IOException {
        Objects.requireNonNull(request, "更新索引 Mapping 请求不能为空");
        String indexName = requireIndexName(request.getIndexName());
        PutMappingRequest.Builder builder = new PutMappingRequest.Builder().index(indexName);
        applyJsonBody(builder, request.getMappings());
        PutMappingResponse response = getClient().indices().putMapping(builder.build());
        return acknowledgedResponse(response.acknowledged(), null,
                ElasticsearchOperation.PUT_MAPPING, "更新索引 Mapping 成功", indexName);
    }

    /**
     * 获取索引 Mapping 的可视化 JSON。
     *
     * @param indexName 索引名称
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<String> getMapping(String indexName) throws IOException {
        final String validatedIndexName = requireIndexName(indexName);
        GetMappingResponse response = getClient().indices().getMapping(GetMappingRequest.of(request -> request
                .index(validatedIndexName)));
        return ElasticsearchResponse.<String>builder()
                .success(true)
                .operation(ElasticsearchOperation.GET_MAPPING)
                .message("获取索引 Mapping 成功")
                .indexName(validatedIndexName)
                .data(toJson(response.result()))
                .build();
    }

    /**
     * 更新索引 Settings。
     *
     * @param request 自定义 Settings 请求
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<Void> putSettings(ElasticsearchIndexSettingsRequest request) throws IOException {
        Objects.requireNonNull(request, "更新索引 Settings 请求不能为空");
        String indexName = requireIndexName(request.getIndexName());
        PutIndicesSettingsRequest.Builder builder = new PutIndicesSettingsRequest.Builder().index(indexName);
        applyJsonBody(builder, request.getSettings());
        PutIndicesSettingsResponse response = getClient().indices().putSettings(builder.build());
        return acknowledgedResponse(response.acknowledged(), null, ElasticsearchOperation.PUT_SETTINGS,
                "更新索引 Settings 成功", indexName);
    }

    /**
     * 获取索引 Settings 的可视化 JSON。
     *
     * @param indexName 索引名称
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<String> getSettings(String indexName) throws IOException {
        final String validatedIndexName = requireIndexName(indexName);
        GetIndicesSettingsResponse response = getClient().indices().getSettings(GetIndicesSettingsRequest.of(request -> request
                .index(validatedIndexName)));
        return ElasticsearchResponse.<String>builder()
                .success(true)
                .operation(ElasticsearchOperation.GET_SETTINGS)
                .message("获取索引 Settings 成功")
                .indexName(validatedIndexName)
                .data(toJson(response.result()))
                .build();
    }

    /**
     * 创建或更新索引别名。
     *
     * @param request 自定义别名请求
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<Void> putAlias(ElasticsearchIndexAliasRequest request) throws IOException {
        Objects.requireNonNull(request, "创建索引别名请求不能为空");
        String indexName = requireIndexName(request.getIndexName());
        String aliasName = requireIndexName(request.getAliasName());
        PutAliasResponse response = getClient().indices().putAlias(PutAliasRequest.of(aliasRequest -> aliasRequest
                .index(indexName)
                .name(aliasName)));
        return acknowledgedResponse(response.acknowledged(), null, ElasticsearchOperation.PUT_ALIAS,
                "创建索引别名成功", indexName);
    }

    /**
     * 删除索引别名。
     *
     * @param request 自定义别名请求
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<Void> deleteAlias(ElasticsearchIndexAliasRequest request) throws IOException {
        Objects.requireNonNull(request, "删除索引别名请求不能为空");
        String indexName = requireIndexName(request.getIndexName());
        String aliasName = requireIndexName(request.getAliasName());
        DeleteAliasResponse response = getClient().indices().deleteAlias(DeleteAliasRequest.of(aliasRequest -> aliasRequest
                .index(indexName)
                .name(aliasName)));
        return acknowledgedResponse(response.acknowledged(), null, ElasticsearchOperation.DELETE_ALIAS,
                "删除索引别名成功", indexName);
    }

    /**
     * 判断索引别名是否存在。
     *
     * @param request 自定义别名请求
     * @return 统一别名存在性响应
     * @throws IOException Elasticsearch 请求异常
     */
    @Override
    public ElasticsearchResponse<Boolean> existsAlias(ElasticsearchIndexAliasRequest request) throws IOException {
        Objects.requireNonNull(request, "判断索引别名存在性请求不能为空");
        String indexName = requireIndexName(request.getIndexName());
        String aliasName = requireIndexName(request.getAliasName());
        BooleanResponse response = getClient().indices().existsAlias(ExistsAliasRequest.of(aliasRequest -> aliasRequest
                .index(indexName)
                .name(aliasName)));
        return ElasticsearchResponse.<Boolean>builder()
                .success(true)
                .operation(ElasticsearchOperation.EXISTS_ALIAS)
                .message(response.value() ? "索引别名存在" : "索引别名不存在")
                .indexName(indexName)
                .data(response.value())
                .found(response.value())
                .build();
    }

    private void applyJsonBody(WithJsonObjectBuilderBase<?> builder, Object body) throws IOException {
        if (body == null) {
            return;
        }
        String json = toJson(body);
        builder.withJson(new StringReader(json));
    }

    private String toJson(Object value) throws IOException {
        JsonpMapper mapper = getClient()._transport().jsonpMapper();
        StringWriter writer = new StringWriter();
        var generator = mapper.jsonProvider().createGenerator(writer);
        mapper.serialize(value == null ? Map.of() : value, generator);
        generator.close();
        return writer.toString();
    }

    private ElasticsearchResponse<Void> acknowledgedResponse(boolean acknowledged, Boolean shardsAcknowledged,
                                                              ElasticsearchOperation operation, String message,
                                                              String indexName) {
        return ElasticsearchResponse.<Void>builder()
                .success(acknowledged)
                .operation(operation)
                .message(acknowledged ? message : operation.getValue() + "未确认")
                .indexName(indexName)
                .acknowledged(acknowledged)
                .shardsAcknowledged(shardsAcknowledged)
                .build();
    }

    private ElasticsearchResponse<Void> shardResponse(boolean success, ElasticsearchOperation operation, String message,
                                                       String indexName) {
        return ElasticsearchResponse.<Void>builder()
                .success(success)
                .operation(operation)
                .message(success ? message : operation.getValue() + "失败")
                .indexName(indexName)
                .build();
    }

    private String requireIndexName(String indexName) {
        if (indexName == null || indexName.trim().isEmpty()) {
            throw new IllegalArgumentException("索引名称不能为空");
        }
        return indexName;
    }

    private static final class ExistsRequestFactory {

        private ExistsRequestFactory() {
        }

        private static co.elastic.clients.elasticsearch.indices.ExistsRequest of(String indexName) {
            return co.elastic.clients.elasticsearch.indices.ExistsRequest.of(request -> request.index(indexName));
        }
    }
}
