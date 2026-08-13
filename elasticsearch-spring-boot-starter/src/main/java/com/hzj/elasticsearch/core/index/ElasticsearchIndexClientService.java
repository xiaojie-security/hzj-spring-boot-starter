package com.hzj.elasticsearch.core.index;

import com.hzj.elasticsearch.core.client.ElasticsearchClientService;
import com.hzj.elasticsearch.core.entity.ElasticsearchResponse;
import com.hzj.elasticsearch.core.index.entity.ElasticsearchIndexAliasRequest;
import com.hzj.elasticsearch.core.index.entity.ElasticsearchIndexCreateRequest;
import com.hzj.elasticsearch.core.index.entity.ElasticsearchIndexMappingRequest;
import com.hzj.elasticsearch.core.index.entity.ElasticsearchIndexSettingsRequest;

import java.io.IOException;

/**
 * Elasticsearch 索引级业务操作服务。
 *
 * <p>ES8 中索引直接对应业务表，不存在 type 中间层；公开接口不暴露 ES 官方请求和响应类型。</p>
 */
public interface ElasticsearchIndexClientService extends ElasticsearchClientService {

    /**
     * 创建索引。
     *
     * @param request 自定义索引创建请求
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<Void> createIndex(ElasticsearchIndexCreateRequest request) throws IOException;

    /**
     * 使用索引名称创建索引。
     *
     * @param indexName 索引名称
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<Void> createIndex(String indexName) throws IOException;

    /**
     * 删除索引。
     *
     * @param indexName 索引名称
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<Void> deleteIndex(String indexName) throws IOException;

    /**
     * 判断索引是否存在。
     *
     * @param indexName 索引名称
     * @return 统一索引存在性响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<Boolean> existsIndex(String indexName) throws IOException;

    /**
     * 获取索引基本信息。
     *
     * @param indexName 索引名称
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<String> getIndex(String indexName) throws IOException;

    /**
     * 刷新索引。
     *
     * @param indexName 索引名称
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<Void> refreshIndex(String indexName) throws IOException;

    /**
     * 打开索引。
     *
     * @param indexName 索引名称
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<Void> openIndex(String indexName) throws IOException;

    /**
     * 关闭索引。
     *
     * @param indexName 索引名称
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<Void> closeIndex(String indexName) throws IOException;

    /**
     * 更新索引 Mapping。
     *
     * @param request 自定义 Mapping 请求
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<Void> putMapping(ElasticsearchIndexMappingRequest request) throws IOException;

    /**
     * 获取索引 Mapping 的可视化 JSON 文本。
     *
     * @param indexName 索引名称
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<String> getMapping(String indexName) throws IOException;

    /**
     * 更新索引 Settings。
     *
     * @param request 自定义 Settings 请求
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<Void> putSettings(ElasticsearchIndexSettingsRequest request) throws IOException;

    /**
     * 获取索引 Settings 的可视化 JSON 文本。
     *
     * @param indexName 索引名称
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<String> getSettings(String indexName) throws IOException;

    /**
     * 创建或更新索引别名。
     *
     * @param request 自定义别名请求
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<Void> putAlias(ElasticsearchIndexAliasRequest request) throws IOException;

    /**
     * 删除索引别名。
     *
     * @param request 自定义别名请求
     * @return 统一索引响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<Void> deleteAlias(ElasticsearchIndexAliasRequest request) throws IOException;

    /**
     * 判断索引别名是否存在。
     *
     * @param request 自定义别名请求
     * @return 统一别名存在性响应
     * @throws IOException Elasticsearch 请求异常
     */
    ElasticsearchResponse<Boolean> existsAlias(ElasticsearchIndexAliasRequest request) throws IOException;
}
