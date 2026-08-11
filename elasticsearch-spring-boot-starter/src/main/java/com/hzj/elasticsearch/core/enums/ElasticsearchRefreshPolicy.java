package com.hzj.elasticsearch.core.enums;

/**
 * Elasticsearch 写入后刷新策略。
 */
public enum ElasticsearchRefreshPolicy {

    /**
     * 使用 Elasticsearch 默认策略。
     */
    NONE("false"),

    /**
     * 写入完成后立即刷新。
     */
    IMMEDIATE("true"),

    /**
     * 等待下一次刷新后返回。
     */
    WAIT_UNTIL("wait_for");

    private final String value;

    ElasticsearchRefreshPolicy(String value) {
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
