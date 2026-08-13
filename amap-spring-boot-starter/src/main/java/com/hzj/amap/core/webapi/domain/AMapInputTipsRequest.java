package com.hzj.amap.core.webapi.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 输入提示请求参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AMapInputTipsRequest extends AMapWebApiRequest {

    /**
     * 查询关键词。
     */
    private String keywords;

    /**
     * 指定查询城市。
     */
    private String city;

    /**
     * 指定输入类型。
     */
    private String type;

    /**
     * 是否仅返回指定城市结果。
     */
    private Boolean citylimit;

    /**
     * 创建输入提示请求。
     */
    public AMapInputTipsRequest() {
        super("https://restapi.amap.com/v3/assistant/inputtips");
    }

    @Override
    public Map<String, String> toQueryParameters() {
        Map<String, String> parameters = createParameters();
        putIfNotBlank(parameters, "keywords", keywords);
        putIfNotBlank(parameters, "city", city);
        putIfNotBlank(parameters, "type", type);
        putIfNotBlank(parameters, "citylimit", citylimit);
        return parameters;
    }
}
