package com.hzj.elasticsearch.utils;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.hzj.elasticsearch.core.document.entity.ElasticsearchQuery;
import com.hzj.elasticsearch.core.entity.enums.ElasticsearchRefreshPolicy;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 将 Starter 自定义查询转换为 Elasticsearch 官方查询对象。
 *
 * <p>该类只在实现层使用，官方 Query 类型不会出现在 Starter 公开接口中。</p>
 */
public final class ElasticsearchQueryConverter {

    private ElasticsearchQueryConverter() {
    }

    /**
     * 转换查询条件。
     *
     * @param source 自定义查询条件
     * @return 官方查询对象
     */
    public static Query convert(ElasticsearchQuery source) {
        if (source == null || source.getType() == null) {
            return Query.of(query -> query.matchAll(matchAll -> matchAll));
        }
        return switch (source.getType()) {
            case MATCH_ALL -> Query.of(query -> query.matchAll(matchAll -> matchAll));
            case TERM -> Query.of(query -> query.term(term -> term
                    .field(requireText(source.getField(), "term 查询字段不能为空"))
                    .value(toFieldValue(source.getValue()))));
            case MATCH -> Query.of(query -> query.match(match -> match
                    .field(requireText(source.getField(), "match 查询字段不能为空"))
                    .query(toFieldValue(source.getValue()))));
            case EXISTS -> Query.of(query -> query.exists(exists -> exists
                    .field(requireText(source.getField(), "exists 查询字段不能为空"))));
            case IDS -> Query.of(query -> query.ids(ids -> ids
                    .values(Objects.requireNonNull(source.getValues(), "ids 查询值不能为空"))));
            case QUERY_STRING -> Query.of(query -> query.queryString(queryString -> queryString
                    .query(requireText(source.getQueryString(), "query_string 查询内容不能为空"))));
            case BOOL -> Query.of(query -> query.bool(bool -> bool
                    .must(convertList(source.getMust()))
                    .filter(convertList(source.getFilter()))
                    .should(convertList(source.getShould()))
                    .mustNot(convertList(source.getMustNot()))));
        };
    }

    /**
     * 转换刷新策略。
     *
     * @param policy Starter 刷新策略
     * @return 官方刷新策略
     */
    public static Refresh convertRefresh(ElasticsearchRefreshPolicy policy) {
        if (policy == null || policy == ElasticsearchRefreshPolicy.NONE) {
            return Refresh.False;
        }
        return policy == ElasticsearchRefreshPolicy.IMMEDIATE ? Refresh.True : Refresh.WaitFor;
    }

    private static List<Query> convertList(List<ElasticsearchQuery> queries) {
        if (queries == null || queries.isEmpty()) {
            return Collections.emptyList();
        }
        return queries.stream().map(ElasticsearchQueryConverter::convert).toList();
    }

    private static FieldValue toFieldValue(Object value) {
        if (value == null) {
            return FieldValue.NULL;
        }
        if (value instanceof Boolean booleanValue) {
            return FieldValue.of(booleanValue);
        }
        if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
            return FieldValue.of(((Number) value).longValue());
        }
        if (value instanceof Number number) {
            return FieldValue.of(number.doubleValue());
        }
        return FieldValue.of(String.valueOf(value));
    }

    private static String requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
