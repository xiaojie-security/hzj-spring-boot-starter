package com.hzj.elasticsearch.core.document.enums;

/**
 * 常用 Elasticsearch 查询类型。
 */
public enum ElasticsearchQueryType {
    MATCH_ALL,
    TERM,
    MATCH,
    EXISTS,
    IDS,
    QUERY_STRING,
    BOOL
}
