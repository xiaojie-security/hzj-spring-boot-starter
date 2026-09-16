package com.hzj.kuaidi100.core.common;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 快递100企业接口通用请求参数。
 *
 * <p>官方接口的增值参数更新频率较高，使用有序扩展参数保留官方字段顺序，同时避免 starter 绑定某一个版本的完整字段集合。</p>
 */
@Data
public class Kuaidi100EnterpriseRequest {

    /** 企业接口业务类型。 */
    private String method;

    /** 接口 param 主体。 */
    private Map<String, Object> parameters = new LinkedHashMap<>();

    /**
     * 写入一个接口参数。
     *
     * @param name 参数名
     * @param value 参数值
     * @return 当前请求
     */
    public Kuaidi100EnterpriseRequest put(String name, Object value) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("快递100参数名不能为空");
        }
        if (value != null) {
            parameters.put(name, value);
        }
        return this;
    }

    /**
     * 获取有序请求参数副本。
     *
     * @return 请求参数副本
     */
    public Map<String, Object> copyParameters() {
        return new LinkedHashMap<>(parameters == null ? new LinkedHashMap<>() : parameters);
    }
}
