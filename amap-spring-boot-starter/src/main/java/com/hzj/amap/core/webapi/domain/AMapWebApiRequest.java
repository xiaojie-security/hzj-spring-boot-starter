package com.hzj.amap.core.webapi.domain;

import com.hzj.amap.core.enums.AMapHttpMethod;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 高德 Web 服务 API 请求基础参数。
 */
@Data
public abstract class AMapWebApiRequest {

    /**
     * 高德 Web 服务请求地址。
     */
    private String requestUrl;

    /**
     * 高德 Web 服务请求方法。
     */
    private AMapHttpMethod requestMethod = AMapHttpMethod.GET;

    /**
     * 使用默认请求地址创建请求参数。
     *
     * @param requestUrl 默认请求地址
     */
    protected AMapWebApiRequest(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    /**
     * 转换为高德接口请求参数。
     *
     * @return 请求参数
     */
    public abstract Map<String, String> toQueryParameters();

    /**
     * 写入非空请求参数。
     *
     * @param parameters 请求参数容器
     * @param key 参数名
     * @param value 参数值
     */
    protected void putIfNotBlank(Map<String, String> parameters, String key, Object value) {
        if (value != null && !value.toString().trim().isEmpty()) {
            parameters.put(key, value.toString());
        }
    }

    /**
     * 创建有序请求参数容器。
     *
     * @return 请求参数容器
     */
    protected Map<String, String> createParameters() {
        return new LinkedHashMap<>();
    }
}
