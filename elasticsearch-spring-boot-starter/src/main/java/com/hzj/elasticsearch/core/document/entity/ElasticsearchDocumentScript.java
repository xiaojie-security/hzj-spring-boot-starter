package com.hzj.elasticsearch.core.document.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 自定义 Painless 脚本。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticsearchDocumentScript {

    /**
     * 脚本源代码。
     */
    private String source;

    /**
     * 脚本语言，默认使用 painless。
     */
    @Builder.Default
    private String language = "painless";

    /**
     * 脚本参数。
     */
    private Map<String, Object> parameters;
}
