package com.hzj.elasticsearch.core.document.entity;

import com.hzj.elasticsearch.core.document.enums.ElasticsearchQueryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 常用 Elasticsearch 查询条件。
 *
 * <p>该对象屏蔽官方 Query Builder；复杂查询可以通过组合 bool 查询完成。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticsearchQuery {

    /**
     * 查询类型。
     */
    private ElasticsearchQueryType type;

    /**
     * 查询字段。
     */
    private String field;

    /**
     * 单值查询值。
     */
    private Object value;

    /**
     * ID 查询值列表。
     */
    private List<String> values;

    /**
     * 查询字符串。
     */
    private String queryString;

    /**
     * bool must 子查询。
     */
    @Builder.Default
    private List<ElasticsearchQuery> must = Collections.emptyList();

    /**
     * bool filter 子查询。
     */
    @Builder.Default
    private List<ElasticsearchQuery> filter = Collections.emptyList();

    /**
     * bool should 子查询。
     */
    @Builder.Default
    private List<ElasticsearchQuery> should = Collections.emptyList();

    /**
     * bool must_not 子查询。
     */
    @Builder.Default
    private List<ElasticsearchQuery> mustNot = Collections.emptyList();

    /**
     * 创建匹配全部查询。
     *
     * @return 查询条件
     */
    public static ElasticsearchQuery matchAll() {
        return ElasticsearchQuery.builder().type(ElasticsearchQueryType.MATCH_ALL).build();
    }

    /**
     * 创建精确值查询。
     *
     * @param field 字段
     * @param value 值
     * @return 查询条件
     */
    public static ElasticsearchQuery term(String field, Object value) {
        return ElasticsearchQuery.builder().type(ElasticsearchQueryType.TERM).field(field).value(value).build();
    }

    /**
     * 创建全文匹配查询。
     *
     * @param field 字段
     * @param value 值
     * @return 查询条件
     */
    public static ElasticsearchQuery match(String field, Object value) {
        return ElasticsearchQuery.builder().type(ElasticsearchQueryType.MATCH).field(field).value(value).build();
    }

    /**
     * 创建字段存在查询。
     *
     * @param field 字段
     * @return 查询条件
     */
    public static ElasticsearchQuery exists(String field) {
        return ElasticsearchQuery.builder().type(ElasticsearchQueryType.EXISTS).field(field).build();
    }

    /**
     * 创建 ID 查询。
     *
     * @param values ID 列表
     * @return 查询条件
     */
    public static ElasticsearchQuery ids(List<String> values) {
        return ElasticsearchQuery.builder().type(ElasticsearchQueryType.IDS).values(values).build();
    }

    /**
     * 创建查询字符串条件。
     *
     * @param queryString 查询字符串
     * @return 查询条件
     */
    public static ElasticsearchQuery queryString(String queryString) {
        return ElasticsearchQuery.builder().type(ElasticsearchQueryType.QUERY_STRING).queryString(queryString).build();
    }

    /**
     * 创建 bool 查询。
     *
     * @param must must 子查询
     * @param filter filter 子查询
     * @return 查询条件
     */
    public static ElasticsearchQuery bool(List<ElasticsearchQuery> must, List<ElasticsearchQuery> filter) {
        return ElasticsearchQuery.builder().type(ElasticsearchQueryType.BOOL).must(must).filter(filter).build();
    }
}
