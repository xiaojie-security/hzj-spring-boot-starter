package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信虚拟支付请求公共参数。
 */
@Data
@NoArgsConstructor
public class WechatVirtualPaymentRequest {

    /**
     * 用户态签名，仅作为查询参数发送，不会进入 JSON 请求体。
     */
    @Expose(serialize = false, deserialize = false)
    private String signature;

    /**
     * 支付态签名，仅作为查询参数发送，不会进入 JSON 请求体。
     */
    @Expose(serialize = false, deserialize = false)
    private String paySig;

    /**
     * 虚拟支付环境，0 表示正式环境，1 表示沙箱环境。
     */
    private Integer env;
}
