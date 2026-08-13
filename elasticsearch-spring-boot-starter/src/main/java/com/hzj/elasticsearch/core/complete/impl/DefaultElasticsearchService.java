package com.hzj.elasticsearch.core.complete.impl;

import com.hzj.elasticsearch.core.complete.ElasticsearchService;
import com.hzj.elasticsearch.core.document.ElasticsearchDocumentService;
import com.hzj.elasticsearch.core.index.ElasticsearchIndexService;
import lombok.experimental.Delegate;

public class DefaultElasticsearchService implements ElasticsearchService {

    @Delegate
    private final ElasticsearchIndexService indexService;

    @Delegate
    private final ElasticsearchDocumentService documentService;

    public DefaultElasticsearchService(ElasticsearchIndexService indexService,
                                       ElasticsearchDocumentService documentService) {
        this.indexService = indexService;
        this.documentService = documentService;
    }
}
