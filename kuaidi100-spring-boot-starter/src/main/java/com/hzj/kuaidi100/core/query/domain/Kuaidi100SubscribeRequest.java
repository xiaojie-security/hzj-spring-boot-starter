package com.hzj.kuaidi100.core.query.domain;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 快递100物流信息订阅请求。
 */
@Data
public class Kuaidi100SubscribeRequest {

    /** 快递公司编码，智能判断开启时可为空。 */
    private String company;

    /** 快递单号。 */
    private String number;

    /** 出发地。 */
    private String from;

    /** 目的地。 */
    private String to;

    /** 回调地址。 */
    private String callbackUrl;

    /** 回调签名随机盐。 */
    private String salt;

    /** 收寄件人手机号。 */
    private String phone;

    /** 结果增强版本。 */
    private String resultv2;

    /** 官方新增字段扩展。 */
    private Map<String, Object> extraParameters = new LinkedHashMap<>();

    /**
     * 转换为订阅 param 主体。
     *
     * @param key 授权key
     * @param intelligentJudgment 是否启用智能单号判断
     * @return 有序参数
     */
    public Map<String, Object> toParameters(String key, boolean intelligentJudgment) {
        Map<String, Object> parameters = new LinkedHashMap<>();
        put(parameters, "company", intelligentJudgment ? null : company);
        put(parameters, "number", number);
        put(parameters, "from", from);
        put(parameters, "to", to);
        put(parameters, "key", key);
        Map<String, Object> subscriptionParameters = new LinkedHashMap<>();
        put(subscriptionParameters, "callbackurl", callbackUrl);
        put(subscriptionParameters, "salt", salt);
        put(subscriptionParameters, "phone", phone);
        put(subscriptionParameters, "resultv2", resultv2);
        if (intelligentJudgment) {
            subscriptionParameters.put("autoCom", "1");
        }
        if (extraParameters != null) {
            subscriptionParameters.putAll(extraParameters);
        }
        parameters.put("parameters", subscriptionParameters);
        return parameters;
    }

    private void put(Map<String, Object> parameters, String name, Object value) {
        if (value != null && (!(value instanceof String) || !((String) value).trim().isEmpty())) {
            parameters.put(name, value);
        }
    }
}
