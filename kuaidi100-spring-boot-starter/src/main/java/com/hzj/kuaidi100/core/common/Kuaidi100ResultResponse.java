package com.hzj.kuaidi100.core.common;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * 快递100订阅及订单导入类接口通用响应。
 */
@Data
public class Kuaidi100ResultResponse {

    /** 是否成功。 */
    private Boolean result;

    /** 返回编码，兼容数字或字符串。 */
    private JsonNode returnCode;

    /** 返回消息。 */
    private String message;

    /** 业务数据。 */
    private JsonNode data;

    /** 原始响应。 */
    private JsonNode raw;
}
