package com.hzj.elasticsearch.core.entity;

/**
 * Elasticsearch 按查询操作的版本冲突处理策略。
 */
public enum ElasticsearchConflictPolicy {

    /**
     * 遇到版本冲突时终止操作。
     */
    ABORT("abort"),

    /**
     * 遇到版本冲突时跳过冲突文档并继续操作。
     */
    PROCEED("proceed");

    private final String value;

    ElasticsearchConflictPolicy(String value) {
        this.value = value;
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
