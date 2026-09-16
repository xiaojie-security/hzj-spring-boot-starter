package com.hzj.kuaidi100.core.common;

import lombok.Data;

/**
 * 快递100订阅接口响应。
 */
@Data
public class Kuaidi100SubscriptionResponse {

    /** 是否提交成功。 */
    private Boolean result;

    /** 返回编码。 */
    private String returnCode;

    /** 返回消息。 */
    private String message;
}
