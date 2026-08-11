package com.hzj.elasticsearch.core.document.enums;

import java.util.Locale;

/**
 * Bulk 文档操作类型。
 */
public enum ElasticsearchBulkOperationType {
    INDEX,
    CREATE,
    UPDATE,
    DELETE;

    /**
     * 根据 Elasticsearch 返回值获取枚举。
     *
     * @param value Elasticsearch 返回值
     * @return Bulk 操作类型
     */
    public static ElasticsearchBulkOperationType fromValue(String value) {
        return value == null ? null : valueOf(value.toUpperCase(Locale.ROOT));
    }
}
