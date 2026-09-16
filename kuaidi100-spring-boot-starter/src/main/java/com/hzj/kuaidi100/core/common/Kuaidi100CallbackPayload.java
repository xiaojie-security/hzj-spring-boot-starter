package com.hzj.kuaidi100.core.common;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * 快递100回调请求的统一解析结果。
 */
@Data
public class Kuaidi100CallbackPayload {

    /** 回调签名。 */
    private String sign;

    /** 任务标识。 */
    private String taskId;

    /** 打印状态等回调类型。 */
    private String pushType;

    /** OCR 或异常回调类型。 */
    private String type;

    /** 回调主体。 */
    private JsonNode param;
}
