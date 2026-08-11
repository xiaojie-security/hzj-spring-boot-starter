package com.hzj.elasticsearch.core.document.entity.enums;

import java.util.Arrays;

/**
 * Elasticsearch 文档写入结果。
 */
public enum ElasticsearchDocumentWriteResult {

    CREATED("created"),
    UPDATED("updated"),
    DELETED("deleted"),
    NOT_FOUND("not_found"),
    NOOP("noop"),
    UNKNOWN("unknown");

    private final String value;

    ElasticsearchDocumentWriteResult(String value) {
        this.value = value;
    }

    /**
     * 根据 Elasticsearch 返回值获取枚举。
     *
     * @param value Elasticsearch 返回值
     * @return 文档写入结果
     */
    public static ElasticsearchDocumentWriteResult fromValue(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(result -> result.value.equals(value))
                .findFirst()
                .orElse(UNKNOWN);
    }

    /**
     * 获取 Elasticsearch 协议值。
     *
     * @return 协议值
     */
    public String getValue() {
        return value;
    }
}
