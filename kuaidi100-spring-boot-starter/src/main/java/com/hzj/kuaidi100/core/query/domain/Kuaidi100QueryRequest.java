package com.hzj.kuaidi100.core.query.domain;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 快递100实时查询请求。
 */
@Data
public class Kuaidi100QueryRequest {

    /** 快递公司编码，智能判断开启时可为空。 */
    private String com;

    /** 快递单号。 */
    private String num;

    /** 收寄件人手机号。 */
    private String phone;

    /** 出发地。 */
    private String from;

    /** 目的地。 */
    private String to;

    /** 结果增强版本。 */
    private String resultv2;

    /** 返回格式。 */
    private String show;

    /** 轨迹排序方式。 */
    private String order;

    /** 返回语言。 */
    private String lang;

    /** 是否提取快递员信息。 */
    private Boolean needCourierInfo;

    /** 签名类型，默认 MD5。 */
    private String signType;

    /** 官方新增字段扩展。 */
    private Map<String, Object> extraParameters = new LinkedHashMap<>();

    /**
     * 转换为快递100 param 主体。
     *
     * @param intelligentJudgment 是否启用智能单号判断
     * @return 有序参数
     */
    public Map<String, Object> toParameters(boolean intelligentJudgment) {
        Map<String, Object> parameters = new LinkedHashMap<>();
        if (!intelligentJudgment || !isBlank(com)) {
            put(parameters, "com", com);
        }
        put(parameters, "num", num);
        put(parameters, "phone", phone);
        put(parameters, "from", from);
        put(parameters, "to", to);
        put(parameters, "resultv2", resultv2);
        put(parameters, "show", show);
        put(parameters, "order", order);
        put(parameters, "lang", lang);
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

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
