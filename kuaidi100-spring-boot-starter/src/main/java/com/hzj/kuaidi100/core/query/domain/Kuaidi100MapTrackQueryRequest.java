package com.hzj.kuaidi100.core.query.domain;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 快递100地图轨迹查询请求。
 */
@Data
public class Kuaidi100MapTrackQueryRequest {

    /** 快递公司编码。 */
    private String com;

    /** 快递单号。 */
    private String num;

    /** 收寄件人手机号。 */
    private String phone;

    /** 出发地。 */
    private String from;

    /** 目的地。 */
    private String to;

    /** 地图轨迹结果增强版本。 */
    private String resultv2;

    /** 返回格式。 */
    private String show;

    /** 地图轨迹模板标识。 */
    private String mapConfigKey;

    /** 轨迹排序方式。 */
    private String order;

    /** 是否提取快递员信息。 */
    private Boolean needCourierInfo;

    /** 官方新增字段扩展。 */
    private Map<String, Object> extraParameters = new LinkedHashMap<>();

    /**
     * 转换为快递100 param 主体。
     *
     * @return 有序参数
     */
    public Map<String, Object> toParameters() {
        Map<String, Object> parameters = new LinkedHashMap<>();
        put(parameters, "com", com);
        put(parameters, "num", num);
        put(parameters, "phone", phone);
        put(parameters, "from", from);
        put(parameters, "to", to);
        put(parameters, "resultv2", resultv2);
        put(parameters, "show", show);
        put(parameters, "mapConfigKey", mapConfigKey);
        put(parameters, "order", order);
        put(parameters, "needCourierInfo", needCourierInfo);
        if (extraParameters != null) {
            parameters.putAll(extraParameters);
        }
        return parameters;
    }

    private void put(Map<String, Object> parameters, String name, Object value) {
        if (value != null && (!(value instanceof String) || !((String) value).trim().isEmpty())) {
            parameters.put(name, value);
        }
    }
}
