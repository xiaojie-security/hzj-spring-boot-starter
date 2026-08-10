package com.hzj.elasticsearch.provider.es.enums;

/**
 * Elasticsearch 连接协议。
 */
public enum ElasticsearchScheme {

    /** HTTP 协议。 */
    HTTP("http"),

    /** HTTPS 协议。 */
    HTTPS("https");

    private final String value;

    ElasticsearchScheme(String value) {
        this.value = value;
    }

    /**
     * 获取协议字符串。
     *
     * @return 协议字符串
     */
    public String getValue() {
        return value;
    }
}
