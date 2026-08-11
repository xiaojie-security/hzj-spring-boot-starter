package com.hzj.elasticsearch.core.entity.enums;

/**
 * Elasticsearch Starter 支持的业务操作。
 */
public enum ElasticsearchOperation {

    SAVE_DOCUMENT("saveDocument"),
    CREATE_DOCUMENT("createDocument"),
    GET_DOCUMENT("getDocument"),
    EXISTS_DOCUMENT("existsDocument"),
    DELETE_DOCUMENT("deleteDocument"),
    UPDATE_DOCUMENT("updateDocument"),
    SEARCH_DOCUMENTS("searchDocuments"),
    COUNT_DOCUMENTS("countDocuments"),
    BULK_DOCUMENTS("bulkDocuments"),
    DELETE_DOCUMENTS_BY_QUERY("deleteDocumentsByQuery"),
    UPDATE_DOCUMENTS_BY_QUERY("updateDocumentsByQuery"),
    MULTI_GET_DOCUMENTS("multiGetDocuments"),
    CREATE_INDEX("createIndex"),
    DELETE_INDEX("deleteIndex"),
    EXISTS_INDEX("existsIndex"),
    GET_INDEX("getIndex"),
    REFRESH_INDEX("refreshIndex"),
    OPEN_INDEX("openIndex"),
    CLOSE_INDEX("closeIndex"),
    PUT_MAPPING("putMapping"),
    GET_MAPPING("getMapping"),
    PUT_SETTINGS("putSettings"),
    GET_SETTINGS("getSettings"),
    PUT_ALIAS("putAlias"),
    DELETE_ALIAS("deleteAlias"),
    EXISTS_ALIAS("existsAlias");

    private final String value;

    ElasticsearchOperation(String value) {
        this.value = value;
    }

    /**
     * 获取操作标识。
     *
     * @return 操作标识
     */
    public String getValue() {
        return value;
    }
}
