package com.hzj.wechat.core.payment.domain;

import com.hzj.wechat.core.payment.enums.WechatVirtualPaymentApi;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询批量发布道具任务请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatQueryPublishGoodsRequest extends WechatVirtualPaymentRequest {
    public WechatQueryPublishGoodsRequest() { super(WechatVirtualPaymentApi.QUERY_PUBLISH_GOODS); }
}
