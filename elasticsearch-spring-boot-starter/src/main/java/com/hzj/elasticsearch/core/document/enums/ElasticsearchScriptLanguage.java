package com.hzj.elasticsearch.core.document.enums;

/**
 * Elasticsearch 内置脚本语言。
 */
public enum ElasticsearchScriptLanguage {

    PAINLESS("painless"),
    MUSTACHE("mustache"),
    EXPRESSION("expression");

    private final String value;

    ElasticsearchScriptLanguage(String value) {
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
